package com.iamnzrv.smo.entities.device;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.events.EventManager;
import com.iamnzrv.smo.global.GlobalManager;

import java.util.Date;
import java.util.Random;

import static com.iamnzrv.smo.entities.bid.Bid.DONE;
import static com.iamnzrv.smo.entities.bid.Bid.PROCESSED;

public class Device {
  private String status;
  private double dLambda;
  private Bid bid;
  private int nextSleepTime = 0;
  private long lastStart = 0;
  private long totalWorkTime = 0;
  private long bidsProcessed = 0;
  private final long dMax;
  private final long dMin;

  public static final String BUSY = "BUSY";
  public static final String FREE = "FREE";

  public Device(String status, double dLambda, long dMax, long dMin) {
    this.status = status;
    this.dLambda = dLambda;
    this.dMax = dMax;
    this.dMin = dMin;
  }

  public synchronized String getStatus() {
    return status;
  }

  public synchronized void setStatus(String status) {
    this.status = status;
  }

  public synchronized Bid getBid() {
    return bid;
  }

  public synchronized int getNextSleepTime() {
    return nextSleepTime;
  }

  public synchronized long getTotalWorkTime() {
    return totalWorkTime;
  }

  public synchronized void setTotalWorkTime(long workTime) {
    totalWorkTime = workTime;
  }

  public synchronized long getLastStart() {
    return lastStart;
  }

  public long getBidsProcessed() {
    return bidsProcessed;
  }

  public synchronized void process(Bid bid) {
    this.bid = bid;
    new Thread(() -> {
      setStatus(BUSY);
      double x = (double) ((int) (Math.random() * ((dMax - dMin) + 1)) + dMin) / 1000;
      if (nextSleepTime == 0)
        nextSleepTime = (int) ((-(1.0 / dLambda)) * Math.log(x) * 1000);
      lastStart = new Date().getTime();
      try {
        bid.setStatus(PROCESSED);
        Thread.sleep(nextSleepTime);
        bid.setStatus(DONE);
        nextSleepTime = (int) ((-(1.0 / dLambda)) * Math.log(x) * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      totalWorkTime += new Date().getTime() - lastStart;
      bidsProcessed++;
      this.bid = null;
      GlobalManager.getInstance().notifyThatDeviceIsAvailable(this);
      setStatus(FREE);
      GlobalManager.getInstance().getEventManager().addDeviceEvent(
          EventManager.DEVICE_IS_FREE,
          GlobalManager.getInstance().getDeviceIndex(this)
      );
    }).start();
  }
}
