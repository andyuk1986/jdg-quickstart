package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.ReportStatisticsProvider;
import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Managed bean for viewing the chart of statistics. The chart is drawn using JSFlot, so the class is used by JSFlot
 * component.
 *
 * @author Anna Manukyan
 */
@Named("chartMbean")
public class ChartMBean {

   /**
    * Parameter name for the identifying full or partial reports.
    */
   public static final String PARAM_FULL_REPORT = "full";

   private XYDataList series1DataList = new XYDataList();
   private XYDataList series2DataList = new XYDataList();
   private XYDataList series3DataList = new XYDataList();
   private XYDataList series4DataList = new XYDataList();
   private FlotChartRendererData chartData;

   private static final String CHART_MODE = "Time";
   private static final String CHART_WIDTH_IN_PIXELS = "625";

   /**
    * Constructor, which is called when JSFlot component is initialized. Gets the data from the cache, full or partial
    * dependent on the parameter from request and fills the corresponding collections for further processing.
    */
   public ChartMBean() {
      chartData = new FlotChartRendererData();
      chartData.setMode(CHART_MODE);
      chartData.setWidth(CHART_WIDTH_IN_PIXELS);

      String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(PARAM_FULL_REPORT);
      Map<String, Report> cacheData = null;

      if(param != null && param.equals(true)) {
         cacheData = ReportStatisticsProvider.getInstance().getAllEntriesFromCache();
      } else {
         cacheData = CacheProvider.getInstance().getCache(CacheProvider.REPORT_CACHE_NAME);
      }

      Set<Date> orderedSet = getSetOfKeys(cacheData);
      for(Date d : orderedSet) {
         Report entry = cacheData.get(ReportStatisticsProvider.GENERAL_DATE_FORMATTER.format(d));
         series1DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getUserCount()));
         series2DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getSentNotificationCount()));
         series3DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getSubscribtionCount()));
         series4DataList.addDataPoint(new XYDataPoint(d.getTime(), entry.getCancellationCount()));
      }
      series1DataList.setLabel("User Count");
      series2DataList.setLabel("Sent Notification Count");
      series3DataList.setLabel("Subscription Count");
      series4DataList.setLabel("Cancellation Count");

   }

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

   /**
    * Returns chart series. They are 4 - user counts, sent notifications count, subscription and cancellation counts.
    * @return        the chart series.
    */
   public XYDataSetCollection getChartSeries() {
      XYDataSetCollection collection = new XYDataSetCollection();

      collection.addDataList(getChartDataList(series1DataList));
      collection.addDataList(getChartDataList(series2DataList));
      collection.addDataList(getChartDataList(series3DataList));
      collection.addDataList(getChartDataList(series4DataList));

      return collection;
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

   public XYDataList getSeries3DataList() {
      return series3DataList;
   }

   public void setSeries3DataList(XYDataList series4DataList) {
      this.series3DataList = series3DataList;
   }

   public XYDataList getSeries4DataList() {
      return series4DataList;
   }

   public void setSeries4DataList(XYDataList series4DataList) {
      this.series4DataList = series3DataList;
   }

   public FlotChartRendererData getChartData() {
      return chartData;
   }

   public void setChartData(FlotChartRendererData chartData) {
      this.chartData = chartData;
   }
}
