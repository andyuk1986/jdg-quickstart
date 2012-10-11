package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Rest service supporting /freshrep url, and which returns the Report for total and average numbers as a json.
 *
 * @author Anna Manukyan
 */
@Path("/freshrep")
public class ReportStatsAjaxRestService {

   @Inject
   private ReportStatisticsProvider reportStatisticsProvider;

   /**
    * Supports GET request, which gets parameter, true or false which means - full or partial report.
    * @param isFullReport           identifies whether full or partial report is needed.
    * @return                       the List containing Total and Average report as a JSON.
    */
   @GET
   @Produces("application/json")
   @Path("{full}")
   public List<Report> updateReportStats(@PathParam("full") boolean isFullReport) {
      return reportStatisticsProvider.getReportStatistics(isFullReport);
   }
}
