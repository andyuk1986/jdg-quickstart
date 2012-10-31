package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.stats.Stats;
import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class providing statistics regarding Cache usage.
 *
 * @author Anna Manukyan
 */
@RequestScoped
public class CacheStatisticsProvider {

   private Stats statistics;

   @Inject
   CacheProvider cacheProvider;

   @Inject
   ReportStatisticsProvider reportStatisticsProvider;

   /**
    * Gets the DefaultCacheManager object and retrieves the statistics from it.
    */
   @PostConstruct
   public void init() {
      DefaultCacheManager manager = cacheProvider.getCacheManager();
      statistics = manager.getCache(CacheProvider.REPORT_CACHE_NAME).getAdvancedCache().getStats();
   }

   /**
    * Returns the Cache Usage Statistics as Map.
    * The cache statistics consists of the following elements;
    *
    * Number of Hits - When the application generates keys and gets the data according to the keys, the hits number
    *                  is increased.
    *
    * Number of misses - While the application generates the key, and it tries out each key to find the first available
    *                    one. In case of wrong keys, the number of misses is incremented.
    * @return        the map, which contains cache usage statistics.
    */
   public Map<String, Long> getCacheStatisticsAsMap() {
      Map<String, Long> stats = new LinkedHashMap<String, Long>();

      stats.put("Number of Hits", getHits());
      stats.put("Number of Misses", getMisses());

      stats.put("Number of Evictions During Last Execution", (long) getEvictions());
      stats.put("Number of Passivations During Last Execution", (long) getPassivations());
      stats.put("Number of Loads During Last Execution", (long) getLoads());

      stats.put("Current number of entries", (long) getCurrentNumberOfEntries());
      stats.put("Total number of entries in the cache", getTotalNumberOfEntries());
      stats.put("Duration of Data Retrieval in MilliSeconds", reportStatisticsProvider.getExecutionTimeInMillis());



      return stats;
   }

   /**
    * Returns the current number of entries in the cache.
    * @return           the current number of entries in the cache.
    */
   public int getCurrentNumberOfEntries() {
      return statistics.getCurrentNumberOfEntries();
   }

   /**
    * Returns the total number of entries in the cache.
    * @return           the total number of entries in the cache.
    */
   public long getTotalNumberOfEntries() {
      return statistics.getTotalNumberOfEntries();
   }

   /**
    * Returns the number of hits from the cache.
    *
    * The number of hits should be usually equal to entryCountInMinute + 1. The last 1 is because of first available key
    * generation. As soon as the value with specified generated key is found, that key is returned, which means the number
    * of hits will be always more with 1.
    *
    * @return           the number of all hits from the cache.
    */
   public long getHits() {
      return statistics.getHits();
   }

   /**
    * Returns the number of misses from the cache.
    *
    * The number of hits is the number of attempts to get the value with generated key for finding out the first valid key.
    * @return           the number of all misses from the cache.
    */
   public long getMisses() {
      return statistics.getMisses();
   }

   /**
    * Returns the number of activations from the cache during the last execution.
    * @return        the number of activations during the last execution.
    */
   public int getLoads() {
      return cacheProvider.getNotifListener().getLoadedCounter();
   }

   /**
    * Returns the number of passivations during the last execution.
    * @return        the number of passivations during the last execution.
    */
   public int getPassivations() {
      return cacheProvider.getNotifListener().getPassivationCounter();
   }

   /**
    * Returns the number of evictions during the last execution.
    * @return        the number of evictions during the last execution.
    */
   public int getEvictions() {
      return cacheProvider.getNotifListener().getEvictionCounter();
   }
}
