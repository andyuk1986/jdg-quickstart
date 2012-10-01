package org.jboss.as.quickstarts.datagrid.monispan.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class Report implements Serializable {
   private String reportName;
   private String nodeName;
   private int userCount;
   private int sentNotificationCount;
   private int subscribtionCount;
   private int cancellationCount;

   public Report() {
   }

   public Report(final String nodeName, final int userCount, final int sentNotificationCount, final int subscribtionCount, final int cancellationCount) {
      this.nodeName = nodeName;
      this.userCount = userCount;
      this.sentNotificationCount = sentNotificationCount;
      this.subscribtionCount = subscribtionCount;
      this.cancellationCount = cancellationCount;
   }

   public String getReportName() {
      return reportName;
   }

   public void setReportName(String reportName) {
      this.reportName = reportName;
   }

   public String getNodeName() {
      return nodeName;
   }

   public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
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
}
