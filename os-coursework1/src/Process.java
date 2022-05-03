import java.lang.Comparable;
import java.util.Queue;
import java.util.LinkedList;

/**
 * A simulated process
 * 
 * @version 2017
 */
public abstract class Process implements Comparable<Process> {

  public enum State { CREATED, READY, RUNNING, BLOCKED, TERMINATED };
  
  protected int id; /*** process ID */
  protected int priority; /*** priority (lower value means higher priority) */

  protected int createdTime; /*** point in time the process was created */
  protected int startedTime; /*** point in time 
				the process was running for the first time */
  protected int terminatedTime; /*** point in time the process terminated */
  protected int blockedTime; /*** amount of time the process was blocked */
  protected int cpuTime; /*** amount of time the process was running */

  protected State state; /*** process state */
  
  /**
   * Returns the time the process spends waiting in the ready queue.
   */
  public int getWaitingTime() {
    return startedTime - createdTime + blockedTime;
  }

  /**
   *  Returns the turnaround time of the process.
   */
  public int getTurnaroundTime() {
    return terminatedTime - createdTime;
  }

  /**
   * Returns the response time of the process.
   */
  public int getResponseTime() {
    return startedTime - createdTime;
  }

  /**
   * Sets the priority of the process
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * Returns the priority of the process
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Returns the process ID
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the duration of the most recent CPU burst
   * or -1 if a CPU burst has not yet happened.
   */
  public abstract int getRecentBurst();

  /**
   * Returns the next CPU burst in the future
   * or -1 if there is no such CPU burst.
   */
  public abstract int getNextBurst();

  /**
   * Defines the natural ordering of proceses by their priority.
   */
  public int compareTo(Process other) {
    return new Integer(priority).compareTo(new Integer(other.getPriority()));
  }

  /**
   * Processes are equal if they have the same ID.
   */
  public boolean equals(Process other) {
    if (other == null)
      return false;
    return id == other.getId();
  }

  /**
   * Constructor
   */
  protected Process(int arrivalTime, int priority) {
    this.id = nextProcessId;
    nextProcessId++;
    this.priority = priority;
    this.createdTime = arrivalTime;
    this.startedTime = -1;
    this.terminatedTime = -1;
    this.blockedTime = 0;
    this.cpuTime = 0;
    this.state = State.CREATED;
  }
  
  /**
   * Returns a string representation of the process
   */
  public String toString() {
    return ""+id;
  }

  /**
   * Returns the header row of the output table
   */
  public static String getHeaderRecord() {
    return "id\tpriority\tcreatedTime\tstartedTime\tterminatedTime"+
      "\tcpuTime\tblockedTime"+
      "\tturnaroundTime\twaitingTime\tresponseTime";
  }

  /**
   * Returns a row containing the process metrics
   */
  public String getRecord() {
    return id+"\t"+priority+"\t"+createdTime+"\t"+startedTime+"\t"+
      terminatedTime+"\t"+cpuTime+"\t"+blockedTime+
      "\t"+getTurnaroundTime()+"\t"+getWaitingTime()+
      "\t"+getResponseTime();
  }

  private static int nextProcessId = 0; /*** process ID for a new process */
}
