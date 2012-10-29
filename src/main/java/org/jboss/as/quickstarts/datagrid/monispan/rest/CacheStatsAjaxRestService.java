package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * Rest service, which provides the cache statistics as a response. This service is accessible by /cachestats URL.
 *
 * @author Anna Manukyan
 */
@Path("/cachestats")
public class CacheStatsAjaxRestService {

   @Inject
   CacheStatisticsProvider provider;

   @GET
   @Produces("application/json")
   public Map<String, Long> getCacheStatistics() {
      return provider.getCacheStatisticsAsMap();

   }
}
