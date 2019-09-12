import java.util.concurrent.ForkJoinPool;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Tino Muzambi
 * 2019/08/23 20:23
 * Processing data in parallel.
 */
public class ParallelVersion {// Map and Reduce

    private static final ForkJoinPool fjPool = new ForkJoinPool();
    private static long startTime = 0;
    private static CloudData cloudData = new CloudData();
    private static double[] averages;
    static int SEQ_CUTOFF = 1000;

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
     * Calculates local average for a given time level.
     * @param t time level.
     */
    private static void setLocalAverages(int t) {
        averages = new double[cloudData.dim()];
        int count = 0;
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
                averages[count] = Math.sqrt(xAvg + yAvg);
                count++;
            }
        }
    }

//    /**
//     * Determine cloud type.
//     * @param wind wind value for specific index.
//     * @param t time value.
//     * @param x x-coordinate.
//     * @param y y-coordinate.
//     */
//    private static void setClassification(double wind, int t, int x, int y) {
//        if (Math.abs(cloudData.convection[t][x][y]) > wind) {
//            cloudData.classification[t][x][y] = 0;
//        }
//        else if ((wind > 0.2) && (wind >= Math.abs(cloudData.convection[t][x][y]))) {
//            cloudData.classification[t][x][y] = 1;
//        }
//        else {
//            cloudData.classification[t][x][y] = 2;
//        }
//    }

    public static void main(String[] args) {
        System.gc();
        tick();
        cloudData.readData(args[0]);
        SEQ_CUTOFF = Integer.parseInt(args[2]);

        float time = tock();
        System.out.println("Reading in file took " + time + " seconds");

        tick();
        double sumX = fjPool.invoke(new SumXElements(0, cloudData.dim(), cloudData));
        double avgx = sumX / cloudData.dim();
        double sumY = fjPool.invoke(new SumYElements(0, cloudData.dim(), cloudData));
        double avgy = sumY / cloudData.dim();

        for (int i = 0; i < cloudData.dimt; i++) {
            setLocalAverages(i);
        }
        fjPool.invoke(new SetClassification(cloudData, 0, cloudData.dim(), averages));
        time = tock();
        System.out.println("Processing took " + time + " seconds");

        tick();
        Vector wind = new Vector(avgx, avgy);
        cloudData.writeData(args[1], wind);
        time = tock();
        System.out.println("Writing file took " + time + " seconds");
    }
}
