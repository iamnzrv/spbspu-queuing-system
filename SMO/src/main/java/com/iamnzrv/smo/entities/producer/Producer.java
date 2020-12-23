package com.iamnzrv.smo.entities.producer;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.global.GlobalManager;

import static com.iamnzrv.smo.entities.bid.Bid.GENERATED;

public class Producer {
  private boolean isInterrupted = false;
  private int pMax;
  private int pMin;
  private int bidCnt = 0;
  private int nextSleepTime = 0;

  public static final String PRODUCING = "PRODUCING";
  public static final String STOPPED = "STOPPED";

  private String status;

  public Producer(String status, int pMax, int pMin) {
    this.status = status;
    this.pMax = pMax;
    this.pMin = pMin;
  }

  public void interrupt() {
    isInterrupted = true;
  }

  public String getStatus() {
    return status;
  }

  public int getNextSleepTime() {
    return nextSleepTime;
  }

  public void start() {
    new Thread(() -> {
      status = PRODUCING;
      while (!isInterrupted) {
        try {
          if (nextSleepTime == 0)
            nextSleepTime = (int) (Math.random() * ((pMax - pMin) + 1)) + pMin;
          Thread.sleep(nextSleepTime);
          bidCnt++;
          nextSleepTime = (int) (Math.random() * ((pMax - pMin) + 1)) + pMin;
          GlobalManager.getInstance().tryToPutBidToDevice(
              new Bid(
                  GENERATED,
                  GlobalManager.getInstance().getProducerIndex(this),
                  bidCnt
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
