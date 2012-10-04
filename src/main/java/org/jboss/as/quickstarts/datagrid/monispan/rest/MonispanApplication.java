package org.jboss.as.quickstarts.datagrid.monispan.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Is used by REST for initializing the REST services.
 *
 * @author Anna Manukyan
 */
@ApplicationPath("/rest")
public class MonispanApplication extends Application {
   private Set<Object> singletons = new HashSet<Object>();

   public MonispanApplication() {
      singletons.add(new ReportReceiverRestService());
      singletons.add(new ReportStatsAjaxRestService());
      singletons.add(new CacheStatsAjaxRestService());
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
