

import java.lang.*;
import java.util.*;

public class GetPreciseSensitivityFromGui {

    private static HashSet<Float> allPossibleSensitivies = new HashSet<Float>();

    static class Screen {

        int width;
        int height;
        int scaledWidth;
        int scaledHeight;
        boolean forceUnicodeFont;

        public Screen(int width, int height, boolean forceUnicodeFont) {

            this.width = width;
            this.height = height;
            this.forceUnicodeFont = forceUnicodeFont;
            int scaleFactor = calculateScaleFactor();
            scaledWidth = (int)Math.ceil((double)width / scaleFactor);
            scaledHeight = (int)Math.ceil((double)height / scaleFactor);
        }

        private int calculateScaleFactor() {
            int scale = 1;
            for (scale = 1; scale < width && scale < height && width / (scale + 1) >= 320 && height / (scale + 1) >= 240; ++scale) {}
            if (forceUnicodeFont && scale % 2 != 0) {
                ++scale;
            }
            return scale;
        }
    }

    /**
    We should be able to pick any value

    the box starts at
        floor(window width / 2)-155
    left box starts at
        floor(window width / 2)-155+160
    box width is 150
    these are a function of SCALED window width

    we can click on any UNSCALED window width pixel

    this is then converted to the SCALED coordinate like so:
    (x/unscaled_width)*scaled_width
    this value is not rounded
    scaled_width = unscaled_width / scale_factor

    the calculateScaleFactor function calculates the scale factor. i think guiScale = 0 for auto.
     */

    private static double calcMinIncForSens(double sens) {

        double f = sens * (double)0.6f + (double)0.2f;
        double g = f * f * f;
        double h = g * 8.0;
        double o = 1.0 * h;
        double minInc = o * 0.15;

        return minInc;
    }

    private static double calcCastingErrorForSens(double sens) {

        float prevYaw = 0.0f;

        double f = sens * (double)0.6f + (double)0.2f;
        double g = f * f * f;
        double h = g * 8.0;
        double o = 1.0 * h;
        double angle = o * 0.15;
        float yaw = (float)((double)prevYaw + angle);

        return Math.abs(angle-yaw);
    }

    private static double getSensFromRawClickCoord(Screen screen, double clickCoord) {

        double scaledClickSpot = (double)(clickCoord/screen.width)*((double)screen.scaledWidth);
        return getSensFromScaledClickCoord(screen, scaledClickSpot);
    }

    private static double getSensFromScaledClickCoord(Screen screen, double clickCoord) {

        int boxStart = (screen.scaledWidth / 2) - 155;
        double boxPercentage = (clickCoord - (boxStart+4))/142.0;

        if (boxPercentage < 0.0) {

            boxPercentage = 0.0;
        } else if (boxPercentage > 1.0) {

            boxPercentage = 1.0;
        }

        return boxPercentage;
    }

    private static void auditionSens(double sens) {

        allPossibleSensitivies.add((float)sens);
    }

    private static void runTrialForParams(int width, int height, boolean textValue) {

        Screen screen = new Screen(height, width, textValue);

        for (int clickCoord = 0; clickCoord < width; clickCoord++) {

            double baseSens = getSensFromRawClickCoord(screen, clickCoord);
            auditionSens(baseSens);
            double upSens = baseSens;
            while (upSens < 1.0) {

                upSens += 1.0/142.0;
                auditionSens(upSens);
            }
            double downSens = baseSens;
            while (downSens > 0.0) {

                downSens -= 1.0/142.0;
                auditionSens(downSens);
            }
        }
    }

    public static void main(String[] args) {

        int maxSize = 300;

        boolean[] posTextValues = new boolean[2];
        posTextValues[1] = true;

        for (int height = 100; height < maxSize; height++) {

            for (int width = 100; width < maxSize; width++) {

                for (int i = 0; i < posTextValues.length; i++) {

                    boolean textVal = posTextValues[i];

                    runTrialForParams(width, height, textVal);
                }
            }
        }

        System.out.println(maxSize-100);
        System.out.println(allPossibleSensitivies.size());
    }
}