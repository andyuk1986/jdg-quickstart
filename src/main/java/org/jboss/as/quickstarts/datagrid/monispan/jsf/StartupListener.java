package org.jboss.as.quickstarts.datagrid.monispan.jsf;

import org.jboss.as.quickstarts.datagrid.monispan.Reporter;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class StartupListener implements SystemEventListener {

   public static final String NODE_NUMBER = "simulatorNodeNum";

   public static final String EXECUTION_FREQUENCY = "execFreq";

   public static final String DEFAULT_NODE_NUMBER = "5";

   public static final String DEFAULT_EXECUTION_FREQUENCY = "10000";

   public static final String PROPERTY_FILE_NAME = "edg.properties";

   //@TODO change
   public static long frequency;

   public static long threadNum;

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

      System.out.println("The node number is: " + threadNum);
      System.out.println("The frequency is: " + frequency);

      System.out.println("Starting Cache ...");
      CacheProvider.getInstance().startCache();

      for(int i = 1; i <= threadNum; i++) {
         String nodeName = "node" + i;
         Reporter r = new Reporter(nodeName);
         Timer t = new Timer();

         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.SECOND, 10);

         t.schedule(r, cal.getTime(), frequency);
         System.out.println("Thread is initialized. Will start work on: " + cal.getTime());
      }
   }

   @Override
   public boolean isListenerForSource(Object source) {
      return source instanceof Application;
   }
}