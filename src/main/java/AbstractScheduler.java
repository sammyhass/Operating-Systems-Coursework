import java.util.Properties;

/**
 * AbstractScheduler
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public abstract class AbstractScheduler {

  /**
   * Initializes the scheduler from the given parameters
   */
  public void initialize(Properties parameters) {
  }
  
  /**
   * Adds a process to the ready queue.
   * usedFullTimeQuantum is true if process is being moved to ready
   * after having fully used its time quantum.
   */
  public abstract void ready(Process process, boolean usedFullTimeQuantum);

  /**
   * Removes the next process to be run from the ready queue 
   * and returns it.
   * Returns null if there is no process to run.
   */
  public abstract Process schedule();

  /**
   * Returns the time quantum of this scheduler
   * or -1 if the scheduler does not require a timer interrupt.
   */
  public int getTimeQuantum() {
    return -1;
  }

  /**
   * Returns whether the scheduler is preemptive
   */
  public boolean isPreemptive() {
    return false;
  }
  
}
