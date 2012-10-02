package org.jboss.as.quickstarts.datagrid.monispan.model;

import java.io.Serializable;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class Report implements Serializable {
   private String reportName;
   private String elemPrefix;
   private int userCount;
   private int sentNotificationCount;
   private int subscribtionCount;
   private int cancellationCount;

   private String reportDate;

   public Report() {
   }

   public Report(final String elemPrefix, final int userCount, final int sentNotificationCount, final int subscribtionCount,
                 final int cancellationCount, final String reportDate) {
      this.elemPrefix = elemPrefix;
      this.userCount = userCount;
      this.sentNotificationCount = sentNotificationCount;
      this.subscribtionCount = subscribtionCount;
      this.cancellationCount = cancellationCount;
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

   public int getSubscribtionCount() {
      return subscribtionCount;
   }

   public void setSubscribtionCount(int subscribtionCount) {
      this.subscribtionCount = subscribtionCount;
   }

   public int getCancellationCount() {
      return cancellationCount;
   }

   public void setCancellationCount(int cancellationCount) {
      this.cancellationCount = cancellationCount;
   }

   public String getReportDate() {
      return reportDate;
   }

   public void setReportDate(String reportDate) {
      this.reportDate = reportDate;
   }
}
