import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

/**
 * Round Robin Scheduler
 *
 * @version 2017
 */
public class RRScheduler extends AbstractScheduler {
    int timeQuantum;

    private Queue<Process> readyQueue;


    @Override
    public void initialize(Properties parameters) {
        this.timeQuantum = Integer.parseInt(parameters.getProperty("timeQuantum"));
        this.readyQueue = new LinkedList<>();
    }

    @Override
    public int getTimeQuantum() {
        return timeQuantum;
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
        readyQueue.add(process);
    }


    /**
     * Removes the next process to be run from the ready queue
     * and returns it.
     * Returns null if there is no process to run.
     */
    public Process schedule() {
        return readyQueue.poll();
    }


}
