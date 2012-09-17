package com.jboss.datagrid.monispan.cache;

import com.jboss.datagrid.monispan.servlet.StartupListener;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.loaders.jdbc.mixed.JdbcMixedCacheStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public final class CacheProvider {

   private static CacheProvider instance;
   private DefaultCacheManager cacheManager;

   public static final long SECOND_IN_MILLIS = 60000;
   public static final int DATA_SHOW_MINUTES = 5;

   private CacheProvider() {
   }

   public static CacheProvider getInstance() {
      if(instance == null) {
         instance = new CacheProvider();
      }

      return instance;
   }

   public void startCache(final String cacheName) {
      if(cacheManager == null) {
         cacheManager = new DefaultCacheManager();
      }

      long maxEntriesCount = (SECOND_IN_MILLIS / StartupListener.frequency) * DATA_SHOW_MINUTES;
      Configuration config = new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
            .eviction().maxEntries((int) maxEntriesCount).strategy(EvictionStrategy.FIFO)
            .loaders().passivation(true).preload(false).shared(false)
            .addCacheLoader().cacheLoader(new JdbcMixedCacheStore()).fetchPersistentState(false).purgeOnStartup(false)
            .addProperty("tableNamePrefixForStrings", "prefix")
            .addProperty("tableNamePrefixForBinary", "bucket")
            .addProperty("idColumnNameForStrings", "id")
            .addProperty("idColumnNameForBinary", "id")
            .addProperty("dataColumnNameForStrings", "bin_col")
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

      System.out.println(config);

      cacheManager.defineConfiguration(cacheName, config);
   }

   public void put(final String cacheName, final String key, final int value) {
      System.out.println("Putting " + cacheManager.getCache(cacheName).size());
      cacheManager.getCache(cacheName).put(key, value);
      System.out.println("Putted" + cacheManager.getCache(cacheName).size());
   }

   public Map<String, String> getCache (final String cacheName) {
      return cacheManager.getCache(cacheName);
   }

   public Set<String> getAllAvailableCaches() {
      return cacheManager.getCacheNames();
   }
}
