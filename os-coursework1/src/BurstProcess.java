import java.lang.Comparable;
import java.util.Queue;
import java.util.LinkedList;

/**
 * A simulated process with bursts
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class BurstProcess extends Process {

  /**
   * The idle process
   */
  private static BurstProcess theIdleProcess = null;
  public static BurstProcess idleProcess() {
    if (theIdleProcess == null) {
      theIdleProcess = new BurstProcess(0, 0, new int[0]);  
    }
    return theIdleProcess;
  }
  
  protected int[] bursts;
  protected int burstIndex;
  private int burstStartTime;
  private int burstRemainingTime;
  private Event nextEvent;

  /**
   * Constructor
   */
  public BurstProcess(int arrivalTime, int priority, int[] bursts) {
    super(arrivalTime, priority);
    this.nextEvent = null;
    this.bursts = bursts;
    this.burstIndex = -1;
  }

  /**
   * Copy constructor
   */
  public BurstProcess(BurstProcess p, int arrivalTime) {
    this(arrivalTime, p.priority, p.bursts);
  }

  /**
   *
   */
  public int getRecentBurst() {
    if (burstIndex < 0)
      return -1;
    
    if (burstIndex % 2 == 1)
      return bursts[burstIndex - 1];

    return bursts[burstIndex];
  }

  /**
   *
   */
  public int getNextBurst() {
    int i = burstIndex;
    if(i < 0 || i % 2 == 1)
      i = burstIndex + 1;
    
    if (0 <= i && i < bursts.length)
      return bursts[i];

    return -1;
  }
  
  /**
   *
   */
  public int getTotalTime() {
    int total = 0;
    for(int i=0; i<bursts.length; i++)
      total += bursts[i];
    return total;
  }

  /**
   *
   */
  private int nextBurst(int currentTime) {
    // return remaining time if process has been interrupted
    if (burstRemainingTime > 0) {
      burstStartTime = currentTime;
      return burstRemainingTime;
    }

    burstIndex++;
    
    // check whether process has terminated
    if (burstIndex >= bursts.length) {
      burstRemainingTime = 0;
      return -1;
    }
    
    burstRemainingTime = bursts[burstIndex];
    burstStartTime = currentTime;

    return burstRemainingTime;
  }

  /**
   *
   */
  private boolean hasBurst() {
    // check whether process has terminated
    return burstIndex+1 < bursts.length;
  }

  /**
   *
   */
  public Event setRunning(int currentTime, int timeQuantum) {
    state = State.RUNNING;

    if (this.equals(BurstProcess.idleProcess())) {
      startedTime = 0;
      burstStartTime = currentTime;
      return null;
    } else {
      if (startedTime < 0)
	startedTime = currentTime;
    }
    
    int burst = nextBurst(currentTime);
    boolean isLastBurst = !hasBurst();
    if (timeQuantum <= 0 || burst <= timeQuantum) {
      nextEvent =
	new Event(burst,
		  this,
		  isLastBurst ? Event.Type.TERMINATE : Event.Type.BLOCK);
    } else {
      nextEvent = new Event(timeQuantum, this, Event.Type.TIMER);
    }
    return nextEvent;
  }

  /**
   *
   */
  public Event setBlocked(int currentTime) {
    state = State.BLOCKED;
    cpuTime = cpuTime + currentTime - burstStartTime;
    burstRemainingTime = 0; // reached the end of the CPU burst
    int burst = nextBurst(currentTime);
    blockedTime += burst;
    burstRemainingTime = 0; // I/O bursts cannot be delayed in our model
    nextEvent = new Event(burst, this, Event.Type.UNBLOCK);
    return nextEvent;
  }
  
  /**
   *
   */
  public void setReady(int currentTime) {
    if (state == State.RUNNING) {
      cpuTime = cpuTime + currentTime - burstStartTime;
    
      // the process has been preeempted, thus cancel existing events
      if (nextEvent != null) {
	burstRemainingTime = burstStartTime + burstRemainingTime - currentTime;
	nextEvent.cancel();
	nextEvent = null;
      }
    }

    state = State.READY;
  }

  /**
   *
   */
  public void setTerminated(int currentTime) {
    state = State.TERMINATED;
    cpuTime = cpuTime + currentTime - burstStartTime;
    terminatedTime = currentTime;
  }
}
