package com.iamnzrv.smo.entities;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.buffer.Buffer;
import com.iamnzrv.smo.entities.device.Device;
import com.iamnzrv.smo.entities.producer.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.iamnzrv.smo.entities.device.Device.FREE;


public class EntityManager {
  private final List<Producer> producerList;
  private final List<Bid> bidList;
  private final List<Device> deviceList;
  private final Buffer buffer;

  public EntityManager() {
    producerList = new ArrayList<>();
    bidList = new ArrayList<>();
    deviceList = new ArrayList<>();
    buffer = new Buffer();
  }

  public void init(int producersAmount, int devicesAmount) {
    for (int i = 0; i < producersAmount; i++) {
      Producer producer = new Producer(Producer.STOPPED);
      producerList.add(producer);
    }
    for (int i = 0; i < devicesAmount; i++) {
      Device device = new Device(FREE);
      deviceList.add(device);
    }
  }

  public Buffer getBuffer() {
    return buffer;
  }

  public void addBid(Bid bid) {
    bidList.add(bid);
  }

  public List<Producer> getProducerList() {
    return Collections.unmodifiableList(producerList);
  }

  public List<Device> getDeviceList() {
    return deviceList;
  }

  public List<Bid> getBidList() {
    return Collections.unmodifiableList(bidList);
  }

  public List<Bid> getBidListByStatus(String status) {
    return bidList
        .stream()
        .filter(bid -> bid.getStatus().equals(status))
        .collect(Collectors.toUnmodifiableList());
  }
}
