

import java.lang.*;
import java.util.*;

public class OptimizeMySens {

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

    public static void main(String[] args) {

        String mySens = "0.001";

        // Run Float.parseFloat like GameOptions does. Then assign to double.
        float parsedSens = Float.parseFloat(mySens);

        float curSens = parsedSens;

        long numFloats = 0;

        while (curSens < 1.0f) {

            if (calcMinIncForSens(curSens) > 0.01) {

                numFloats++;
            }

            curSens = Math.nextUp(curSens);
        }

        System.out.println("Num floats is " + numFloats);
    }
}