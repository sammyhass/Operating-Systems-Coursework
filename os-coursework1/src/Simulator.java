import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Simulator
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class Simulator
{
  /**
   * Prints a usage message
   */
  public static void usage() {
    System.err.println(
      "Usage: java Simulator propertyfile outputfile inputfiles");
  }

  /**
   * Main method
   * 
   * @param  args   the command line arguments
   */
  public static void main(String args[]) {
    // check arguments
    if (args.length < 3) {
      usage();
      System.exit(1);
    }

    // load properties
    Properties parameters = new Properties();
    try {
      File propertyFile = new File(args[0]);
      if(propertyFile != null) {
	parameters.load(new FileInputStream(propertyFile));
      }
    } catch (FileNotFoundException e) {
      System.err.println("Given property file not found");
      System.err.print(e);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Problem loading property file");
      System.exit(1);
    }

    // instantiate scheduler
    AbstractScheduler scheduler = null;
    try {
      scheduler = (AbstractScheduler)Class
	.forName(parameters.getProperty("scheduler")).newInstance();
      scheduler.initialize(parameters);
    } catch (Exception e) {
      System.err.println("Given scheduler class not found");
      System.exit(1);
    }

    // should the events be repeated periodically
    boolean periodic = false;
    try {
      periodic =
	Boolean.parseBoolean(parameters.getProperty("periodic", "false"));
    } catch(Exception e) {
      System.err.println("periodic not a boolean.");
      System.exit(1);
    }

    // duration of the simulation
    int timeLimit = 0;
    try {
      timeLimit =
	Integer.parseInt(parameters.getProperty("timeLimit", "1000"));
    } catch(NumberFormatException e) {
      System.err.println("timeLimit not a number.");
      System.exit(1);
    }

    ProcessModel processModel = new ProcessModel(parameters, scheduler);
    EventProcessor eventProcessor = new EventProcessor(processModel, timeLimit);
    
    // load input files
    for (int argindex=2; argindex < args.length; argindex++) {
      // load input file
      InputReader inputReader = new InputReader(args[argindex]);
      eventProcessor.addInitialEvents(inputReader.getEvents(), periodic);
    }
    
    // run simulator
    eventProcessor.run();
    
    // write output
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
      out.write(processModel.getOutput());
      out.flush();
      out.close();   
    } catch (IOException e) {
      System.err.println("Problem writing output file");
      System.err.print(e);
      System.exit(1);
    }
  }
}
