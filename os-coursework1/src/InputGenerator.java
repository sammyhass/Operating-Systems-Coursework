import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

/**
 * InputGenerator
 * 
 * @author Peter Schrammel
 * @version 2017
 */
public class InputGenerator
{
  /**
   * Prints a usage message
   */
  public static void usage() {
    System.err.println("Usage: java InputGenerator propertyfile outputfile");
  }

  /**
   * Main method
   * 
   * @param  args   the command line arguments
   */
  public static void main(String args[]) {
    // check arguments
    if (args.length != 2) {
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
      System.err.print(e);
      System.exit(1);
    }

    // run generator
    InputGenerator inputGenerator = new InputGenerator(parameters);
    inputGenerator.run();
    
    // write to file
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
      out.write(inputGenerator.toString());
      out.flush();
      out.close();   
    } catch (IOException e) {
      System.err.println("Problem writing output file");
      System.err.print(e);
      System.exit(1);
    }
  }

  private Properties parameters;
  private static class Record {
    Record(int cycles) {
      bursts = new int[cycles];
    }

    int priority;
    int arrivalTime;
    int[] bursts;
  };
  private Record[] data;
  
  /**
   * Creates an input generator
   */
  private InputGenerator(Properties parameters) {
    this.parameters = parameters;
  }
  
  /**
   * Runs the input generator
   */
  private void run() {
    long seed =
      Long.parseLong(parameters.getProperty("seed",
                                            System.currentTimeMillis()+""));
    Random random = new Random(seed);

    // number of processes
    int n = 0;
    try {
      n =
        Integer.parseInt(parameters.getProperty("numberOfProcesses", "0"));
    } catch(NumberFormatException e) {
      System.err.println("numberOfProcesses not a number.");
      System.exit(1);
    }
    data = new Record[n];

    int priority = 0;
    try {
      priority =
        Integer.parseInt(parameters.getProperty("staticPriority", "0"));
    } catch(NumberFormatException e) {
      System.err.println("timeLimit not a number.");
      System.exit(1);
    }
    
    double meanInterArrival = 1.0;
    try {
      meanInterArrival =
        Double.parseDouble(parameters.getProperty("meanInterArrival", "1.0"));
    } catch(NumberFormatException e) {
      System.err.println("meanInterArrival not a number.");
      System.exit(1);
    }
    ExponentialGenerator arrival =
      new ExponentialGenerator(random, meanInterArrival);

    double meanCpuBurst = 1.0;
    try {
      meanCpuBurst =
        Double.parseDouble(parameters.getProperty("meanCpuBurst", "1.0"));
    } catch(NumberFormatException e) {
      System.err.println("meanCpuBurst not a number.");
      System.exit(1);
    }
    ExponentialGenerator cpuBurst =
      new ExponentialGenerator(random, meanCpuBurst);

    double meanIOBurst = 1.0;
    try {
      meanIOBurst =
        Double.parseDouble(parameters.getProperty("meanIOBurst", "1.0"));
    } catch(NumberFormatException e) {
      System.err.println("meanIOBurst not a number.");
      System.exit(1);
    }

    ExponentialGenerator ioBurst =
      new ExponentialGenerator(random, meanIOBurst);

    double meanNumberBursts = 1.0;
    try {
      meanNumberBursts =
        Double.parseDouble(parameters.getProperty("meanNumberBursts", "10.0"));
    } catch(NumberFormatException e) {
      System.err.println("meanNumberBursts not a number.");
      System.exit(1);
    }
    ExponentialGenerator cycles =
      new ExponentialGenerator(random, meanNumberBursts);

    for (int i=0; i<data.length; i++) {
      int m = cycles.nextInt();
      m = (m % 2 == 0) ? m+1 : m;
      data[i] = new Record(m);
      data[i].priority = priority;
      data[i].arrivalTime = i==0 ? 0 : data[i-1].arrivalTime+arrival.nextInt();
      for (int j=0; j<data[i].bursts.length; j++) {
        if (j%2 == 0)
          data[i].bursts[j] = cpuBurst.nextInt();
        else
          data[i].bursts[j] = ioBurst.nextInt();
      }
    }
  }

  /**
   * Returns the output of the input generator
   */
  public String toString() {
    StringBuffer s = new StringBuffer();
    for (int i=0; i<data.length; i++) {
      s.append(data[i].priority+" "+data[i].arrivalTime);
      for (int j=0; j<data[i].bursts.length; j++)
        s.append(" "+data[i].bursts[j]);
      s.append(System.lineSeparator());
    }
    return s.toString();
  }
}
