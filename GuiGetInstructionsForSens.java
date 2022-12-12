

import java.lang.*;
import java.util.*;
import java.io.*;

public class GetPreciseSensitivityFromGui {

    private static HashSet<Float> allPossibleSensitivies = new HashSet<Float>();
    private static HashMap<Float, String> instructionSet = new HashMap<Float, String>();

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

    private static void auditionSens(Screen screen, int clickCoord, double sens) {

        auditionSens(screen, clickCoord, sens, 0);
    }

    private static void auditionSens(Screen screen, int clickCoord, double sens, int arrowCount) {

        double scaledClickSpot = (((double)clickCoord)/screen.width)*(screen.scaledWidth);

        String instructionString = screen.width + " " + screen.height + " " + screen.forceUnicodeFont + " click at " + clickCoord + " raw and " + scaledClickSpot + " scaled";

        if (arrowCount > 0) {

            instructionString += " up arrow " + arrowCount + " times";
        } else if (arrowCount < 0) {

            instructionString += " down arrow " + ((-1)*arrowCount) + " times";
        }

        float floatSens = (float) sens;

        allPossibleSensitivies.add(floatSens);
        instructionSet.put(floatSens, instructionString);
    }

    private static void runTrialForParams(int width, int height, boolean textValue) {

        Screen screen = new Screen(height, width, textValue);

        int boxStart = screen.scaledWidth/2 - 155;
        int unscaledBoxStart = (int)((((double)boxStart)/screen.scaledWidth)*screen.width);

        for (int clickCoord = Math.max(0, unscaledBoxStart-5); clickCoord < width; clickCoord++) {

            double baseSens = getSensFromRawClickCoord(screen, clickCoord);
            auditionSens(screen, clickCoord, baseSens);
            if (baseSens == 1.0) {

                clickCoord = width;
                continue;
            }
            int marksUp = 0;
            double upSens = baseSens;
            while (upSens < 1.0) {

                upSens += 1.0/142.0;
                auditionSens(screen, clickCoord, upSens, ++marksUp);
            }
            int marksDown = 0;
            double downSens = baseSens;
            while (downSens > 0.0) {

                downSens -= 1.0/142.0;
                auditionSens(screen, clickCoord, downSens, --marksDown);
            }
        }
    }

    private static HashSet<Float> goodSensitivities = new HashSet<Float>();

    private static void parseSensitivities() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File("No_Error_Angles.txt")));

        String line = null;

        while ((line = reader.readLine()) != null) {

            StringTokenizer st = new StringTokenizer(line);

            goodSensitivities.add(Float.parseFloat(st.nextToken()));
        }
    }

    public static void main(String[] args) throws Exception {

        parseSensitivities();

        int minSize = 1000;
        int maxSize = 1100;

        boolean[] posTextValues = new boolean[2];
        posTextValues[1] = true;

        for (int height = minSize; height < maxSize; height+=2) {

            for (int width = minSize; width < maxSize; width+=2) {

                for (int i = 0; i < posTextValues.length; i++) {

                    boolean textVal = posTextValues[i];

                    runTrialForParams(width, height, textVal);
                }
            }
        }

        System.out.println(maxSize-100);
        System.out.println(allPossibleSensitivies.size());

        for (Float val : goodSensitivities) {

            if (instructionSet.containsKey(val)) {

                System.out.println(val + " " + instructionSet.get(val));
            }
        }
    }
}