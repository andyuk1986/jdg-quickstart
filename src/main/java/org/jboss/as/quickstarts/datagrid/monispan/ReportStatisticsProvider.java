package org.jboss.as.quickstarts.datagrid.monispan;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupInitListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;
import org.jboss.as.quickstarts.datagrid.monispan.rest.ReportReceiverRestService;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which provides custom statistics from the different node executors.
 *
 * @author Anna Manukyan
 */
@ManagedBean
public class ReportStatisticsProvider {

   @Inject
   private CacheProvider cacheProvider;

   /**
    * The format to which the date is corresponds in the project.
    */
   public static SimpleDateFormat GENERAL_DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");
   private long executionTimeInMillis = 0;

   /**
    * Returns the "user statistics" - Total and average reports based on got data. Depending on the parameter, the report
    * is full or partial, i.e. in case of the full report the whole data is picked (also from the storage).
    *
    * @param isFullReport        defines whether the report should be full or partial.
    * @return                    the List object which contains Total and Average reports.
    */
   public List<Report> getReportStatistics(final boolean isFullReport) {
      List<Report> reportList = new ArrayList<Report>();
      Map<String, Report> cacheElems = getEntriesFromCache(isFullReport);

      List<Report> valuesList= new ArrayList<Report>();
      valuesList.addAll(cacheElems.values());

      Report totalReport = getTotalReport(valuesList);
      reportList.add(totalReport);

      int cacheSize = cacheElems.size();
      if(cacheSize > 0) {
         Report averageReport = new Report("avg", cacheSize > 0 ? totalReport.getUserCount() / cacheSize : 0,
                                           cacheSize > 0 ? totalReport.getSentNotificationCount() / cacheSize : 0, null);
         averageReport.setReportName("Average Numbers");
         reportList.add(averageReport);
      }

      return reportList;
   }

   /**
    * Returns all (both current and stored in the storage) entries from the cache.
    * @param  isFullReport specifies whether the full report is needed or no
    * @return              all entries from the cache as a Map.
    */
   public Map<String, Report> getEntriesFromCache(final boolean isFullReport) {
      long startTime = System.currentTimeMillis();

      Map<String, Report> cacheEntries = new HashMap<String, Report>();

      Date dateKey = getFirstAvailableKey(isFullReport);

      long notifFrequency = StartupInitListener.getFrequency();
      Date currentDate = new Date();

      while(dateKey.before(currentDate)) {
         String key = GENERAL_DATE_FORMATTER.format(dateKey);
         Report rep = cacheProvider.getCache(CacheProvider.REPORT_CACHE_NAME).get(key);

         if(rep != null) {
            cacheEntries.put(key, rep);
         }

         Calendar cal = Calendar.getInstance();
         cal.setTime(dateKey);
         cal.add(Calendar.MILLISECOND, (int) notifFrequency);

         dateKey = cal.getTime();
      }

      executionTimeInMillis = System.currentTimeMillis() - startTime;

      Map<String,Report> elementsInCache = cacheProvider.getCache(CacheProvider.REPORT_CACHE_NAME);
      for(String elem : elementsInCache.keySet()) {
         System.out.println(elem);
      }

      return cacheEntries;
   }

   /**
    * Returns the first available key depending on whether the full report should be generated or the short one.
    *
    * @param isFullReportNeeded        identifies whether the full report is needed.
    * @return                          the first available key for retrieving data from cache.
    */
   private Date getFirstAvailableKey(final boolean isFullReportNeeded) {
      Date firstKey = null;
      if(isFullReportNeeded) {
         firstKey = ReportReceiverRestService.getFirstReportDate();
      } else {
         Calendar now = Calendar.getInstance();
         now.add(Calendar.MINUTE, -CacheProvider.DATA_SHOW_MINUTES);
         firstKey = now.getTime();

         boolean isCorrectKeyFound = false;
         Date currentDate = new Date();
         while(!isCorrectKeyFound && firstKey.before(currentDate)) {
            if(cacheProvider.getCache(CacheProvider.REPORT_CACHE_NAME).get(GENERAL_DATE_FORMATTER.format(firstKey)) != null) {
               isCorrectKeyFound = true;
            } else {
               Calendar c = Calendar.getInstance();
               c.setTime(firstKey);
               c.add(Calendar.SECOND, 1);

               firstKey = c.getTime();
            }
         }
      }

      return firstKey;
   }

   /**
    * Returns the report which shows total numbers. I.e. total numbers are counted for user counts, sent notif counts,
    * subscription counts nad cancellation counts from the given List of reports.
    *
    * @param reportSet           the List of reports which total should be calculated.
    * @return                    single report which contains the total numbers.
    */
   public Report getTotalReport(List<Report> reportSet) {
      int userCountSum = 0;
      int sentNotifSum = 0;
      for(Report rep : reportSet) {
         userCountSum += rep.getUserCount();
         sentNotifSum += rep.getSentNotificationCount();
      }

      Report totalReport = new Report("total", userCountSum, sentNotifSum, null);
      totalReport.setReportName("Total Numbers");

      return totalReport;
   }

   /**
    * Returns the execution time in seconds.
    * @return           the list retrieval execution time in seconds.
    */
   public long getExecutionTimeInMillis() {
      return executionTimeInMillis;
   }
}
