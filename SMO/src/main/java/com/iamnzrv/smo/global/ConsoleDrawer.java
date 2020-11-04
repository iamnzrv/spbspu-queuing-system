package com.iamnzrv.smo.global;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.buffer.Buffer;
import com.iamnzrv.smo.entities.devices.Device;
import com.iamnzrv.smo.entities.producer.Producer;

import java.util.List;

public class ConsoleDrawer {

  public static void draw() {
    List<Producer> producerList = GlobalManager.getInstance().getEntityManager().getProducerList();
    List<Device> deviceList = GlobalManager.getInstance().getEntityManager().getDeviceList();
    Buffer buffer = GlobalManager.getInstance().getEntityManager().getBuffer();
    int maxSize = Math.max(producerList.size(), deviceList.size());
    for (int i = 0; i < maxSize; i++) {
      StringBuilder stringBuilder = new StringBuilder();
      if (maxSize == producerList.size()) {
        stringBuilder.append("Producer #").append(i).append(" | ").append(producerList.get(i).getStatus());
        if (i < deviceList.size()) {
          stringBuilder.append(" | Device #").append(i).append(" | ").append(deviceList.get(i).getStatus());
        }
      } else {
        if (i < producerList.size()) {
          stringBuilder.append("Producer #").append(i).append(" | ").append(producerList.get(i).getStatus());
        }
        stringBuilder.append(" | Device #").append(i).append(" | ").append(deviceList.get(i).getStatus());
      }
      System.out.println(stringBuilder.toString());
    }
    Bid[] bids = buffer.getBids();
    for (int i = 0; i < bids.length; i++) {
      if (bids[i] != null) {
        System.out.println("Buffer place #" + i + " | " + bids[i].getStatus());
      } else {
        System.out.println("Buffer place #" + i + " | EMPTY");
      }
    }
  }
}
