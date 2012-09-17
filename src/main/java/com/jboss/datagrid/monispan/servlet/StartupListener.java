package com.jboss.datagrid.monispan.servlet;

import com.jboss.datagrid.monispan.Reporter;
import com.jboss.datagrid.monispan.cache.CacheProvider;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
public class StartupListener implements ServletContextListener {

   public static final String NODE_NUMBER = "simulatorNodeNum";

   public static final String EXECUTION_FREQUENCY = "execFreq";

   public static final String DEFAULT_NODE_NUMBER = "5";

   public static final String DEFAULT_EXECUTION_FREQUENCY = "10000";

   public static final String PROPERTY_FILE_NAME = "edg.properties";

   //@TODO change
   public static String realApplicationPath;
   public static long frequency;

   public void contextInitialized(ServletContextEvent event) {
      System.out.println("Context is initialized.");

      Properties prop = new Properties();
      try {
         prop.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME));
      } catch (IOException e) {
         e.printStackTrace();
      }

      int threadNum = Integer.parseInt(prop.getProperty(NODE_NUMBER, DEFAULT_NODE_NUMBER));
      frequency = Integer.parseInt(prop.getProperty(EXECUTION_FREQUENCY, DEFAULT_EXECUTION_FREQUENCY));

      System.out.println("The node number is: " + threadNum);
      System.out.println("The frequency is: " + frequency);

      for(int i = 1; i <= threadNum; i++) {
         String nodeName = "node" + i;
         Reporter r = new Reporter(nodeName);
         Timer t = new Timer();

         Calendar cal = Calendar.getInstance();
         System.out.println(cal.getTime());
         cal.add(Calendar.SECOND, i);

         t.schedule(r, cal.getTime(), frequency);
         CacheProvider.getInstance().startCache(nodeName);

         System.out.println("Thread is initialized.");
      }

      realApplicationPath = event.getServletContext().getRealPath("/");
   }

   public void contextDestroyed(ServletContextEvent event) {
      System.out.println("Context is destroyed.");
   }
}