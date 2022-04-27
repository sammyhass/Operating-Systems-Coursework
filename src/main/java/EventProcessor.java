import java.lang.IllegalStateException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * EventProcessor
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class EventProcessor {

  private LinkedList<Event> eventQueue;
  private ProcessModel processModel;
  private int timeLimit;
  private int time;
  
  /**
   * Constructor
   */
  public EventProcessor(ProcessModel processModel, int timeLimit) {
    this.eventQueue = new LinkedList<Event>();
    this.processModel = processModel;
    this.timeLimit = timeLimit;
    this.time = 0;
  }

  /**
   * Adds initial events to the event queue
   */
  public void addInitialEvents(Collection<Event> events, boolean periodic) {
    // add given events to event queue
    for (Event e : events) {
      eventQueue.add(e);

      // if periodic, create duplicate events
      //   at multiples of inter-arrival time plus cpu plus io time
      int i=0;
      Event ee = null;
      if (periodic) {
	do {
	  i++;
	  ee = new Event(e, i);
	  if (ee.getTime() >= timeLimit)
	    break;
	  eventQueue.add(ee);
	} while (true);
      }
    }
  }  
  
  /**
   * Runs the simulation
   */
  public void run() {
    try {
      // check whether there is a new event in the queue
      Event e = null;
      while ((e = poll(eventQueue)) != null) {

	// ignore past and cancelled events
	if (time > e.getTime()) {
	  continue;
	}
      
	// current time
	time = e.getTime();

	// reached the time limit, stop the simulation
	if (time >= timeLimit)
	  break;

	// process event
        System.err.println(time+": "+e);
	Collection<Event> newEvents = new ArrayList<Event>();
	int timeUpdate = processModel.execute(e, newEvents);

	// delay some existing events
	for (Event ee : eventQueue) {
	  ee.updateRelativeTime(timeUpdate);
	}
	
	// add new events to the queue
	for (Event newEvent : newEvents) {
	  newEvent.updateAbsoluteTime(time+timeUpdate);
	  eventQueue.add(newEvent);
	}
      }
      // shut down the model
      processModel.done(time);
    } catch (IllegalStateException e) {
      System.err.println("Increase event queue size");
      System.exit(1);
    }
  }

  private static Event poll(LinkedList<Event> eventQueue) {
    java.util.Collections.sort(eventQueue);
    if (eventQueue.isEmpty())
      return null;
    return eventQueue.removeFirst();
  }
  
}
