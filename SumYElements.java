import java.util.concurrent.RecursiveTask;

/**
 * Tino Muzambi
 * 2019/09/08 17:19
 * Obtain sum of all y elements.
 */
public class SumYElements extends RecursiveTask<Double> {

    private CloudData cloudData;
    private int lo, hi;
    private int[] arrLocation;

    SumYElements(int lo, int hi, CloudData cloudData) {
        this.lo = lo;
        this.hi = hi;
        this.cloudData = cloudData;
        arrLocation = new int[3];
    }

    @Override
    protected Double compute() {
        if ((hi - lo) < ParallelVersion.SEQ_CUTOFF) {
            double sumY = 0;
            for (int i = lo; i < hi; ++i) {
                cloudData.locate(i, arrLocation);
                sumY += cloudData.advection[arrLocation[0]][arrLocation[1]][arrLocation[2]].y;
            }
            return sumY;
        } else {
            SumYElements right = new SumYElements(lo, (hi + lo) / 2, cloudData);
            SumYElements left = new SumYElements((hi + lo) / 2, hi, cloudData);

            left.fork();
            double rightResult = right.compute();
            double leftResult = left.join();
            return rightResult + leftResult;
        }
    }
}
