package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.loaders.jdbc.stringbased.JdbcStringBasedCacheStore;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntriesEvicted;
import org.infinispan.notifications.cachelistener.event.Event;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupInitListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

/**
 * Cache Manager which configures and starts the Infinispan Cache, as well as provides functionality related to storing,
 * getting data from cache/data storage.
 *
 * @author Anna Manukyan
 */
@ApplicationScoped
public class CacheProvider {
   /**
    * Constant - the representation of 1 minute in millisecond.
    */
   public static final long MINUTE_IN_MILLISECOND = 60 * 1000;
   /**
    * The minutes during which the statistics should be shown.
    */
   public static final int DATA_SHOW_MINUTES = 1;
   /**
    * The name of the reporting cache.
    */
   public static final String REPORT_CACHE_NAME = "reportingCache";
   /**
    * The name of the Infinispan async executor property.
    */
   private static final String MAX_THREADS_PROP_NAME = "maxThreads";

   private DefaultCacheManager cacheManager;
   private AsyncNotifListener notifListener;

   /**
    * Configures and starts cache with the provided name. The cache is configured in the following way:
    *     no clustering is activated, eviction is set with LRU strategy,
    *     the JDBCStringBasedCacheStore is used, for storing part of the data and uses H2 database as a cache store.
    */
   public void startCache() {
      if(cacheManager == null) {
         // GlobalConfiguration for Infinispan cache config:
         GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder().globalJmxStatistics().
               // the jmx statistics are enabled with custom name;
               enabled(true).allowDuplicateDomains(true).jmxDomain("org.jboss.as.quickstarts.datagrid.monispan")
               // the async listener executor is enabled with max number of threads as 5 - this setting allows to have simultaneously
               // running 5 threads in an executor thread, which notifies the asynchronously working listener about cache related
               // events, e.g. eviction
               .asyncListenerExecutor().addProperty(MAX_THREADS_PROP_NAME, "5")
               .build();
         cacheManager = new DefaultCacheManager(globalConfiguration);
      }

      // Calculating the number of maxentries in the cache.
      // The cache max entries number is calculated according to the provided number of simulator jobs execution frequency.
      // E.g. if the execution frequency is set to 6 seconds, then the number of max entries is calculated as 10
      // (as if on report is put to the cache each 6 seconds, there would be 10 reports in a minute maximum).
      long maxEntriesCount = (MINUTE_IN_MILLISECOND / StartupInitListener.getFrequency()) * DATA_SHOW_MINUTES;

      //The cache is configured in local mode, with enabled JMX statistics;
      Configuration config = new ConfigurationBuilder().jmxStatistics().enable().clustering().cacheMode(CacheMode.LOCAL)
            // The eviction is enabled with LRU strategy and max number of entries calculated above;
            .eviction().maxEntries((int) maxEntriesCount).strategy(EvictionStrategy.LRU)
            // The cache loader is enabled without preload and with enabled passivation so that in case of eviction the
            // entries are stored in the cache store;
            .loaders().passivation(true).preload(false).shared(false)
            //As a CacheLoader the JDBCStringBasedCacheStore is added, as the keys for the cache are Strings.
            .addCacheLoader().cacheLoader(new JdbcStringBasedCacheStore()).fetchPersistentState(false).purgeOnStartup(false)
            //As a storage h2 database is used which parameters are configured below.
            .addProperty("stringsTableNamePrefix", "monispan")
            .addProperty("idColumnName", "id")
            .addProperty("dataColumnName", "data")
            .addProperty("timestampColumnName", "create_ts")
            .addProperty("timestampColumnType", "BIGINT")
            .addProperty("connectionFactoryClass", "org.infinispan.loaders.jdbc.connectionfactory.ManagedConnectionFactory")
            .addProperty("idColumnType", "VARCHAR")
            .addProperty("dataColumnType", "BINARY")
            .addProperty("dropTableOnExit", "true")
            .addProperty("createTableOnStart", "true")
            .addProperty("databaseType", "H2")
            .addProperty("datasourceJndiLocation", "java:jboss/datasources/ExampleDS")
            .build();

      notifListener = new AsyncNotifListener();

      cacheManager.defineConfiguration(REPORT_CACHE_NAME, config);

      //Attaching asynchronous Listener to the cache, so that the number of evictions is properly calculated.
      cacheManager.getCache(REPORT_CACHE_NAME).addListener(notifListener);
   }

   /**
    * Returns the cache manager.
    * @return              the cache manager object.
    */
   public DefaultCacheManager getCacheManager() {
      return cacheManager;
   }

   /**
    * Returns the notification listener which tracks the number of evictions.
    * @return              the notification listener.
    */
   public AsyncNotifListener getNotifListener() {
      return notifListener;
   }

   /**
    * Puts the given data into the cache.
    * @param cacheName        the cache name to which the data should be stored.
    * @param key              the key with which the data should be stored.
    * @param report           the report to be stored in the cache.
    */
   public void put(final String cacheName, final String key, final Report report) {
      cacheManager.getCache(cacheName).putIfAbsent(key, report);
   }

   /**
    * Returns the cache object by the provided name.
    * @param cacheName        the name of the cache to be provided.
    * @return                 the cache object.
    */
   public Map<String, Report> getCache (final String cacheName) {
      return cacheManager.getCache(cacheName);
   }

   /**
    * Returns the Set of all available cache names.
    * @return                 the set of cache names.
    */
   public Set<String> getAllAvailableCaches() {
      return cacheManager.getCacheNames();
   }

   /**
    * Custom listener for tracking evictions number.
    */
   @Listener(sync=false)
   public class AsyncNotifListener {
      private int counter = 0;

      @CacheEntriesEvicted
      public synchronized void handleEvictions(Event event) {
         if(event.getType() == Event.Type.CACHE_ENTRY_EVICTED) {
            counter++;
         }
      }

      /**
       * Returns the number of total evictions during app lifetime.
       * @return                 the total evictions number.
       *
       */
      public int getCounter() {
         return counter;
      }
   }
}
