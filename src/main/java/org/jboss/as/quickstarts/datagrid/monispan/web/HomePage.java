package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
@Named(value = "homePage")
public class HomePage {

   public List<Report> generateStatisticsChart() throws IOException {
      return ReportStatisticsProvider.getInstance().getReportStatistics();
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

   public String getAnna() {
      Random r = new Random();
      return "anna" + r.nextInt(100);
   }
}
