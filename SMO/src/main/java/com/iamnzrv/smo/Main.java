package com.iamnzrv.smo;

import com.iamnzrv.smo.global.GlobalManager;

class Main {
  public static void main(String[] args) {
    GlobalManager globalManager = GlobalManager.init(
        100,
        5,
        5,
        5000,
        1000,
        0.1
    );
    globalManager.startProducers();
  }
}