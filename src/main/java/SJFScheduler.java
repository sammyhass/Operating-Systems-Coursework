import java.util.PriorityQueue;
import java.util.Properties;

/**
 * Shortest Job First Scheduler
 *
 * @version 2017
 */
public class SJFScheduler extends AbstractScheduler {

    double initialBurstEstimate;
    double alphaBurstEstimate;

    PriorityQueue<Process> queue;

    @Override
    public void initialize(Properties parameters) {
        initialBurstEstimate = Double.parseDouble(parameters.getProperty("initialBurstEstimate"));
        alphaBurstEstimate = Double.parseDouble(parameters.getProperty("alphaBurstEstimate"));
        queue = new PriorityQueue<>((Process p1, Process p2) -> (int) (predictNextBurst(p1) - predictNextBurst(p2)));
    }

    @Override
    public int getTimeQuantum() {
        return super.getTimeQuantum();
    }

    @Override
    public boolean isPreemptive() {
        return false;
    }

    /**
     * Adds a process to the ready queue.
     * usedFullTimeQuantum is true if process is being moved to ready
     * after having fully used its time quantum.
     */
    public void ready(Process process, boolean usedFullTimeQuantum) {
        queue.add(process);
    }

    /**
     * Removes the next process to be run from the ready queue
     * and returns it.
     * Returns null if there is no process to run.
     */
    public Process schedule() {
        return queue.poll();
    }

    // Predicts the next burst time for a process.
    // uses exponential average to predict burst time.
    private double predictNextBurst(Process process) {
        // t_(lastBurst) = actual length of nth burst
        // t_(lastBurst+1) = predicted length of next burst
        // For alpha, 0 <= alpha <= 1, the predicted burst time is
        // t_(lastBurst + 1) = (alpha * t_(lastBurst)) + ((1-a) * t_(lastBurst+1))
        // where a is the alphaBurstEstimate.

        double burst;

        // If the process is new, use the initial burst estimate.
        if (process.getRecentBurst() == -1) {
            burst = initialBurstEstimate;
        } else { // Otherwise, use the exponential average.
            burst = (alphaBurstEstimate * process.getRecentBurst()) + ((1 - alphaBurstEstimate) * process.getRecentBurst());
        }
        return burst;
    }
}
