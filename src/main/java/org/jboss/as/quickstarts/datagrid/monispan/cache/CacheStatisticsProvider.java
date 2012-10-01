package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.stats.Stats;
import org.jboss.as.quickstarts.datagrid.monispan.model.CacheStats;

import java.util.HashMap;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class CacheStatisticsProvider {

   private Stats statistics;

   public CacheStatisticsProvider() {
      DefaultCacheManager manager = CacheProvider.getInstance().getCacheManager();
      statistics = manager.getCache(CacheProvider.REPORT_CACHE_NAME).getAdvancedCache().getStats();
   }

   public CacheStats getCacheStatistics() {
      CacheStats stats = new CacheStats(getCurrentNumberOfEntries(), getTotalNumberOfEntries(), getEvictions(),
                                        getStores(), getRetrievals());

      return stats;
   }

   public Map<String, Long> getCacheStatisticsAsMap() {
      Map<String, Long> stats = new HashMap<String, Long>();

      stats.put("Total number of entries in the cache", statistics.getTotalNumberOfEntries());
      stats.put("Current number of entries", (long) statistics.getCurrentNumberOfEntries());
      stats.put("Number Of Evictions", statistics.getEvictions());
      stats.put("Number of Retrievals", statistics.getRetrievals());
      stats.put("Number of stores", statistics.getStores());

      return stats;
   }

   public int getCurrentNumberOfEntries() {
      return statistics.getCurrentNumberOfEntries();
   }

   public long getTotalNumberOfEntries() {
      return statistics.getTotalNumberOfEntries();
   }

   public long getEvictions() {
      return statistics.getEvictions();
   }

   public long getStores() {
      return statistics.getStores();
   }

   public long getRetrievals() {
      return statistics.getRetrievals();
   }
}