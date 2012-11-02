package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupInitListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet which receives reports regarding user statistics. All data is processed and stored in the cache.
 * The service is accessible via /report path.
 *
 * @author Anna Manukyan
 */
@Path("/report")
@ManagedBean
public class ReportReceiverRestService {
   private static Map<String, List<Report>> groupMap = new HashMap<String, List<Report>>();

   public static final String OK_RESPONSE = "ok";
   private static Date firstReportDate = null;
   private static Date nextReportDate = null;

   @Inject
   private CacheProvider cacheProvider;

   @Inject
   ReportStatisticsProvider reportStatisticsProvider;

   @GET
   @Path("{userCount}/{notifCount}/{date}")
   @Produces("text/plain")
   public String doGet(@PathParam("userCount") int userCount, @PathParam("notifCount") int sentNotificationCount,
                                @PathParam("date") String date) throws IOException, ParseException {

      Report report = new Report(null, userCount, sentNotificationCount, date);
      return processReport(report);
   }

   /*
    * The report processing method.
    * @param nodeName            the node name received report from.
    * @return                    OK response.
   */
   private String processReport(Report report) throws ParseException {
      setFirstReportDate(report);

      synchronized (groupMap) {
         String key = ReportStatisticsProvider.GENERAL_DATE_FORMATTER.format(nextReportDate);
         List<Report> reportSet = groupMap.get(key);
         if(reportSet == null) {
            reportSet = new ArrayList<Report>();
            groupMap.put(key, reportSet);
         }

         reportSet.add(report);

         if(reportSet.size() == StartupInitListener.getThreadNum()) {
            Report finalReport = reportStatisticsProvider.getTotalReport(reportSet);
            cacheProvider.put(CacheProvider.REPORT_CACHE_NAME, key, finalReport);

            groupMap.remove(key);

            Calendar cal = Calendar.getInstance();
            cal.setTime(nextReportDate);
            cal.add(Calendar.MILLISECOND, (int) StartupInitListener.getFrequency());

            nextReportDate = cal.getTime();
         }
      }

      return OK_RESPONSE;
   }

   /**
    * Stores the date when the first report has been sent.
    * @param report        the report from which the date should be picked.
    */
   public static synchronized void setFirstReportDate(Report report) {
      if(firstReportDate == null) {
         try {
            firstReportDate = ReportStatisticsProvider.GENERAL_DATE_FORMATTER.parse(report.getReportDate());
            nextReportDate = firstReportDate;
         } catch (ParseException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Returns the date when the first report has been sent.
    * @return              the date when the first report has been sent.
    */
   public static Date getFirstReportDate() {
      return firstReportDate;
   }
}
