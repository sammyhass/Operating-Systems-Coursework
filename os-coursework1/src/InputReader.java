import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;

/**
 * Reads an input data file
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class InputReader {

  private List<Event> events;

  /**
   * Constructor
   */
  public InputReader(String fileName){
    events = new LinkedList<Event>();
    
    try {
      File file = new File(fileName);
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);

        int priority = 0;
        int arrivalTime = 0;

        // parse priority
        if (st.hasMoreTokens()) {
          String s = st.nextToken();
          try {
            priority = Integer.parseInt(s);
          } catch (NumberFormatException e) {
            System.err.println(
	      "Warning: Priority is not a number. Ignoring line: "+line);
            continue;
          }
        }

        // parse arrival time
        if (st.hasMoreTokens()) {
          String s = st.nextToken();
          try {
            arrivalTime = Integer.parseInt(s);
          } catch (NumberFormatException e) {
            System.err.println(
	      "Warning: Arrival time is not a number. Ignoring line: "+line);
            continue;
          }
        }

        List<Integer> bursts = new LinkedList<Integer>();
	
        // gather burst data
        while (st.hasMoreTokens()) {
          String s = st.nextToken();
          try {
            bursts.add(new Integer(s));
          } catch (NumberFormatException e) {
            System.err.println(
	      "Warning: Burst time is not a number. Ignoring line: "+line);
            continue;
          }
        }

        // We always have an odd number of tokens in each line
	if(bursts.size() % 2 != 1) {
	  System.err.println(
	    "Warning: Number of bursts must be odd. Ignoring line: "+line);
	  continue;
	}
	
        // this is a good line
        BurstProcess process =
	  new BurstProcess(
	    arrivalTime, priority,
	    bursts.stream().mapToInt(Integer::intValue).toArray());
        Event event = new Event(arrivalTime, process, Event.Type.CREATE);
        events.add(event);
      }
      fileReader.close();
    } catch (IOException e) {
      System.err.println("Cannot read input data file");
      System.exit(1);
    }
  }
  
  /**
   * Returns the list of events read form the input file
   */
  public List<Event> getEvents() {
    return events;
  }

}

