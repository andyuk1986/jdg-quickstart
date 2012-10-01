package org.jboss.as.quickstarts.datagrid.monispan.cache;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.loaders.jdbc.mixed.JdbcMixedCacheStore;
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

   public static final long SECOND_IN_MILLIS = 60000;
   public static final int DATA_SHOW_MINUTES = 5;

   public static final String REPORT_CACHE_NAME = "reportingCache";

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
               enabled(true).allowDuplicateDomains(true).jmxDomain("org.jboss.as.quickstarts.datagrid.monispan").build();
         cacheManager = new DefaultCacheManager(globalConfiguration);
      }

      File tmpFolder = new File("/tmp/cache/" + REPORT_CACHE_NAME + "/");
      if(tmpFolder.exists() && tmpFolder.canWrite() && tmpFolder.isDirectory()) {
         File[] listOfFiles = tmpFolder.listFiles();
         if(listOfFiles != null && listOfFiles.length > 0) {
            for(int i = 0; i < listOfFiles.length; i++) {
               File f = listOfFiles[i];
               f.delete();
            }
         }
      }

      long maxEntriesCount = (SECOND_IN_MILLIS / StartupListener.frequency) * DATA_SHOW_MINUTES;

      System.out.println("Max entries count: " + maxEntriesCount);
      Configuration config = new ConfigurationBuilder().jmxStatistics().enable().clustering().cacheMode(CacheMode.LOCAL)
            .locking().lockAcquisitionTimeout(40000)
            .transaction().locking().lockAcquisitionTimeout(40000)
            .eviction().maxEntries((int) maxEntriesCount).strategy(EvictionStrategy.FIFO)
            .loaders().passivation(true).preload(false).shared(false)
            //.addFileCacheStore().location("/tmp/cache/").streamBufferSize(7800).build();
            .addCacheLoader().cacheLoader(new JdbcMixedCacheStore()).fetchPersistentState(false).purgeOnStartup(false)
            .addProperty("tableNamePrefixForStrings", "str")
            .addProperty("tableNamePrefixForBinary", "bin")
            .addProperty("idColumnNameForStrings", "id")
            .addProperty("idColumnNameForBinary", "id")
            .addProperty("dataColumnNameForStrings", "str_col")
            .addProperty("dataColumnNameForBinary", "bin_col")
            .addProperty("timestampColumnNameForStrings", "create_ts")
            .addProperty("timestampColumnNameForBinary", "create_ts")
            .addProperty("timestampColumnTypeForStrings", "BIGINT")
            .addProperty("timestampColumnTypeForBinary", "BIGINT")
            .addProperty("connectionFactoryClass", "org.infinispan.loaders.jdbc.connectionfactory.ManagedConnectionFactory")
            .addProperty("connectionUrl", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            .addProperty("userName", "sa")
            .addProperty("password", "sa")
            .addProperty("driverClass", "org.h2.Driver")
            .addProperty("idColumnTypeForStrings", "VARCHAR")
            .addProperty("idColumnTypeForBinary", "VARCHAR")
            .addProperty("dataColumnTypeForStrings", "BINARY")
            .addProperty("dataColumnTypeForBinary", "BINARY")
            .addProperty("dropTableOnExitForStrings", "true")
            .addProperty("dropTableOnExitForBinary", "true")
            .addProperty("createTableOnStartForStrings", "true")
            .addProperty("createTableOnStartForBinary", "true")
            .addProperty("databaseType", "H2")
            .addProperty("datasourceJndiLocation", "java:jboss/datasources/ExampleDS").build();

      System.out.println("Lock Aquisitoin time out: " + config.locking().lockAcquisitionTimeout());
      AsyncNotifListener listener = new AsyncNotifListener();

      cacheManager.defineConfiguration(REPORT_CACHE_NAME, config);
      cacheManager.getCache(REPORT_CACHE_NAME).addListener(listener);
   }

   /**
    * Returns the cache manager.
    * @return              the cache manager object.
    */
   public DefaultCacheManager getCacheManager() {
      return cacheManager;
   }

   /**
    * Puts the given data into the cache.
    * @param cacheName        the cache name to which the data should be stored.
    * @param key              the key with which the data should be stored.
    * @param report           the report to be stored in the cache.
    */
   public void put(final String cacheName, final String key, final Report report) {
      System.out.println("Putting " + cacheManager.getCache(cacheName).size());
      cacheManager.getCache(cacheName).putIfAbsent(key, report);
      System.out.println("Putted" + cacheManager.getCache(cacheName).size());
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
      public void handlePassivationActivation(Event event) {
         if(event.getType() == Event.Type.CACHE_ENTRY_EVICTED) {
            counter++;
            System.out.println("Entry evicted: " + counter);
         }
      }
   }
}
