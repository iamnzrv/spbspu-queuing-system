package com.iamnzrv.smo.global;

import com.iamnzrv.smo.entities.EntityManager;
import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.buffer.Buffer;
import com.iamnzrv.smo.entities.device.Device;
import com.iamnzrv.smo.entities.producer.Producer;
import com.iamnzrv.smo.events.EventManager;

import static com.iamnzrv.smo.entities.device.Device.FREE;

public final class GlobalManager {
  private final EntityManager entityManager;
  private final EventManager eventManager;
  private static GlobalManager instance;

  private GlobalManager(int producersAmount, int devicesAmount) {
    entityManager = new EntityManager();
    entityManager.init(producersAmount, devicesAmount);
    eventManager = new EventManager();
  }

  public static GlobalManager init(int producersAmount, int devicesAmount) {
    if (instance == null) {
      instance = new GlobalManager(producersAmount, devicesAmount);
    }
    return getInstance();
  }

  public static GlobalManager getInstance() {
    return instance;
  }

  public void startProducers() {
    entityManager.getProducerList().forEach(Producer::start);
  }

  public synchronized void notifyThatDeviceIsAvailable(Device device) {
    Bid bidFromBuffer = entityManager.getBuffer().chooseBidFromBuffer();
    if (bidFromBuffer != null) {
      device.process(bidFromBuffer);
    }
  }

  public synchronized void tryToPutBidToDevice(Bid bid) {
    if (bid.getStatus().equals(Bid.GENERATED)) {
      eventManager.addEvent(EventManager.GENERATED_BID);
    }
    entityManager.addBid(bid);
    boolean foundFreeDevice = false;
    for (int i = 0; i < entityManager.getDeviceList().size(); i++) {
      Device device = entityManager.getDeviceList().get(i);
      if (device.getStatus().equals(FREE)) {
        device.process(bid);
        foundFreeDevice = true;
        break;
      }
    }
    if (!foundFreeDevice) {
      tryToPutBidToBuffer(bid);
    }
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public EventManager getEventManager() {
    return eventManager;
  }

  public int getProducerIndex(Producer producer) {
    return entityManager.getProducerList().indexOf(producer);
  }

  private synchronized void tryToPutBidToBuffer(Bid bid) {
    try {
      entityManager.getBuffer().putBidToBuffer(bid);
      bid.setStatus(Bid.WAITING);
    } catch (Buffer.BufferIsFullException e) {
      bid.setStatus(Bid.REJECTED);
      eventManager.addEvent(EventManager.BID_WAS_REJECTED);
    }
  }
}
