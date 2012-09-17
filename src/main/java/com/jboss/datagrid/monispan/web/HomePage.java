package com.jboss.datagrid.monispan.web;

import com.jboss.datagrid.monispan.cache.CacheProvider;
import com.jboss.datagrid.monispan.servlet.StartupListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class HomePage {

   private HttpServletRequest request;
   private HttpServletResponse response;

   public void init(HttpServletRequest request, HttpServletResponse response) {
      this.request = request;
      this.response = response;
   }

   public void generateStatisticsChart() throws IOException {
      Set<String> cacheNames = CacheProvider.getInstance().getAllAvailableCaches();
      for (String cacheName : cacheNames) {
         System.out.println("Cache value in cache " + cacheName + " is: " + CacheProvider.getInstance().getCache(cacheName).size());
      }

      DefaultPieDataset pieDataset = new DefaultPieDataset();
      pieDataset.setValue("JavaWorld", new Integer(75));
      pieDataset.setValue("Other", new Integer(25));
      JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart",   // Title
             pieDataset,           // Dataset
             true,                  // Show legend
             false, false
            );

      File newImg = new File(StartupListener.realApplicationPath + "chart.jpg");

      System.out.println(newImg.getAbsolutePath());
      ChartUtilities.saveChartAsJPEG(newImg, chart, 500, 300);


      try {
         Class.forName("org.h2.Driver");
         Connection connection = DriverManager.getConnection(
               "jdbc:h2:C:\\H2DB\\database\\EMPLOYEEDB", "sa", "");
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement
               .executeQuery("SELECT EMPNAME FROM EMPLOYEEDETAILS");
         while (resultSet.next()) {
            System.out.println("EMPLOYEE NAME:"
                                     + resultSet.getString("EMPNAME"));
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {

      }
   }
}
