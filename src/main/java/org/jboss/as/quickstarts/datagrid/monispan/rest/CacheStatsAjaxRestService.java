package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
@Path("/cachestats")
public class CacheStatsAjaxRestService {

   @GET
   @Produces("application/json")
   public Map<String, Long> getCacheStatistics() {
      CacheStatisticsProvider provider = new CacheStatisticsProvider();
      return provider.getCacheStatisticsAsMap();

   }
}
