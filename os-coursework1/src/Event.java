import java.lang.Comparable;

/**
 * Event
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class Event implements Comparable<Event> {

  public enum Type { CREATE, BLOCK, UNBLOCK, TERMINATE, TIMER };
  
  protected int time; /*** the timestamp of the event */
  protected Type type; /*** event type */
  protected BurstProcess process;

  /**
   * Constructor
   */
  public Event(int time, BurstProcess process, Type type) {
    this.time = time;
    this.process = process;
    this.type = type;
  }    
  
   /**
   * Copy constructor for periodic events
   */
  public Event(Event e, int n) {
    int period = n*(e.getTime() + e.process.getTotalTime());
    this.time = e.getTime() + period;
    this.process = new BurstProcess(e.process, this.time);
    this.type = e.type;
  }    

  /**
   * Update the time if the event has been created with a time
   * relative to the current time
   */
  public void updateAbsoluteTime(int currentTime) {
    if (time >= 0)
      time += currentTime;
  }

  /**
   * Delays an event for the given amount of time
   */
  public void updateRelativeTime(int update) {
    if (type == Type.BLOCK || type == Type.TERMINATE)
      time += update;
  }
  
  /**
   * Returns the time when this event will happen
   */
  public int getTime() {
    return time;
  }
    
  /**
   * Returns the process attached to this event
   */
  public BurstProcess getProcess() {
    return process;
  }

  /**
   * Returns the event type
   */
  public Type getType() {
    return type;
  }

  /**
   * Events are ordered by their time stamps
   */
  public int compareTo(Event other) {
    int compare = new Integer(time).compareTo(new Integer(other.getTime()));
    if (compare != 0)
      return compare;
    if (type == Type.BLOCK || type == Type.TERMINATE)
      return -1;
    if (other.getType() == Type.BLOCK || type == Type.TERMINATE)
      return 1;
    return 0;
  }

  /**
   * Cancels an event
   */
  public void cancel() {
    time = -1;
  }

  /**
   * Returns a description of an event
   */
  public String toString() {
    return type+" process "+process; 
  }

}
