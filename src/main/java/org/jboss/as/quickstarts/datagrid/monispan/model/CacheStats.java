package org.jboss.as.quickstarts.datagrid.monispan.model;

import java.io.Serializable;

/**
 * // TODO: Document this
 *
 * @author anna.manukyan
 * @since 4.0
 */
public class CacheStats implements Serializable {
   private int currentNumberOfEntries;
   private long totalNumberOfEntries;
   private long evictions;
   private long stores;
   private long retrievals;

   public CacheStats(int currentNumberOfEntries, long totalNumberOfEntries, long evictions, long stores, long retrievals) {
      this.currentNumberOfEntries = currentNumberOfEntries;
      this.totalNumberOfEntries = totalNumberOfEntries;
      this.evictions = evictions;
      this.stores = stores;
      this.retrievals = retrievals;
   }

   public int getCurrentNumberOfEntries() {
      return currentNumberOfEntries;
   }

   public void setCurrentNumberOfEntries(int currentNumberOfEntries) {
      this.currentNumberOfEntries = currentNumberOfEntries;
   }

   public long getTotalNumberOfEntries() {
      return totalNumberOfEntries;
   }

   public void setTotalNumberOfEntries(long totalNumberOfEntries) {
      this.totalNumberOfEntries = totalNumberOfEntries;
   }

   public long getEvictions() {
      return evictions;
   }

   public void setEvictions(long evictions) {
      this.evictions = evictions;
   }

   public long getStores() {
      return stores;
   }

   public void setStores(long stores) {
      this.stores = stores;
   }

   public long getRetrievals() {
      return retrievals;
   }

   public void setRetrievals(long retrievals) {
      this.retrievals = retrievals;
   }
}
