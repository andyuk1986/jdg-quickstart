package org.jboss.as.quickstarts.datagrid.monispan.model;

import java.io.Serializable;

/**
 * Represents the report object. Contains all necessary fields for further use.
 *
 * @author Anna Manukyan
 */
public class Report implements Serializable {
   private String reportName;
   private String elemPrefix;
   private int userCount;
   private int sentNotificationCount;
   private String reportDate;

   public Report() {
   }

   public Report(final String elemPrefix, final int userCount, final int sentNotificationCount, final String reportDate) {
      this.elemPrefix = elemPrefix;
      this.userCount = userCount;
      this.sentNotificationCount = sentNotificationCount;
      this.reportDate = reportDate;
   }

   public String getReportName() {
      return reportName;
   }

   public void setReportName(String reportName) {
      this.reportName = reportName;
   }

   public String getElemPrefix() {
      return elemPrefix;
   }

   public void setElemPrefix(String elemPrefix) {
      this.elemPrefix = elemPrefix;
   }

   public int getUserCount() {
      return userCount;
   }

   public void setUserCount(int userCount) {
      this.userCount = userCount;
   }

   public int getSentNotificationCount() {
      return sentNotificationCount;
   }

   public void setSentNotificationCount(int sentNotificationCount) {
      this.sentNotificationCount = sentNotificationCount;
   }

   public String getReportDate() {
      return reportDate;
   }

   public void setReportDate(String reportDate) {
      this.reportDate = reportDate;
   }
}
