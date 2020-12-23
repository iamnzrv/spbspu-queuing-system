package com.iamnzrv.smo.entities.device;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.events.EventManager;
import com.iamnzrv.smo.global.GlobalManager;

import java.util.Random;

import static com.iamnzrv.smo.entities.bid.Bid.DONE;
import static com.iamnzrv.smo.entities.bid.Bid.PROCESSED;

public class Device {
  private String status;
  private double dLambda;
  private Bid bid;
  private int nextSleepTime = 0;

  public static final String BUSY = "BUSY";
  public static final String FREE = "FREE";

  public Device(String status, double dLambda) {
    this.status = status;
    this.dLambda = dLambda;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public synchronized Bid getBid() {
    return bid;
  }

  public synchronized int getNextSleepTime() {
    return nextSleepTime;
  }

  public void process(Bid bid) {
    this.bid = bid;
    new Thread(() -> {
      setStatus(BUSY);
      Random random = new Random();
      double x = random.nextDouble();
      if (nextSleepTime == 0)
        nextSleepTime = (int) ((-(1.0 / dLambda)) * Math.log(x) * 1000);
      try {
        bid.setStatus(PROCESSED);
        Thread.sleep(nextSleepTime);
        bid.setStatus(DONE);
        nextSleepTime = (int) ((-(1.0 / dLambda)) * Math.log(x) * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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
