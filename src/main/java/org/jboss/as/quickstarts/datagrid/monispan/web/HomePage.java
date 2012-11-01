package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Managed bean for full and recent reports view. The bean is responsible for viewing the chart with the reporting data.
 * Also the bean contains some element calculations which should be viewed on the page.
 *
 * @author Anna Manukyan
 */
@Named("homePage")
@RequestScoped
public class HomePage {

   @Inject
   ReportStatisticsProvider reportStatisticsProvider;

   @Inject
   CacheStatisticsProvider statistics;

   @Inject
   CacheProvider cacheProvider;

   /**
    * Parameter name for the identifying full or partial reports.
    */
   public static final String PARAM_FULL_REPORT = "full";

   private XYDataList series1DataList = new XYDataList();
   private XYDataList series2DataList = new XYDataList();
   private static FlotChartRendererData chartData = new FlotChartRendererData();

   private static final String CHART_MODE = "Time";
   private static final String CHART_WIDTH_IN_PIXELS = "625";

   private String pageTitle;
   private String pageDescription;
   private String information;

   /**
    * Returns the infinispan statistics for view.
    * @return        the list which contains entryset of infinispan statistics.
    */
   @Produces @RequestScoped
   @Named("infinispanStatistics")
   public List<Map.Entry<String, Long>> generateInfinispanStatistics() {
      Map<String, Long> stats = statistics.getCacheStatisticsAsMap();

      cacheProvider.getNotifListener().resetCounters();
      return new ArrayList<Map.Entry<String, Long>>(stats.entrySet());
   }

   /**
    * Returns the page title to show based on whether the full report is shown or not.
    * @return        the page title to show.
    */
   @PostConstruct
   public String getPageData() {
      if(isFullReportNeeded()) {
         pageTitle = "Full Data";
         pageDescription = "the full data report";
         information = "showing the full data for User Count and Sent Notif. Count.";
      } else {
         pageTitle = "Data For The Last Minute";
         pageDescription = "the report for the last minute";
         information = "showing runtime data for User Count and Sent Notif. Count for the last " + CacheProvider.DATA_SHOW_MINUTES + " minute/s.";
      }

      return pageTitle;
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
    * Constructor, which is called when JSFlot component is initialized. Gets the data from the cache, full or partial
    * dependent on the parameter from request and fills the corresponding collections for further processing.
    */
   private void generateSeriesData() {
      chartData.setMode(CHART_MODE);
      chartData.setWidth(CHART_WIDTH_IN_PIXELS);

      Map<String, Report> cacheData = null;

      cacheData = reportStatisticsProvider.getEntriesFromCache(isFullReportNeeded());

      if(cacheData.size() < 10) {
         for(String keys : cacheData.keySet()) {
            System.out.println(keys);
         }
      }

      Set<Date> orderedSet = getSetOfKeys(cacheData);
      for(Date d : orderedSet) {
         Report entry = cacheData.get(ReportStatisticsProvider.GENERAL_DATE_FORMATTER.format(d));
         series1DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getUserCount()));
         series2DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getSentNotificationCount()));
      }
      series1DataList.setLabel("User Count");
      series2DataList.setLabel("Sent Notification Count");
   }

   /**
    * Returns chart series. They are 4 - user counts, sent notifications count, subscription and cancellation counts.
    * @return        the chart series.
    */
   @Produces @RequestScoped
   @Named("chartSeriesData")
   public XYDataSetCollection getChartSeries() {
      generateSeriesData();

      XYDataSetCollection collection = new XYDataSetCollection();

      collection.addDataList(getChartDataList(series1DataList));
      collection.addDataList(getChartDataList(series2DataList));

      return collection;
   }

   //--------------------------------- Methods neccessary for showing the graph ---------------------------
   private Set<Date> getSetOfKeys(Map<String, Report> cacheData) {
      Set<Date> orderedSet = new TreeSet<Date>();

      for(String reportDate : cacheData.keySet()) {
         Date d = null;
         try {
            d = ReportStatisticsProvider.GENERAL_DATE_FORMATTER.parse(reportDate);
         } catch (ParseException e) {
            e.printStackTrace();
         }
         if(d == null) {
            d = new Date();
         }
         orderedSet.add(d);
      }

      return orderedSet;
   }

   private XYDataList getChartDataList(XYDataList seriesDataList) {
      XYDataList currentSeries1DataList = new XYDataList();

      for (int i = 0; i < seriesDataList.size(); i++) {
         XYDataPoint p1 = new XYDataPoint(seriesDataList.get(i).getX(), seriesDataList.get(i).getY());

         currentSeries1DataList.addDataPoint(p1);
      }
      //Copy over the meta data for each series to the current viewed-series
      currentSeries1DataList.setLabel(seriesDataList.getLabel());
      currentSeries1DataList.setFillLines(seriesDataList.isFillLines());
      currentSeries1DataList.setMarkerPosition(seriesDataList.getMarkerPosition());
      currentSeries1DataList.setMarkers(seriesDataList.isMarkers());
      currentSeries1DataList.setShowDataPoints(seriesDataList.isShowDataPoints());
      currentSeries1DataList.setShowLines(seriesDataList.isShowLines());

      return currentSeries1DataList;
   }

   public XYDataList getSeries1DataList() {
      return series1DataList;
   }

   public void setSeries1DataList(XYDataList series1DataList) {
      this.series1DataList = series1DataList;
   }

   public XYDataList getSeries2DataList() {
      return series2DataList;
   }

   public void setSeries2DataList(XYDataList series2DataList) {
      this.series2DataList = series2DataList;
   }

   public FlotChartRendererData getChartData() {
      return chartData;
   }

   public void setChartData(FlotChartRendererData chartData) {
      this.chartData = chartData;
   }

   public String getPageTitle() {
      return pageTitle;
   }

   public String getPageDescription() {
      return pageDescription;
   }

   public String getInformation() {
      return information;
   }
}
