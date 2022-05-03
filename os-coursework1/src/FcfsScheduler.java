import java.util.Queue;
import java.util.LinkedList;

/**
 * First-Come First-Served Scheduler
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class FcfsScheduler extends AbstractScheduler {

  private Queue<Process> readyQueue;

 /**
   * Creates an instance of the FCFS scheduler
   */
  public FcfsScheduler() {
    readyQueue = new LinkedList<Process>();
  }

  /**
   * Adds a process to the ready queue.
   */
  public void ready(Process process, boolean usedFullTimeQuantum) {
    readyQueue.offer(process);
  }

  /**
   * Removes the next process to be run from the ready queue 
   * and returns it. 
   * Returns null if there is no process to run.
   */
  public Process schedule() {
    System.out.println("Scheduler selects process "+readyQueue.peek());
    return readyQueue.poll();
  }
}
