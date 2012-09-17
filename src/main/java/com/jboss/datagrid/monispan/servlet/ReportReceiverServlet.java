package com.jboss.datagrid.monispan.servlet;

import com.jboss.datagrid.monispan.cache.CacheProvider;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class ReportReceiverServlet extends HttpServlet {

   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

   public static final String OK_RESPONSE = "ok";

   public static final String NOK_RESPONSE = "nok";

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String nodeName = request.getParameter("node");
      String userCountStr = request.getParameter("count");
      PrintWriter out = response.getWriter();
      String responseStr = NOK_RESPONSE;

      if(userCountStr != null && !userCountStr.isEmpty()) {
         try {
            int userCount = Integer.parseInt(userCountStr);

            responseStr = receiveReport(nodeName, userCount);
         } catch(Exception ex) {
            System.out.println("The passed parameter is not correct: " + ex.getMessage());
         }
      }

      out.write(responseStr);
   }

   private String receiveReport(String nodeName, int userCount) {
      System.out.println("CPU usage on " + nodeName + " is:" + userCount);

      CacheProvider provider = CacheProvider.getInstance();
      String key = formatter.format(new Date());

      provider.put(nodeName, key, userCount);

      return OK_RESPONSE;
   }
}
