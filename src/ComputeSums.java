import java.util.concurrent.RecursiveAction;

/**
 * Tino Muzambi
 * 2019/09/12 13:48
 * Compute global sums.
 */
public class ComputeSums extends RecursiveAction {

    private static CloudData cloudData;
    private static int lo, hi;
    private static int[] arrLocation;
    private static double sumX, sumY;

    private ComputeSums(CloudData cloudData, int lo, int hi, double avgx, double avgy) {
        ComputeSums.cloudData = cloudData;
        ComputeSums.lo = lo;
        ComputeSums.hi = hi;
        sumX = avgx;
        sumY = avgy;
        arrLocation = new int[3];
    }

    /**
     * Compute sums using fork-join.
     */
    @Override
    protected void compute() {
        if ((hi - lo) < ParallelVersion.SEQ_CUTOFF) {

            for (int i = lo; i < hi; ++i) {
                cloudData.locate(i, arrLocation);
                sumX += cloudData.advection[arrLocation[0]][arrLocation[1]][arrLocation[2]].x;
                sumY += cloudData.advection[arrLocation[0]][arrLocation[1]][arrLocation[2]].y;
            }

        }
        else {
            ComputeSums left = new ComputeSums(cloudData, lo, (hi + lo) / 2, sumX, sumX);
            ComputeSums right = new ComputeSums(cloudData, (hi + lo) / 2, hi, sumX, sumX);

            left.fork();
            right.compute();
            left.join();
        }
    }

    /**
     * Invokes runs of ComputeSums to obtain global sums.
     * @param cloudData CloudData object.
     * @return array of length 2 containing x sum and y sum.
     */
    static double[] doCompute(CloudData cloudData) {
        double[] ans = new double[2];
        ParallelVersion.fjPool.invoke(new ComputeSums(cloudData, 0, cloudData.dim(), ParallelVersion.avgx, ParallelVersion.avgy));
        ans[0] = sumX;
        ans[1] = sumY;
        return ans;
    }

}
