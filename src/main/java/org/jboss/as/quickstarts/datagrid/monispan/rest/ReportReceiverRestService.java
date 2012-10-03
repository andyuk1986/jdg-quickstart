package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet which receives reports regarding user statistics and places the data into the cache.
 *
 * @author Anna Manukyan
 */
@Path("/report")
public class ReportReceiverRestService {

   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");
   private static Map<String, List<Report>> groupMap = new HashMap<String, List<Report>>();

   public static final String OK_RESPONSE = "ok";

   @GET
   @Path("{node}/{userCount}/{notifCount}/{subCount}/{cancels}/{date}")
   @Produces("text/plain")
   public String doGet(@PathParam("node") String nodeName, @PathParam("userCount") int userCount, @PathParam("notifCount") int sentNotificationCount,
                                @PathParam("subCount") int subscriptionCount, @PathParam("cancels") int cancellationCount,
                                @PathParam("date") String date) throws IOException {

      Report report = new Report(null, userCount, sentNotificationCount, subscriptionCount, cancellationCount, date);
      return processReport(report);
   }

   /*
    * The report processing method.
    * @param nodeName            the node name received report from.
    * @return                    OK response.
   */
   private String processReport(Report report) {
      CacheProvider provider = CacheProvider.getInstance();
      String key = report.getReportDate();

      synchronized (groupMap) {
         List<Report> reportList = groupMap.get(key);
         if(reportList == null) {
            reportList = new ArrayList<Report>();
            groupMap.put(key, reportList);
         }

         reportList.add(report);
         if(reportList.size() == StartupListener.threadNum) {
            int userCountSum = 0;
            int sentNotifSum = 0;
            int subscriptionSum = 0;
            int cancellationSum = 0;
            for(Report rep : reportList) {
               userCountSum += rep.getUserCount();
               sentNotifSum += rep.getSentNotificationCount();
               subscriptionSum += rep.getSubscribtionCount();
               cancellationSum += rep.getCancellationCount();
            }

            Report finalReport = new Report(null, userCountSum, sentNotifSum, subscriptionSum, cancellationSum, key);
            provider.put(CacheProvider.REPORT_CACHE_NAME, key, finalReport);

            groupMap.remove(key);
         }

         System.out.println("The group map size is:  " + groupMap.size());
      }

      return OK_RESPONSE;
   }
}
