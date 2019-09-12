import java.util.concurrent.RecursiveAction;

/**
 * Tino Muzambi
 * 2019/09/08 17:30
 * Set classification of clouds.
 */
public class SetClassification extends RecursiveAction {

    private CloudData cloudData;
    private int lo, hi;
    private static int count = -1;
    private int[] arrLocation;
    private double[] averages;

    SetClassification(CloudData cloudData, int lo, int hi, double[] averages) {
        this.cloudData = cloudData;
        this.lo = lo;
        this.hi = hi;
        this.averages = averages;
        count++;
        arrLocation = new int[3];
    }

    @Override
    protected void compute() {
        if ((hi - lo) < ParallelVersion.SEQ_CUTOFF) {
            for (int i = lo; i < hi; ++i) {
                cloudData.locate(i, arrLocation);
                if (Math.abs(cloudData.convection[arrLocation[0]][arrLocation[1]][arrLocation[2]]) > averages[count]) {
                    cloudData.classification[arrLocation[0]][arrLocation[1]][arrLocation[2]] = 0;
                }
                else if ((averages[count] > 0.2) && (averages[count] >= Math.abs(cloudData.convection[arrLocation[0]][arrLocation[1]][arrLocation[2]]))) {
                    cloudData.classification[arrLocation[0]][arrLocation[1]][arrLocation[2]] = 1;
                }
                else {
                    cloudData.classification[arrLocation[0]][arrLocation[1]][arrLocation[2]] = 2;
                }
            }
        }
        else {
            SetClassification right = new SetClassification(cloudData, lo, (hi + lo) / 2, averages);
            SetClassification left = new SetClassification(cloudData, (hi + lo) / 2, hi, averages);

            left.fork();
            right.compute();
            left.join();
        }
    }
}
