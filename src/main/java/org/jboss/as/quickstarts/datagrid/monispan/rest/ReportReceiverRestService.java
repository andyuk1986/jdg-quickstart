package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servlet which receives reports regarding user statistics and places the data into the cache.
 *
 * @author Anna Manukyan
 */
@Path("/report")
public class ReportReceiverRestService {

   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

   public static final String OK_RESPONSE = "ok";

   @GET
   @Path("{node}/{userCount}/{notifCount}/{subCount}/{cancels}")
   @Produces("text/plain")
   public String doGet(@PathParam("node") String nodeName, @PathParam("userCount") int userCount, @PathParam("notifCount") int sentNotificationCount,
                                @PathParam("subCount") int subscriptionCount, @PathParam("cancels") int cancellationCount) throws IOException {

      Report report = new Report(nodeName, userCount, sentNotificationCount, subscriptionCount, cancellationCount);
      return receiveReport(report);
   }

   /*
    * The report processing method.
    * @param nodeName            the node name received report from.
    * @return                    OK response.
   */
   private String receiveReport(Report report) {
      CacheProvider provider = CacheProvider.getInstance();
      String key = formatter.format(new Date());

      provider.put(CacheProvider.REPORT_CACHE_NAME, key, report);

      return OK_RESPONSE;
   }
}
