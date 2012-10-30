package org.jboss.as.quickstarts.datagrid.monispan;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupInitListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;
import org.jboss.as.quickstarts.datagrid.monispan.rest.ReportReceiverRestService;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class performs all related actions to the cache in order to retrieve and calculate the statistics data.
 * As an introduction, the application simulates the monitoring system. It consists of simulator threads, which
 * works periodically with the provided frequency.
 *
 * The threads are performing REST calls to the application 2nd part, which parses and merges the report and puts the
 * data into the cache.
 *
 * The application provides the WEB UI, which has 2 views:
 *
 * 1. The full data report view - the whole data is shown as a graph as well as average calculated numbers.
 *
 * 2. The recent data report view - the data for the last minute is shown with it's corresponding graph and avg
 *    calculations.
 *
 * The methods in this class are interactions with the cache for providing functionality described above.
 * @author Anna Manukyan
 */
@ApplicationScoped
public class ReportStatisticsProvider {

   @Inject
   CacheProvider cacheProvider;

   /**
    * The format to which the date is corresponds in the project.
    */
   public static SimpleDateFormat GENERAL_DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");
   private long executionTimeInMillis = 0;

   private static final int SECOND_IN_MILLIS = 1000;

   /**
    * Returns entries from the cache based on the provided parameter.
    * If the full report is needed, i.e. <code>isFullReport</code> is <code>true</code>, then the full data is retrieved
    * from the cache, otherwise only the data for the last minute.
    *
    * The steps of execution are following:
    *
    * 1. The method retrieves the first available key depending on whether the full data is needed or only the latest one.
    *
    * 2. Then based on the returned key, the method gets the entry from the cache based on the got key. Note, that the
    *    key/value pair may be either immediately available or may be evicted to CacheStore.
    *
    *    Cache Eviction Strategy is LRU (Last Recently Used) and the cache max entries number is calculated
    *    according to the provided numbers of simulator jobs execution frequency. E.g. if the execution frequency is set
    *    to 6 seconds, then the number of max entries is set to 10 (as if on report is put to the cache each 6 seconds,
    *    there would be 10 reports in a minute maximum).
    *
    *    Taking into account, that Eviction's maxEntries is used as guide for the entire cache, but eviction happens on
    *    per cache segment (so when the segment is full, the segment is evicted), there is no guarantee that the cache
    *    will contain all reports for the last minute as some data may be evicted in the meantime.
    *
    *    That's why even the data for the last minute is taken key by key and not just as keySet.
    *
    * 3. The next key is generated according to the frequency of reports push to the cache. E.g. the next key would be
    *    current key (as date) + job_execution_frequency (in milliseconds).
    *
    * @param  isFullReport          specifies whether the full report is needed or no
    * @return                       all entries from the cache as a Map.
    */
   public Map<String, Report> getEntriesFromCache(final boolean isFullReport) {
      long startTime = System.currentTimeMillis();

      Map<String, Report> cacheEntries = new HashMap<String, Report>();
      long notifFrequency = StartupInitListener.getFrequency();
      //The first key is got. The first key is either the date when the very first report was put to the cache (if the full
      //report is necessary, or is the first available key withing the last minute.
      Date dateKey = getFirstAvailableKey(isFullReport);

      Date currentDate = new Date();

      //looping till the generated key doesn't exceed the current date
      while (dateKey.before(currentDate)) {
         String key = GENERAL_DATE_FORMATTER.format(dateKey);

         //Retrieves the cache entry by generated key.
         //The entry may be either immediately available or may be evicted and stored in CacheStore
         Report rep = cacheProvider.getCache(CacheProvider.REPORT_CACHE_NAME).get(key);

         if(rep != null) {
            cacheEntries.put(key, rep);
         }

         //The next key is generated according to the frequency of placing reports to the cache. The next key
         //should be current_key (as date) + job_execution_frequency(in milliseconds)
         dateKey = getNextPossibleDateKey(dateKey, (int) notifFrequency);
      }

      executionTimeInMillis = System.currentTimeMillis() - startTime;

      return cacheEntries;
   }

   /**
    * Generates key for retrieving data from the cache. The key is generated with the following logic:
    *
    * as the report is sent to system with identified frequency, and as the key represents the date when the report has
    * been placed into the cache, the following happens:
    *
    * 1. The system stores the date when the very first report was placed into the cache;
    * 2. This date is got from the cache, and by adding the execution frequency time, the system gets the next keys.
    *
    * @param currentDateKey                  the last executed key.
    * @return                                the newly generated key which is last executed key + frequency
    */
   private Date getNextPossibleDateKey(final Date currentDateKey, final int period) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(currentDateKey);

      cal.add(Calendar.MILLISECOND, period);

      return cal.getTime();
   }

   /**
    * Returns the first available key depending on whether the full report should be generated or the short one.
    *
    * The short report shows the data for the last minute. The full report shows the data starting from the app execution.
    *
    * In case of the full report, the first available key is got from stored variable. The system saves the date when the
    * first report has been stored to the cache.
    *
    * In case of the short report, the first key is got in the following way:
    *
    * 1. The system generates date with -1 minute difference than now.
    *
    * 2. Then the application tries to get data with the generated key. If the data is found, then this generated key
    *    is the one to return.
    *
    * 3. If there is no data available with the generated key, then the application works in a loop by adding a second to
    *    the generated key and checking whether there is data available with that key. If yes, then the key is returned,
    *    otherwise the loop continues to add a second and to check till the date doesn't reach the current date.
    *
    * @param isFullReportNeeded        identifies whether the full report is needed.
    * @return                          the first available key for retrieving data from cache.
    */
   private Date getFirstAvailableKey(final boolean isFullReportNeeded) {
      Date firstKey = null;
      if(isFullReportNeeded) {
         //If full report is needed, then the very first report date is picked.
         firstKey = ReportReceiverRestService.getFirstReportDate();
      } else {
         //The first key is current_date - 1 minute
         Calendar now = Calendar.getInstance();
         now.add(Calendar.MINUTE, -CacheProvider.DATA_SHOW_MINUTES);
         firstKey = now.getTime();

         boolean isCorrectKeyFound = false;
         Date currentDate = new Date();
         while(!isCorrectKeyFound && firstKey.before(currentDate)) {
            String date = GENERAL_DATE_FORMATTER.format(firstKey);

            //If the data with the provided key is found, then this is the key to return
            if(cacheProvider.getCache(CacheProvider.REPORT_CACHE_NAME).get(date) != null) {
               isCorrectKeyFound = true;
            } else {
               //If the data is not found, then the key is incremented with a second.
               firstKey = getNextPossibleDateKey(firstKey, SECOND_IN_MILLIS);
            }
         }
      }

      return firstKey;
   }

   /**
    * Returns the report which shows total numbers.
    * The report contains the total number of users and sent notification counts.
    *
    * @param reportSet           list which contains the reports from the cache.
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
    * Returns the execution time of data retrieval from the cache in seconds. Is necessary for statistics.
    * @return           the list retrieval execution time in seconds.
    */
   public long getExecutionTimeInMillis() {
      return executionTimeInMillis;
   }
}
