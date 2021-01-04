package com.iamnzrv.smo.global;

import com.iamnzrv.smo.entities.EntityManager;
import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.buffer.Buffer;
import com.iamnzrv.smo.entities.device.Device;
import com.iamnzrv.smo.entities.producer.Producer;
import com.iamnzrv.smo.events.EventManager;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.iamnzrv.smo.entities.device.Device.FREE;

public final class GlobalManager {
    private final EntityManager entityManager;
    private final EventManager eventManager;
    private final long startTime;
    private static GlobalManager instance;
    private final String mode;

    private GlobalManager(
            String mode,
            int bufferCapacity,
            int requestsAmount,
            int producersAmount,
            int devicesAmount,
            int pMax,
            int pMin,
            int dMax,
            int dMin,
            double dLambda
    ) {
        this.mode = mode;
        startTime = new Date().getTime();
        entityManager = new EntityManager(bufferCapacity);
        entityManager.init(
                requestsAmount,
                producersAmount,
                devicesAmount,
                pMax,
                pMin,
                dMax,
                dMin,
                dLambda
        );
        eventManager = new EventManager();
    }

    public static GlobalManager init(
            String mode,
            int bufferCapacity,
            int requestsAmount,
            int producersAmount,
            int devicesAmount,
            int pMax,
            int pMin,
            int dMax,
            int dMin,
            double dLambda
    ) {
        if (instance == null) {
            instance = new GlobalManager(
                    mode,
                    bufferCapacity,
                    requestsAmount,
                    producersAmount,
                    devicesAmount,
                    pMax,
                    pMin,
                    dMax,
                    dMin,
                    dLambda
            );
        }
        return getInstance();
    }

    public static synchronized GlobalManager getInstance() {
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

    public void tryToPutBidToDevice(Bid bid) {
        if (bid.getStatus().equals(Bid.GENERATED)) {
            eventManager.addProducerEvent(EventManager.GENERATED_BID, bid.getProducerIndex());
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

    public synchronized long getStartTime() {
        return startTime;
    }

    public synchronized EntityManager getEntityManager() {
        return entityManager;
    }

    public synchronized EventManager getEventManager() {
        return eventManager;
    }

    public synchronized  int getProducerIndex(Producer producer) {
        return entityManager.getProducerList().indexOf(producer);
    }

    public synchronized  int getDeviceIndex(Device device) {
        return entityManager.getDeviceList().indexOf(device);
    }

    public String getMode() {
        return mode;
    }

    private void tryToPutBidToBuffer(Bid bid) {
        try {
            entityManager.getBuffer().putBidToBuffer(bid);
            bid.setStatus(Bid.WAITING);
        } catch (Buffer.BufferIsFullException e) {
            bid.setStatus(Bid.REJECTED);
        }
    }
}
