package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.stats.Stats;

import java.util.HashMap;
import java.util.Map;

/**
 * Class providing statistics regarding Cache usage.
 *
 * @author Anna Manukyan
 */
public class CacheStatisticsProvider {

   private Stats statistics;

   /**
    * Constructor, gets the DefaultCacheManager object and retrieves the statistics from it.
    */
   public CacheStatisticsProvider() {
      DefaultCacheManager manager = CacheProvider.getInstance().getCacheManager();
      statistics = manager.getCache(CacheProvider.REPORT_CACHE_NAME).getAdvancedCache().getStats();
   }

   /**
    * Returns the Cache Usage Statistics as Map.
    * @return        the map, which contains cache usage statistics.
    */
   public Map<String, Long> getCacheStatisticsAsMap() {
      Map<String, Long> stats = new HashMap<String, Long>();

      stats.put("Total number of entries in the cache", getTotalNumberOfEntries());
      stats.put("Current number of entries", (long) getCurrentNumberOfEntries());
      stats.put("Number Of Evictions", getEvictions());
      stats.put("Number of Retrievals", getRetrievals());
      stats.put("Number of stores", getStores());

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
      return CacheProvider.getInstance().getNotifListener().getCounter();
   }

   /**
    * Number of total stores to the cache.
    * @return           the number of total stores to the cache.
    */
   public long getStores() {
      return statistics.getStores();
   }

   /**
    * Returns the number of total retreivals from the cache.
    * @return           the number of all retreivals from the cache.
    */
   public long getRetrievals() {
      return statistics.getRetrievals();
   }
}
