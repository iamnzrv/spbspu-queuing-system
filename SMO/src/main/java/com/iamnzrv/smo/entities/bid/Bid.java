package com.iamnzrv.smo.entities.bid;

public class Bid {
  private String status;
  private final int producerIndex;
  private final int bidNumber;

  public static final String GENERATED = "GENERATED";
  public static final String REJECTED = "REJECTED";
  public static final String WAITING = "WAITING";
  public static final String PROCESSED = "PROCESSED";
  public static final String DONE = "DONE";

  public Bid(String status, int producerIndex, int bidNumber) {
    this.status = status;
    this.producerIndex = producerIndex;
    this.bidNumber = bidNumber;
  }

  public int getProducerIndex() {
    return producerIndex;
  }

  public synchronized String getStatus() {
    return status;
  }

  public synchronized void setStatus(String status) {
    this.status = status;
  }

  public int getBidNumber() {
    return bidNumber;
  }

  @Override
  public String toString() {
    return "(" + producerIndex + "," + bidNumber + ")";
  }
}
