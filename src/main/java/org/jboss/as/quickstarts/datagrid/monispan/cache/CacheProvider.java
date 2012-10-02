package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.loaders.jdbc.binary.JdbcBinaryCacheStore;
import org.infinispan.loaders.jdbc.mixed.JdbcMixedCacheStore;
import org.infinispan.loaders.jdbc.stringbased.JdbcStringBasedCacheStore;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntriesEvicted;
import org.infinispan.notifications.cachelistener.event.Event;
import org.jboss.as.quickstarts.datagrid.monispan.jsf.StartupListener;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Cache Manager which provides functionality related to storing, evicting data in cache/data storage.
 *
 * @author Anna Manukyan
 */
public final class CacheProvider {

   private static CacheProvider instance;
   private DefaultCacheManager cacheManager;
   private AsyncNotifListener notifListener;

   public static final long SECOND_IN_MILLIS = 60000;
   public static final int DATA_SHOW_MINUTES = 5;

   public static final String REPORT_CACHE_NAME = "reportingCache";

   private static final String MAX_THREADS_PROP_NAME = "maxThreads";

   private CacheProvider() {
   }

   public static CacheProvider getInstance() {
      if(instance == null) {
         instance = new CacheProvider();
      }

      return instance;
   }

   /**
    * Configures and starts cache with the provided name.
    */
   public void startCache() {
      if(cacheManager == null) {
         GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder().globalJmxStatistics().
               enabled(true).allowDuplicateDomains(true).jmxDomain("org.jboss.as.quickstarts.datagrid.monispan")
               .asyncListenerExecutor().addProperty(MAX_THREADS_PROP_NAME, "5")
               .build();
         cacheManager = new DefaultCacheManager(globalConfiguration);
      }

      long maxEntriesCount = (SECOND_IN_MILLIS / StartupListener.frequency) * DATA_SHOW_MINUTES;

      System.out.println("Max entries count: " + maxEntriesCount);
      Configuration config = new ConfigurationBuilder().jmxStatistics().enable().clustering().cacheMode(CacheMode.LOCAL)
            .eviction().maxEntries((int) maxEntriesCount).strategy(EvictionStrategy.FIFO)
            .loaders().passivation(true).preload(false).shared(false)
            .addCacheLoader().cacheLoader(new JdbcStringBasedCacheStore()).fetchPersistentState(false).purgeOnStartup(false)
            .addProperty("stringsTableNamePrefix", "monispan")
            .addProperty("idColumnName", "id")
            .addProperty("dataColumnName", "data")
            .addProperty("timestampColumnName", "create_ts")
            .addProperty("timestampColumnType", "BIGINT")
            //.addProperty("connectionFactoryClass", "org.infinispan.loaders.jdbc.connectionfactory.ManagedConnectionFactory")
            .addProperty("connectionFactoryClass", "org.infinispan.loaders.jdbc.connectionfactory.PooledConnectionFactory")
            .addProperty("connectionUrl", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            .addProperty("userName", "sa")
            .addProperty("password", "sa")
            .addProperty("driverClass", "org.h2.Driver")
            .addProperty("idColumnType", "VARCHAR")
            .addProperty("dataColumnType", "BINARY")
            .addProperty("dropTableOnExit", "false")
            .addProperty("createTableOnStart", "true")
            .addProperty("databaseType", "H2")
            //.addProperty("datasourceJndiLocation", "java:jboss/datasources/ExampleDS")
            .build();

      System.out.println("Lock Aquisitoin time out: " + config.locking().lockAcquisitionTimeout());
      notifListener = new AsyncNotifListener();

      cacheManager.defineConfiguration(REPORT_CACHE_NAME, config);
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
      //System.out.println("Putting " + cacheManager.getCache(cacheName).size());
      cacheManager.getCache(cacheName).putIfAbsent(key, report);
      //System.out.println("Putted" + cacheManager.getCache(cacheName).size());
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
    * Custom listener for performing all verifications.
    */
   @Listener(sync=false)
   public class AsyncNotifListener {
      private int counter = 0;
      @CacheEntriesEvicted
      public synchronized void handlePassivationActivation(Event event) {
         if(event.getType() == Event.Type.CACHE_ENTRY_EVICTED) {
            counter++;
            System.out.println("Entry evicted: " + counter);
         }
      }

      public int getCounter() {
         return counter;
      }
   }
}
