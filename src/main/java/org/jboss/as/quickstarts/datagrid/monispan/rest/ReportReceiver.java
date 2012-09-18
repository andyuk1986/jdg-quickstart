package org.jboss.as.quickstarts.datagrid.monispan.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Servlet which receives reports regarding user statistics and places the data into the cache.
 *
 * @author Anna Manukyan
 */
@Path("/report")
public class ReportReceiver {

   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

   public static final String OK_RESPONSE = "ok";

   public static final String NOK_RESPONSE = "nok";

   /**
    * Responds to GET requests.
    * @param request          the http request object.
    * @param response         the http response object.
    * @throws IOException
    */
   @GET
   @Path("/report/{cpu}")
   @Produces("text/plain")
   public void doGet(@PathParam("node") String nodeName, @PathParam("userCount") int userCount, @PathParam("notifCount") int sentNotificationCount,
                                @PathParam("subCount") int subscriptionCount, @PathParam("cancels") int cancellationCount) throws IOException {
      String nodeName = request.getParameter("node");
      String userCountStr = request.getParameter("count");
      PrintWriter out = response.getWriter();
      String responseStr = NOK_RESPONSE;

      if(userCountStr != null && !userCountStr.isEmpty()) {
         try {
            int userCount = Integer.parseInt(userCountStr);

            responseStr = receiveReport(nodeName, userCount);
         } catch(Exception ex) {
            System.out.println("The passed parameter is not correct: " + ex.getMessage());
         }
      }

      out.write(responseStr);
   }

   /*
    * The report processing method.
    * @param nodeName            the node name received report from.
    * @param userCount           the count of users received by request.
    * @return                    OK response.
   */
   private String receiveReport(String nodeName, int userCount) {
      System.out.println("CPU usage on " + nodeName + " is:" + userCount);

      CacheProvider provider = CacheProvider.getInstance();
      String key = formatter.format(new Date());

      provider.put(nodeName, key, userCount);

      return OK_RESPONSE;
   }
}
