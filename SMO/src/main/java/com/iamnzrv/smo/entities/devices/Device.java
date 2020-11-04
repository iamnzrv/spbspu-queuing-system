package com.iamnzrv.smo.entities.devices;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.events.EventManager;
import com.iamnzrv.smo.global.GlobalManager;

import java.util.Random;

import static com.iamnzrv.smo.entities.bid.Bid.DONE;
import static com.iamnzrv.smo.entities.bid.Bid.PROCESSED;

public class Device {
  private String status;

  public static final String BUSY = "BUSY";
  public static final String FREE = "FREE";

  public Device(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void process(Bid bid) {
    new Thread(() -> {
      setStatus(BUSY);
      GlobalManager.getInstance().getEventManager().addEvent(EventManager.DEVICE_ACCEPTED_BID);
      Random rand = new Random();
      int lambda = 5;
//      int sleepTime = (int) Math.log(1 - rand.nextDouble()) / (-lambda) * 1000;
      int sleepTime = 10000;
      try {
        bid.setStatus(PROCESSED);
        Thread.sleep(sleepTime);
        bid.setStatus(DONE);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      GlobalManager.getInstance().notifyThatDeviceIsAvailable(this);
      setStatus(FREE);
      GlobalManager.getInstance().getEventManager().addEvent(EventManager.DEVICE_IS_FREE);
    }).start();
  }
}
