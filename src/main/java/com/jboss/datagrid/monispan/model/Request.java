package com.jboss.datagrid.monispan.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
@XmlRootElement(name = "request")
public class Request {
   private String nodeName;
   private int usagePercent;

   @XmlElement
   public String getNodeName() {
      return nodeName;
   }

   public void setNodeName(String nodeName) {
      this.nodeName = nodeName;
   }

   @XmlElement
   public int getUsagePercent() {
      return usagePercent;
   }

   public void setUsagePercent(int usagePercent) {
      this.usagePercent = usagePercent;
   }
}
