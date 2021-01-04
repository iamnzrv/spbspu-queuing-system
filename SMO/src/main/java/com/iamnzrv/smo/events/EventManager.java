package com.iamnzrv.smo.events;

import com.iamnzrv.smo.entities.EntityManager;
import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.device.Device;
import com.iamnzrv.smo.entities.producer.Producer;
import com.iamnzrv.smo.global.GlobalManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EventManager {
    private final List<String> events;

    public static final String GENERATED_BID = "GENERATED_BID";
    public static final String DEVICE_IS_FREE = "DEVICE_IS_FREE";
    public static final String STOP = "STOP";
    public static final String AUTO_MODE = "AUTO_MODE";
    public static final String STEP_MODE = "STEP_MODE";

    public EventManager() {
        events = new ArrayList<>();
    }

    public synchronized void addSystemEvent(String event) {
        events.add(event);
        if (GlobalManager.getInstance().getMode().equals(STEP_MODE)) {
            System.out.printf("SYSTEM %s %d\n", event, getModellingTime());
            System.out.println(
                    Arrays.toString(
                            GlobalManager
                                    .getInstance()
                                    .getEntityManager()
                                    .getBuffer()
                                    .getBidsAsStrings()
                    )
            );
            printState();
            System.out.println();
        } else {
            printStats();
        }
    }

    private synchronized long getModellingTime() {
        return new Date().getTime() - GlobalManager.getInstance().getStartTime();
    }

    public synchronized void printStats() {
        List<Device> devices = GlobalManager.getInstance().getEntityManager().getDeviceList();
        long totalDevicesWorkTime = 0;
        for (Device device : devices) {
            if (device.getTotalWorkTime() == 0)
                device.setTotalWorkTime(new Date().getTime() - device.getLastStart());
            totalDevicesWorkTime += device.getTotalWorkTime();
        }
        totalDevicesWorkTime /= devices.size();
        System.out.println("AVERAGE TIME BID PROCESSED: " + (int) (devices.stream()
                .map(
                        device -> (double) device.getTotalWorkTime() / (double) device.getBidsProcessed()
                )
                .reduce(Double::sum).get() / devices.size()) + "ms"
        );
        long rejectedCount = GlobalManager
                .getInstance()
                .getEntityManager()
                .getBidList()
                .stream()
                .filter((bid) -> bid.getStatus().equals(Bid.REJECTED))
                .count();
        System.out.println("REJECTED NUMBER: " + rejectedCount);
        System.out.println("END TIME: " + getModellingTime() + "ms");
        System.out.println("DEVICE WORK RATIO: " +
                BigDecimal
                        .valueOf((double) totalDevicesWorkTime / (double) getModellingTime() * 100 )
                        .setScale(2, RoundingMode.HALF_UP).doubleValue()  + "%"
        );
        System.out.println("REJECTED RATIO: " + (double) rejectedCount
                / (double) GlobalManager.getInstance().getEntityManager().getBidList().size() * 100 + "%");
    }

    public synchronized void addProducerEvent(String event, int producerIndex) {
        events.add(event);
        if (EventManager.countDoneBids() == GlobalManager.getInstance().getEntityManager().getRequestsAmount()) {
            GlobalManager.getInstance().getEventManager().addSystemEvent(EventManager.STOP);
            System.exit(0);
        }
        if (GlobalManager.getInstance().getMode().equals(STEP_MODE)) {
            System.out.printf("PRODUCER_%d %s %d\n", producerIndex, event, getModellingTime());
            printState();
            System.out.println();
        }
    }

    public synchronized void addDeviceEvent(String event, int deviceIndex) {
        events.add(event);
        if (EventManager.countDoneBids() == GlobalManager.getInstance().getEntityManager().getRequestsAmount()) {
            GlobalManager.getInstance().getEventManager().addSystemEvent(EventManager.STOP);
            System.exit(0);
        }
        if (GlobalManager.getInstance().getMode().equals(STEP_MODE)) {
            System.out.printf("DEVICE_%d %s %d\n", deviceIndex, event, getModellingTime());
            printState();
            System.out.println();
        }
    }

    public static synchronized long countDoneBids() {
        int countDoneBids = 0;
        List<Bid> bids = GlobalManager
                .getInstance()
                .getEntityManager()
                .getBidList();
        for (Bid bid : bids) {
            countDoneBids++;
        }
        return countDoneBids;
    }

    public synchronized void printState() {
        EntityManager entityManager = GlobalManager.getInstance().getEntityManager();
        System.out.println("REQUESTS: " + countDoneBids() + "/" + entityManager.getRequestsAmount());
        List<Producer> producerList = entityManager.getProducerList();
        List<Device> deviceList = entityManager.getDeviceList();
        System.out.print("BUFFER: ");
        System.out.print(
                Arrays.toString(
                        GlobalManager
                                .getInstance()
                                .getEntityManager()
                                .getBuffer()
                                .getBidsAsStrings()
                )
        );
        System.out.println();
        System.out.print("DEVICES: ");
        for (Device device : deviceList) {
            Bid bid = device.getBid();
            if (bid != null)
                System.out.print((deviceList.indexOf(device) == 0 ? "(" : ",(") + bid.getProducerIndex() + "," + bid.getBidNumber() + ")");
            else System.out.print(deviceList.indexOf(device) == 0 ? "WAITING" : ",WAITING");
        }
        System.out.println();
        System.out.print("PRODUCERS TIMINGS: ");
        for (Producer producer : producerList) {
            System.out.print((producerList.indexOf(producer) == 0 ? "(" : ",(") + producer.getNextSleepTime() + ")");
        }
        System.out.println();
        System.out.print("DEVICE TIMINGS: ");
        for (Device device : deviceList) {
            System.out.print((deviceList.indexOf(device) == 0 ? "(" : ",(") + device.getNextSleepTime() + ")");
        }
        System.out.println();
    }

    public List<String> getEvents() {
        return events;
    }
}
