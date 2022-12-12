

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

        String mySens = "0.004676729";

        // Run Float.parseFloat like GameOptions does. Then assign to double.
        double parsedSens = Float.parseFloat(mySens);
        
        double bestSens = Double.NaN;
        double bestError = Double.NaN;

        int mult = 10000000;
        int iterations = 1 * mult;
        double stepSize = 0.001 / mult;

        Random random = new Random(20);

        for (int i = 0; i < iterations; i++) {

            double baseSens = (i-(iterations/2))*stepSize+parsedSens;
            baseSens += random.nextDouble()*stepSize;

            if (Math.abs(baseSens-parsedSens) > 0.001) {

                throw new IllegalStateException("Too much deviation from preferred player setting");
            }

            if (calcMinIncForSens(baseSens) < 0.01) {

                continue;
            }

            if (baseSens < 0) {

                continue;
            }

            // assert Math.abs(baseSens-parsedSens) < 0.002;

            double errorForSens = calcCastingErrorForSens(baseSens);

            if (Double.isNaN(bestError) || errorForSens < bestError) {

                bestSens = baseSens;
                bestError = errorForSens;
            }
        }

        System.out.println("Optimized sens is " + bestSens);
    }
}