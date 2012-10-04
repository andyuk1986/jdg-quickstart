package org.jboss.as.quickstarts.datagrid.monispan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Node simulator which works periodically by given time. It simulates web site monitoring report.
 * It generates random numbers for each type of report -
 * user counts, sent notif count, subscription & cancellation counts and performs HTTP request using REST API
 * request to listener.
 *
 * @author Anna Manukyan
 */
public class Reporter extends TimerTask {
   public static final String REPORT_URL = "http://localhost:8180/jboss-as-monispan/rest/report";

   private Logger log = Logger.getLogger(this.getClass().getName());

   private String name;

   public Reporter(String name) {
      this.name = name;
   }

   /**
    * Generates random numbers for each type of report and performs http request using REST api.
    */
   @Override
   public void run() {
      log.info("Performing Report: " + name);
      try {
         report();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void report() throws Exception {
      Random rand = new Random();
      int sampleUserCount = rand.nextInt(10000);
      int sentNotificationCount = rand.nextInt(20000);
      int subscriptionCount = rand.nextInt(5000);
      int cancellationCount= rand.nextInt(1000);

      String dateFormatted = ReportStatisticsProvider.GENERAL_DATE_FORMATTER.format(new Date());
      StringBuffer urlStr = new StringBuffer();
      urlStr.append(REPORT_URL).append("/").append(sampleUserCount).append("/").append(sentNotificationCount)
            .append("/").append(subscriptionCount).append("/").append(cancellationCount).append("/").append(dateFormatted);

      URL url = new URL(urlStr.toString());
      URLConnection conn = url.openConnection ();

      // Get the response
      InputStreamReader reader = new InputStreamReader(conn.getInputStream());
      BufferedReader rd = new BufferedReader(reader);
      String line = rd.readLine();

      rd.close();
      reader.close();
   }
}
