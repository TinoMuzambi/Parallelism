import java.util.concurrent.RecursiveTask;

/**
 * Tino Muzambi
 * 2019/09/04 16:45
 * Obtain sum of all y elements.
 */
public class SumXElements extends RecursiveTask<Double> {

    private CloudData cloudData;
    private int lo, hi;
    private int[] arrLocation;

    SumXElements(int lo, int hi, CloudData cloudData) {
        this.lo = lo;
        this.hi = hi;
        this.cloudData = cloudData;
        arrLocation = new int[3];
    }

    @Override
    protected Double compute() {
        if ((hi - lo) < ParallelVersion.SEQ_CUTOFF) {
            double sumX = 0;
            int sumY = 0;
            for (int i = lo; i < hi; ++i) {
                cloudData.locate(i, arrLocation);
                sumX += cloudData.advection[arrLocation[0]][arrLocation[1]][arrLocation[2]].x;
            }
            return sumX;
        }
        else {
            SumXElements right = new SumXElements(lo, (hi + lo) / 2, cloudData);
            SumXElements left = new SumXElements((hi + lo) / 2, hi, cloudData);

            left.fork();
            double rightResult = right.compute();
            double leftResult = left.join();
            return rightResult + leftResult;
        }

    }
}
