package org.jboss.as.quickstarts.datagrid.monispan.jsf;

import org.jboss.as.quickstarts.datagrid.monispan.Reporter;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Listener, which is initialized as soon as JSF application started. The listener initializes the Cache, as well as
 * the simulator threads for future work.
 *
 * @author Anna Manukyan
 */
public class StartupInitListener implements ServletContextListener {
   /**
    * The name of the property from jdg.properties file. Corresponds to the number of simulator nodes.
    */
   public static final String NODE_NUMBER = "simulatorNodeNum";

   /**
    * The name of the property from jdg.properties file. Corresponds to the frequency of simulator jobs work.
    */
   public static final String EXECUTION_FREQUENCY = "execFreq";

   /**
    * The name of the property from jdg.properties file. Corresponds to the server host to use in simulator thread.
    */
   public static final String SERVER_HOST= "serverHost";

   /**
    * The name of the property from jdg.properties file. Corresponds to the application server port.
    */
   public static final String SERVER_PORT= "serverPort";

   /**
    * The default value for server host, if is not provided.
    */
   public static final String DEFAULT_SERVER_HOST= "localhost";

   /**
    * Thedefault value for server port, if is not provided.
    */
   public static final int DEFAULT_SERVER_PORT= 8080;

   /**
    * Default number of simulator nodes, in case the property is not provided.
    */
   public static final String DEFAULT_NODE_NUMBER = "5";

   /**
    * The default number of simulators execution frequency, in case the property is not provided.
    */
   public static final String DEFAULT_EXECUTION_FREQUENCY = "10000";

   /**
    * The duration of time in minutes for which the recent report should be shown.
    */
   public static final String DATA_SHOW_MINUTES = "dataShowMinutes;";

   /**
    * The default duration of time in minutes for whch the recent report should be shown.
    */
   public static final String DEFAULT_DATA_SHOW_MINUTES = "1";

   /**
    * The name of the properties file.
    */
   public static final String PROPERTY_FILE_NAME = "jdg.properties";

   private static long frequency;
   private static int threadNum;
   private static int dataShowMinutes;

   private Logger log = Logger.getLogger(this.getClass().getName());

   @Inject
   private CacheProvider cacheProvider;

   /**
    * Loads the property file, and starts the Cache and simulator nodes based on the retrieved data.
    * @param servletContextEvent
    */
   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      log.info("Context is initialized.");

      Properties prop = new Properties();
      try {
         prop.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME));
      } catch (IOException e) {
         e.printStackTrace();
      }

      threadNum = Integer.parseInt(prop.getProperty(NODE_NUMBER, DEFAULT_NODE_NUMBER));
      frequency = Long.parseLong(prop.getProperty(EXECUTION_FREQUENCY, DEFAULT_EXECUTION_FREQUENCY));
      dataShowMinutes = Integer.parseInt(prop.getProperty(DATA_SHOW_MINUTES, DEFAULT_DATA_SHOW_MINUTES));

      String serverHost = prop.getProperty(SERVER_HOST, DEFAULT_SERVER_HOST);
      int serverPort = Integer.parseInt(prop.getProperty(SERVER_PORT, ""  + DEFAULT_SERVER_PORT));
      String contextUrl = servletContextEvent.getServletContext().getContextPath();

      StringBuffer applicationUrl = new StringBuffer("http://").append(serverHost).append(":").append(serverPort).append(contextUrl);

      log.info("The node number is: " + threadNum);
      log.info("The frequency is: " + frequency);
      log.info("The data show minutes is: " + dataShowMinutes);

      log.info("Starting Cache ...");
      cacheProvider.startCache();

      ScheduledExecutorService s = Executors.newScheduledThreadPool(threadNum);
      for(int i = 1; i <= threadNum; i++) {
         String nodeName = "node" + i;
         Reporter r = new Reporter(nodeName, applicationUrl.toString());

         s.scheduleWithFixedDelay(r, 5000, frequency, TimeUnit.MILLISECONDS);
      }
   }

   /**
    * Getter for frequency property. Shows the frequency with which the simulator jobs should work.
    * @return              the frequency falue from properties file.
    */
   public static long getFrequency() {
      return frequency;
   }

   /**
    * Returns the number of simulator threads to be run.
    * @return              the number of threads to run.
    */
   public static int getThreadNum() {
      return threadNum;
   }

   /**
    * Returns the duration of time in minutes during which the recent report should be shown.
    * @return              duration in minutes.
    */
   public static int getDataShowMinutes() {
      return dataShowMinutes;
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      log.info("Context Destroyed!!!!!");
   }
}