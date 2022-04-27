import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.Properties;

/**
 * Process Model
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class ProcessModel {

  private BurstProcess running;
  private AbstractScheduler scheduler;
  private int interruptTime;
  
  private Properties parameters;
  private StringBuilder log;
  
  /**
   * Creates the process model
   */
  public ProcessModel(Properties parameters,
		      AbstractScheduler scheduler) {
    this.scheduler = scheduler;
    this.running = BurstProcess.idleProcess();
    this.log = new StringBuilder();
    output(Process.getHeaderRecord());

    // the time an interrupt takes to execute (including scheduler calls)
    try {
      this.interruptTime =
	Integer.parseInt(parameters.getProperty("interruptTime", "0"));
    } catch(NumberFormatException e) {
      System.err.println("interruptTime not a number.");
      System.exit(1);
    }
  }

  /**
   * Dispatches the selected process
   */
  private void dispatch(BurstProcess process) {
    running = process;
  }

  /**
   * Moves the process to the ready queue
   */
  private void moveToReady(int currentTime, boolean usedFullTimeQuantum) {
    if (!BurstProcess.idleProcess().equals(running)) {
      scheduler.ready(running, usedFullTimeQuantum);
    }
    running.setReady(currentTime);
    running = null;
  }

  /**
   * Runs the scheduler
   */
  private void run(int currentTime,
		   BurstProcess process,
		   Collection<Event> newEvents) {
    // run the idle process if the scheduler did not select anything
    if (process == null) {
      process = BurstProcess.idleProcess();
    }

    // get the next event for this process
    // (which could be BLOCK, TIMER, or TERMINATE)
    int timeQuantum = scheduler.getTimeQuantum();
    Event newEvent = process.setRunning(currentTime+interruptTime, timeQuantum);
    if (newEvent != null)
      newEvents.add(newEvent);

    // dispatch selected process
    dispatch(process);
  }
  
  /**
   * Performs a CREATE action
   */
  protected void create(int currentTime,
			BurstProcess process,
			Collection<Event> newEvents) {
    // add the new process to the ready queue
    scheduler.ready(process, false);
    process.setReady(currentTime);

    // run the scheduler if currently we are idling or
    // the scheduler is preemptive
    if (BurstProcess.idleProcess().equals(running) ||
	scheduler.isPreemptive()) {
      moveToReady(currentTime, false);
      run(currentTime, (BurstProcess)scheduler.schedule(), newEvents);
    }
  }

  /**
   * Performs a TERMINATE action
   */
  protected void terminate(int currentTime,
			   BurstProcess process,
			   Collection<Event> newEvents) {
    process.setTerminated(currentTime);

    // log the process statistics
    output(process.getRecord());

    // run the scheduler
    run(currentTime, (BurstProcess)scheduler.schedule(), newEvents);
  }

  /**
   * Performs a BLOCK action
   */
  protected void block(int currentTime,
		       BurstProcess process,
		       Collection<Event> newEvents) {
    running = null;

    // get the unblock event
    Event newEvent = process.setBlocked(currentTime);
    newEvents.add(newEvent);

    // run the scheduler
    run(currentTime, (BurstProcess)scheduler.schedule(), newEvents);
  }

  /**
   * Performs an UNBLOCK action
   */
  protected void unblock(int currentTime,
			 BurstProcess process,
			 Collection<Event> newEvents) {
    // add the unblocked process to the ready queue
    scheduler.ready(process, false);
    process.setReady(currentTime);

    // run the scheduler if currently we are idling or
    // the scheduler is preemptive
    if (BurstProcess.idleProcess().equals(running) || scheduler.isPreemptive()) {
      moveToReady(currentTime, false);
      run(currentTime, (BurstProcess)scheduler.schedule(), newEvents);
    }
  }

  /**
   * Performs a TIMER interrupt action
   */
  protected void timerInterrupt(int currentTime,
				BurstProcess process,
				Collection<Event> newEvents) {
    // add the interrupted process to the ready queue
    moveToReady(currentTime, true);

    // run the scheduler
    run(currentTime, (BurstProcess)scheduler.schedule(), newEvents);
  }

  /**
   * Executes the action associated with the event.
   * Returns the amount of time the action has taken.
   */
  public int execute(Event e, Collection<Event> newEvents) {
    switch(e.getType()) {
    case CREATE:
      create(e.getTime(), e.getProcess(), newEvents);
      break; 
    case TERMINATE:
      terminate(e.getTime(), e.getProcess(), newEvents);
      break; 
    case BLOCK:
      block(e.getTime(), e.getProcess(), newEvents);
      break; 
    case UNBLOCK:
      unblock(e.getTime(), e.getProcess(), newEvents);
      break; 
    case TIMER:
      timerInterrupt(e.getTime(), e.getProcess(), newEvents);
      break; 
    }
    return interruptTime;
  }
  
  /**
   * Adds a line to the log data
   */
  private void output(String s) {
    log.append(s+System.lineSeparator());
  }
  
  /**
   * Is executed after the simulation finished 
   */
  public void done(int currentTime) {
    BurstProcess.idleProcess().setTerminated(currentTime);
    output(BurstProcess.idleProcess().getRecord());
  }

  /**
   * Turns the log data into a string
   */
  public String getOutput() {
    return log.toString();
  }

}
