package org.jboss.as.quickstarts.datagrid.monispan.web;

import org.jboss.as.quickstarts.datagrid.monispan.cache.CacheProvider;
import org.jboss.as.quickstarts.datagrid.monispan.model.Report;
import org.jsflot.components.FlotChartClickedEvent;
import org.jsflot.components.FlotChartDraggedEvent;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

@Named("chartMbean")
public class ChartMBean {

   private XYDataList series1DataList = new XYDataList();
   private XYDataList series2DataList = new XYDataList();
   private XYDataList series3DataList = new XYDataList();
   private XYDataList series4DataList = new XYDataList();
   private FlotChartRendererData chartData;
   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");

   public static final String PARAM_FULL_REPORT = "full";

   public ChartMBean() {
      // TODO Auto-generated constructor stub
      chartData = new FlotChartRendererData();
      chartData.setMode("Time");
      chartData.setWidth("625");

      String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(PARAM_FULL_REPORT);
      TreeSet<Date> orderedSet = new TreeSet<Date>();
      Map<String, Report> cacheData = null;


      if(param != null && param.equals(true)) {
         cacheData = CacheProvider.getInstance().getCache(CacheProvider.REPORT_CACHE_NAME);
         for(String reportDate : cacheData.keySet()) {
            Date d = null;
            try {
               d = formatter.parse(reportDate);
            } catch (ParseException e) {
               e.printStackTrace();
            }
            if(d == null) {
               d = new Date();
            }
            orderedSet.add(d);
         }
      } else {
         cacheData = CacheProvider.getInstance().getCache(CacheProvider.REPORT_CACHE_NAME);
         for(String reportDate : cacheData.keySet()) {
            Date d = null;
            try {
               d = formatter.parse(reportDate);
            } catch (ParseException e) {
               e.printStackTrace();
            }
            if(d == null) {
               d = new Date();
            }
            orderedSet.add(d);
         }
      }

      for(Date d : orderedSet) {
         Report entry = cacheData.get(formatter.format(d));
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

   public void chartActionListener(ActionEvent event) {
      if (event instanceof FlotChartClickedEvent) {

      } else if (event instanceof FlotChartDraggedEvent) {

      }
   }

   public void testChartDraggedAction(FlotChartDraggedEvent dragEvent) {
      System.out.println("chartDraggedAction");
   }
}
