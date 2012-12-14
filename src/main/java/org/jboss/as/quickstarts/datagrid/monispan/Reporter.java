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
public class Reporter implements Runnable {
   /**
    * URL of the page to which report should be sent.
    */
   public static final String REPORT_URL = "/rest/report";

   private Logger log = Logger.getLogger(this.getClass().getName());

   private String name;
   private String webUrl;

   public Reporter(String name, String webUrl) {
      this.name = name;
      this.webUrl = webUrl;
   }

   /**
    * Generates random numbers for each type of report and performs http request using REST api.
    */
   @Override
   public void run() {
      try {
         report();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void report() throws Exception {
      Random rand = new Random();
      int sampleUserCount = rand.nextInt(500);
      int sentNotificationCount = rand.nextInt(500);

      String dateFormatted = ReportStatisticsProvider.GENERAL_DATE_FORMATTER.format(new Date());
      log.info("Report sent at " + dateFormatted);
      StringBuffer urlStr = new StringBuffer(webUrl);
      urlStr.append(REPORT_URL).append("/").append(sampleUserCount).append("/").append(sentNotificationCount)
            .append("/").append(dateFormatted);

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
