package org.jboss.as.quickstarts.datagrid.monispan;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public final class ReportStatisticsProvider {

   private static ReportStatisticsProvider instance;

   private ReportStatisticsProvider() {

   }

   public static ReportStatisticsProvider getInstance() {
      if(instance == null) {
         instance = new ReportStatisticsProvider();
      }
      return instance;
   }

   public List<Report> getReportStatistics() {
      List<Report> reportList = new ArrayList<Report>();
      Map<String, Report> cacheElems = CacheProvider.getInstance().getCache(CacheProvider.REPORT_CACHE_NAME);

      int userCountSum = 0;
      int sentNotifSum = 0;
      int subscriptionSum = 0;
      int cancellationSum = 0;

      for (Map.Entry<String, Report> reportEntry : cacheElems.entrySet()) {
         Report report = reportEntry.getValue();

         userCountSum += report.getUserCount();
         sentNotifSum += report.getSentNotificationCount();
         subscriptionSum += report.getSubscribtionCount();
         cancellationSum += report.getCancellationCount();
      }

      Report totalReport = new Report("total", userCountSum, sentNotifSum, subscriptionSum, cancellationSum, null);
      totalReport.setReportName("Total Numbers");
      reportList.add(totalReport);

      int cacheSize = cacheElems.size();
      Report averageReport = new Report("avg", userCountSum / cacheSize, sentNotifSum / cacheSize,
                                        subscriptionSum / cacheSize, cancellationSum / cacheSize, null);
      averageReport.setReportName("Average Numbers");
      reportList.add(averageReport);
      return reportList;
   }

}
