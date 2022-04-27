import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Properties;

/**
 * Ideal Shortest Job First Scheduler
 *
 * @version 2017
 */
public class IdealSJFScheduler extends AbstractScheduler {


    PriorityQueue<Process> readyQueue;


    @Override
    public boolean isPreemptive() {
        return false;
    }

    @Override
    public void initialize(Properties parameters) {
        // Initialize a priority queue of processes sorted by next burst time
        readyQueue = new PriorityQueue<>(10, Comparator.comparingInt(Process::getNextBurst));

    }


    /**
     * Adds a process to the ready queue.
     * usedFullTimeQuantum is true if process is being moved to ready
     * after having fully used its time quantum.
     */
    public void ready(Process process, boolean usedFullTimeQuantum) {
        readyQueue.add(process);
    }

    /**
     * Removes the next process to be run from the ready queue
     * and returns it.
     * Returns null if there is no process to run.
     */
    public Process schedule() {
        if (readyQueue.isEmpty()) {
            return null;
        }
        return readyQueue.poll();
    }
}
