package org.jboss.as.quickstarts.datagrid.monispan.jsf;

import org.jboss.as.quickstarts.datagrid.monispan.Reporter;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * Listener, which is initialized as soon as JSF application started. The listener initializes the Cache, as well as
 * the simulator threads for future work.
 *
 * @author Anna Manukyan
 */
public class StartupListener implements SystemEventListener {
   /**
    * The name of the property from edg.properties file. Corresponds to the number of simulator nodes.
    */
   public static final String NODE_NUMBER = "simulatorNodeNum";

   /**
    * The name of the property from edg.properties file. Corresponds to the frequency of simulator jobs work.
    */
   public static final String EXECUTION_FREQUENCY = "execFreq";

   /**
    * Default number of simulator nodes, in case the property is not provided.
    */
   public static final String DEFAULT_NODE_NUMBER = "5";

   /**
    * The default number of simulators execution frequency, in case the property is not provided.
    */
   public static final String DEFAULT_EXECUTION_FREQUENCY = "10000";

   /**
    * The name of the properties file.
    */
   public static final String PROPERTY_FILE_NAME = "edg.properties";

   //@TODO change
   public static long frequency;

   public static long threadNum;

   private Logger log = Logger.getLogger(this.getClass().getName());

   /**
    * Loads the property file, and starts the Cache and simulator nodes based on the retrieved data.
    * @param systemEvent
    * @throws AbortProcessingException
    */
   @Override
   public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {
      System.out.println("Context is initialized.");

      Properties prop = new Properties();
      try {
         prop.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME));
      } catch (IOException e) {
         e.printStackTrace();
      }

      threadNum = Integer.parseInt(prop.getProperty(NODE_NUMBER, DEFAULT_NODE_NUMBER));
      frequency = Integer.parseInt(prop.getProperty(EXECUTION_FREQUENCY, DEFAULT_EXECUTION_FREQUENCY));

      log.info("The node number is: " + threadNum);
      log.info("The frequency is: " + frequency);

      log.info("Starting Cache ...");
      CacheProvider.getInstance().startCache();

      for(int i = 1; i <= threadNum; i++) {
         String nodeName = "node" + i;
         Reporter r = new Reporter(nodeName);
         Timer t = new Timer();

         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.SECOND, 5);

         t.schedule(r, cal.getTime(), frequency);
         log.info("Thread is initialized. Will start work on: " + cal.getTime());
      }
   }

   @Override
   public boolean isListenerForSource(Object source) {
      return source instanceof Application;
   }
}