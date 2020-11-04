package com.iamnzrv.smo.entities.bid;

public class Bid {
  private String status;
  private final int producerIndex;

  public static final String GENERATED = "GENERATED";
  public static final String REJECTED = "REJECTED";
  public static final String WAITING = "WAITING";
  public static final String PROCESSED = "PROCESSED";
  public static final String DONE = "DONE";

  public Bid(String status, int producerIndex) {
    this.status = status;
    this.producerIndex = producerIndex;
  }

  public int getProducerIndex() {
    return producerIndex;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
