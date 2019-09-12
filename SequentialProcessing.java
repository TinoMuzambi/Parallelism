import java.io.FileNotFoundException;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Tino Muzambi
 * 2019/08/23 20:22
 * Processing data sequententially.
 */
public class SequentialProcessing {

    private static CloudData cloudData = new CloudData();
    private static long startTime = 0;
    private static double avgx = 0;
    private static double avgy = 0;

    /**
     * Start timer.
     */
    private static void tick(){
        startTime = System.currentTimeMillis();
    }

    /**
     * Stop timer.
     * @return Time taken from tick() call.
     */
    private static float tock(){
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    /**
     * Determine cloud type.
     * @param wind wind value for specific index.
     * @param t time value.
     * @param x x-coordinate.
     * @param y y-coordinate.
     */
    private static void setClassification(double wind, int t, int x, int y) {
        if (Math.abs(cloudData.convection[t][x][y]) > wind) {
            cloudData.classification[t][x][y] = 0;
        }
        else if ((wind > 0.2) && (wind >= Math.abs(cloudData.convection[t][x][y]))) {
            cloudData.classification[t][x][y] = 1;
        }
        else {
            cloudData.classification[t][x][y] = 2;
        }
    }

    /**
     * Calculates local average for a given time level.
     * @param t time level.
     */
    private static void setLocalAverages(int t) {
        for (int i = 0; i < cloudData.dimx; i++) {
            for (int j = 0; j < cloudData.dimy; j++) {
                double xAvg = 0;
                double yAvg = 0;
                int numNeighbours = 0;
                for (int k = max(0, i - 1); k < min(cloudData.dimx, i + 2); k++) {
                    for (int l = max(0, j - 1); l < min(cloudData.dimy, j + 2); l++) {
                        xAvg += cloudData.advection[t][k][l].x;
                        yAvg += cloudData.advection[t][k][l].y;
                        numNeighbours++;
                    }
                }
                xAvg = Math.pow(xAvg / numNeighbours, 2);
                yAvg = Math.pow(yAvg / numNeighbours, 2);
                double avg = Math.sqrt(xAvg + yAvg);
                setClassification(avg, t, i, j);
            }
        }
    }

    public static void main(String[] args) {
        System.gc();
        tick();
        cloudData.readData(args[0]);
        float time = tock();
        System.out.println("Reading in file took " + time + " seconds");

        tick();
        for (int t = 0; t < cloudData.dimt; t++) { // Time level
            for (int x = 0; x < cloudData.dimx; x++) { // X grid level
                for (int y = 0; y < cloudData.dimy; y++) { // Y grid level
                    avgx += cloudData.advection[t][x][y].x;
                    avgy += cloudData.advection[t][x][y].y;
                }
            }
        }

        avgx /= cloudData.dim();
        avgy /= cloudData.dim();

        // Calculate local average
        for (int i = 0; i < cloudData.dimt; i++) {
            setLocalAverages(i);
        }
        time = tock();
        System.out.println("Processing took " + time + " seconds");

        tick();
        Vector wind = new Vector(avgx, avgy);
        cloudData.writeData(args[1], wind);
        time = tock();
        System.out.println("Writing file took " + time + " seconds");
    }
}
