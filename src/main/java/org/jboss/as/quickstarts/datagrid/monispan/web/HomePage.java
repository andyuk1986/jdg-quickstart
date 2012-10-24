package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Managed bean for view.
 *
 * @author Anna Manukyan
 */
@Named(value = "homePage")
public class HomePage {

   private static final String PARAM_FULL_REPORT = "full";

   @Inject
   ReportStatisticsProvider reportStatisticsProvider;

   @Produces @RequestScoped
   @Named("statisticsChart")
   public List<Report> generateStatisticsChart() throws IOException {
      String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(PARAM_FULL_REPORT);

      //ReportStatisticsProvider reportStatisticsProvider = new ReportStatisticsProvider();
      return reportStatisticsProvider.getReportStatistics(param != null && param.equals("true"));
   }

   /**
    * Returns the infinispan statistics for view.
    * @return        the list which contains entryset of infinispan statistics.
    */
   public List<Map.Entry<String, Long>> generateInfinispanStatistics() {
      CacheStatisticsProvider statistics = new CacheStatisticsProvider();
      Map<String, Long> stats = statistics.getCacheStatisticsAsMap();

      return new ArrayList<Map.Entry<String, Long>>(stats.entrySet());
   }
}
