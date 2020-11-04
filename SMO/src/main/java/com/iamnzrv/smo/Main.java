package com.iamnzrv.smo;

import com.iamnzrv.smo.global.GlobalManager;

class Main {
  public static void main(String[] args) {
    GlobalManager globalManager = GlobalManager.init(5, 5);
    globalManager.startProducers();
  }
}