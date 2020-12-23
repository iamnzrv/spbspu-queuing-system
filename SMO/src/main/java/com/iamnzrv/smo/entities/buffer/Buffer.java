package com.iamnzrv.smo.entities.buffer;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.events.EventManager;
import com.iamnzrv.smo.global.GlobalManager;

public class Buffer {
  private final Bid[] bids;
  private final int capacity;

  public Buffer(int capacity) {
    this.capacity = capacity;
    bids = new Bid[capacity];
  }

  public synchronized void putBidToBuffer(Bid bid) throws BufferIsFullException {
    boolean foundFreePlace = false;
    for (int i = 0; i < bids.length; i++) {
      if (bids[i] == null) {
        bids[i] = bid;
        foundFreePlace = true;
        break;
      }
    }
    if (!foundFreePlace) throw new BufferIsFullException();
    else {
      bid.setStatus(Bid.WAITING);
    }
  }

  public Bid[] getBids() {
    return bids;
  }

  public String[] getBidsAsStrings() {
    String[] strings = new String[bids.length];
    for (int i = 0; i < bids.length; i++) {
      if (bids[i] != null) {
        strings[i] = bids[i].toString();
      } else strings[i] = "EMPTY";
    }
    return strings;
  }

  public Bid chooseBidFromBuffer() {
    Bid highestPriorityBid = bids[0];
    int highestPriorityBidIndex = 0;
    for (int i = 1; i < capacity; i++) {
      if (bids[i] != null) {
        if (highestPriorityBid == null) {
          highestPriorityBid = bids[i];
          highestPriorityBidIndex = i;
          continue;
        }
        if (bids[i].getProducerIndex() < highestPriorityBid.getProducerIndex()) {
          highestPriorityBid = bids[i];
          highestPriorityBidIndex = i;
        }
      }
    }
    bids[highestPriorityBidIndex] = null;
    return highestPriorityBid;
  }

  public static class BufferIsFullException extends Exception {
    public BufferIsFullException() {
      super("Buffer is full");
    }
  }
}
