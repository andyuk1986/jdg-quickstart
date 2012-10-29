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
 * Managed bean for full and recent reports view.
 *
 * @author Anna Manukyan
 */
@Named("homePage")
public class HomePage {

   private static final String PARAM_FULL_REPORT = "full";

   @Inject
   ReportStatisticsProvider reportStatisticsProvider;

   @Inject
   CacheStatisticsProvider statistics;

   @Produces @RequestScoped
   @Named("statisticsChart")
   public List<Report> generateStatisticsChart() throws IOException {
      return reportStatisticsProvider.getReportStatistics(isFullReportNeeded());
   }

   /**
    * Returns the infinispan statistics for view.
    * @return        the list which contains entryset of infinispan statistics.
    */
   public List<Map.Entry<String, Long>> generateInfinispanStatistics() {
      Map<String, Long> stats = statistics.getCacheStatisticsAsMap();

      return new ArrayList<Map.Entry<String, Long>>(stats.entrySet());
   }

   /**
    * Returns whether the full report is needed or no.
    * @return           <code>true</code>, if full report is needed, otherwise <code>false</code>.
    */
   public boolean isFullReportNeeded() {
      String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(PARAM_FULL_REPORT);

      return (param != null && param.equals("true"));
   }

   /**
    * Returns the page title to show based on whether the full report is shown or not.
    * @return        the page title to show.
    */
   public String getPageTitle() {
      String pageTitle = "";

      if(isFullReportNeeded()) {
         pageTitle = "Full Data";
      } else {
         pageTitle = "Data For The Last Minute";
      }

      return pageTitle;
   }

   /**
    * Returns the page description to show based on whether the full report is shown or not.
    *
    * @return        the page description to show on page.
    */
   public String getPageDescription() {
      String description = "";

      if(isFullReportNeeded()) {
         description = "the full data report";
      } else {
         description = "the report for the last minute";
      }

      return description;
   }
}
