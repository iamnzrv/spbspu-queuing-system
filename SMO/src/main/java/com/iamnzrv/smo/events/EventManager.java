package com.iamnzrv.smo.events;

import com.iamnzrv.smo.global.ConsoleDrawer;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
  private final List<String> events;

  public static final String GENERATED_BID = "GENERATED_BID";
  public static final String DEVICE_IS_FREE = "DEVICE_IS_FREE";
  public static final String DEVICE_ACCEPTED_BID = "DEVICE_ACCEPTED_BID";
  public static final String BID_WAS_REJECTED = "BID_WAS_REJECTED";
  public static final String BID_WAS_PUT_TO_BUFFER = "BID_WAS_PUT_TO_BUFFER";
  public static final String STOP = "STOP";


  public EventManager() {
    events = new ArrayList<>();
  }

 public synchronized void addEvent(String event) {
   events.add(event);
   System.out.println(event);
   ConsoleDrawer.draw();
 }

  public List<String> getEvents() {
    return events;
  }
}
