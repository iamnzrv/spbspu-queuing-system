package com.iamnzrv.smo.entities.producer;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.global.GlobalManager;

import static com.iamnzrv.smo.entities.bid.Bid.GENERATED;

public class Producer {
  private boolean isInterrupted = false;

  public static final String PRODUCING = "PRODUCING";
  public static final String STOPPED = "STOPPED";

  private String status;

  public Producer(String status) {
    this.status = status;
  }

  public void interrupt() {
    isInterrupted = true;
  }

  public String getStatus() {
    return status;
  }

  public void start() {
    new Thread(() -> {
      status = PRODUCING;
      int max = 5000;
      int min = 1000;
      while (!isInterrupted) {
        try {
          int sleepTime = (int) (Math.random() * ((max - min) + 1)) + min;
          Thread.sleep(sleepTime);
          GlobalManager.getInstance().tryToPutBidToDevice(
              new Bid(
                  GENERATED,
                  GlobalManager.getInstance().getProducerIndex(this)
              )
          );
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      status = STOPPED;
    }).start();
  }
}
