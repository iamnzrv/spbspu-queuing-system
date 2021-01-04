package com.iamnzrv.smo;

import com.iamnzrv.smo.events.EventManager;
import com.iamnzrv.smo.global.GlobalManager;

class Main {
    public static void main(String[] args) {
        GlobalManager globalManager = GlobalManager.init(
                EventManager.STEP_MODE,
                5,
                10,
                2,
                5,
                200,
                100,
                300,
                100,
                4
        );
        globalManager.startProducers();
    }
}