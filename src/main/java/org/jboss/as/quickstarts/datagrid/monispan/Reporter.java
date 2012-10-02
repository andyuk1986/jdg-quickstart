package org.jboss.as.quickstarts.datagrid.monispan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class Reporter extends TimerTask {

   public static final String REPORT_URL = "http://localhost:8180/jboss-as-monispan/rest/report";
   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");

   private String name;

   public Reporter(String name) {
      this.name = name;
   }

   @Override
   public void run() {
      System.out.println("Performing Report: " + name);
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

      String dateFormatted = formatter.format(new Date());

      StringBuffer urlStr = new StringBuffer();
      urlStr.append(REPORT_URL).append("/").append(name).append("/").append(sampleUserCount).append("/").append(sentNotificationCount)
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

   public String getName() {
      return name;
   }

   public void setName(final String name) {
      this.name = name;
   }

}
