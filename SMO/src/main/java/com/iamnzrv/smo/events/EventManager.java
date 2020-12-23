package com.iamnzrv.smo.events;

import com.iamnzrv.smo.entities.bid.Bid;
import com.iamnzrv.smo.entities.device.Device;
import com.iamnzrv.smo.entities.producer.Producer;
import com.iamnzrv.smo.global.GlobalManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManager {
  private final List<String> events;

  public static final String GENERATED_BID = "GENERATED_BID";
  public static final String DEVICE_IS_FREE = "DEVICE_IS_FREE";
  public static final String STOP = "STOP";
  public static int time = 0;


  public EventManager() {
    events = new ArrayList<>();
  }

  public synchronized void addSystemEvent(String event) {
    events.add(event);
    LocalDateTime now = LocalDateTime.now();
    int hour = now.getHour();
    int minute = now.getMinute();
    int second = now.getSecond();
    int millis = now.get(ChronoField.MILLI_OF_SECOND);

    System.out.printf("SYSTEM %s %02d:%02d:%02d.%03d\n", event, hour, minute, second, millis);
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
  }

  public synchronized void addProducerEvent(String event, int producerIndex) {
    events.add(event);
    LocalDateTime now = LocalDateTime.now();
    int hour = now.getHour();
    int minute = now.getMinute();
    int second = now.getSecond();
    int millis = now.get(ChronoField.MILLI_OF_SECOND);

    System.out.printf("PRODUCER_%d %s %02d:%02d:%02d.%03d\n", producerIndex, event, hour, minute, second, millis);
    printState();
    System.out.println();
  }

  public synchronized void addDeviceEvent(String event, int deviceIndex) {
    events.add(event);
    LocalDateTime now = LocalDateTime.now();
    int hour = now.getHour();
    int minute = now.getMinute();
    int second = now.getSecond();
    int millis = now.get(ChronoField.MILLI_OF_SECOND);

    System.out.printf("DEVICE_%d %s %02d:%02d:%02d.%03d\n", deviceIndex, event, hour, minute, second, millis);
    printState();
    System.out.println();
  }

  public synchronized void printState() {
    System.out.println("REQUESTS: " + GlobalManager.getInstance().getEntityManager().getBidList().size() + "/" + GlobalManager.getInstance().getEntityManager().getRequestsAmount());
    List<Producer> producerList = GlobalManager.getInstance().getEntityManager().getProducerList();
    List<Device> deviceList = GlobalManager.getInstance().getEntityManager().getDeviceList();
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
