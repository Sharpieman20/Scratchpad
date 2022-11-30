

import java.lang.*;

public class SimPrecisionLoss {

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

        String mySens = "0.18661971390247345";

        // Run Float.parseFloat like GameOptions does. Then assign to double.
        double parsedSens = Float.parseFloat(mySens);
        
        double bestSens = Double.NaN;
        double bestError = Double.NaN;

        int iterations = 10;
        double stepSize = 0.00000001;

        for (int i = 0; i < iterations; i++) {

            double baseSens = (i-(iterations/2))*stepSize+parsedSens;

            if (Math.abs(baseSens-parsedSens) > 0.001) {

                throw new IllegalStateException("Too much deviation from preferred player setting");
            }

            // assert Math.abs(baseSens-parsedSens) < 0.002;

            double errorForSens = calcCastingErrorForSens(baseSens);

            if (Double.isNaN(bestError) || errorForSens < bestError) {

                bestSens = baseSens;
                bestError = errorForSens;
            }
        }
        
        System.out.println(parsedSens);
        System.out.println(bestSens);
        System.out.println(Math.abs(bestSens-parsedSens));
        System.out.println(bestError*1_000_000_000);
    }
}