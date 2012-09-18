package org.jboss.as.quickstarts.datagrid.monispan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.TimerTask;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class Reporter extends TimerTask {

   public static final String REPORT_URL = "http://localhost:8080/monispan/report";
   private String name;

   public Reporter(String name) {
      this.name = name;
   }

   @Override
   public void run() {
      System.out.println("Performing Report");
      try {
         report();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void report() throws Exception {
      Random rand = new Random();
      int sampleUserCount = rand.nextInt(10000);

      StringBuffer urlStr = new StringBuffer();
      urlStr.append(REPORT_URL).append("?node=").append(name).append("&count=").append(sampleUserCount);

      URL url = new URL(urlStr.toString());
      URLConnection conn = url.openConnection ();

      // Get the response
      InputStreamReader reader = new InputStreamReader(conn.getInputStream());
      BufferedReader rd = new BufferedReader(reader);
      String line = rd.readLine();

      System.out.println("The response is: " + line);

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
