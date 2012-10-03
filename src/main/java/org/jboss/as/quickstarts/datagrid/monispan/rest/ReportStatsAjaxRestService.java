package org.jboss.as.quickstarts.datagrid.monispan.rest;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
@Path("/freshrep")
public class ReportStatsAjaxRestService {

   @GET
   @Produces("application/json")
   @Path("{full}")
   public List<Report> updateReportStats(@PathParam("full") boolean isFullReport) {
      return ReportStatisticsProvider.getInstance().getReportStatistics(isFullReport);
   }
}
