package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.stats.Stats;
import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

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
      Map<String, Long> stats = new HashMap<String, Long>();

      stats.put("Duration of Data Retrieval in MilliSeconds", reportStatisticsProvider.getExecutionTimeInMillis());
      stats.put("Total number of entries in the cache", getTotalNumberOfEntries());
      stats.put("Current number of entries", (long) getCurrentNumberOfEntries());
      stats.put("Number Of Evictions", getEvictions());
      stats.put("Number of Hits", getHits());
      stats.put("Number of Misses", getMisses());
      stats.put("Number of Stores To Cache", getStores());

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
    * Returns the number of evictions in the cache. Note: as the getEvictions() of Stat class, works with some other
    * logic, the evictions are counted manually using async listener.
    *
    * @return           the number of performed evictions.
    */
   public long getEvictions() {
      return cacheProvider.getNotifListener().getCounter();
   }

   /**
    * Number of total stores to the cache.
    * @return           the number of total stores to the cache.
    */
   public long getStores() {
      return statistics.getStores();
   }

   /**
    * Returns the number of hits from the cache.
    * @return           the number of all hits from the cache.
    */
   public long getHits() {
      return statistics.getHits();
   }

   /**
    * Returns the number of misses from the cache.
    * @return           the number of all misses from the cache.
    */
   public long getMisses() {
      return statistics.getMisses();
   }
}
