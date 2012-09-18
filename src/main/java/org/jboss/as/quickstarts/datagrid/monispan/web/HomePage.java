package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;

import javax.inject.Named;
import java.io.IOException;
import java.util.Set;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
@Named(value = "homePage")
public class HomePage {

   public String generateStatisticsChart() throws IOException {
      Set<String> cacheNames = CacheProvider.getInstance().getAllAvailableCaches();
      for (String cacheName : cacheNames) {
         System.out.println("Cache value in cache " + cacheName + " is: " + CacheProvider.getInstance().getCache(cacheName).size());
      }

      return "anna";
   }
}
