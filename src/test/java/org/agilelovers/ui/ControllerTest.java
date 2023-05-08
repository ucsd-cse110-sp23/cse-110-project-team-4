package org.agilelovers.ui;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControllerTest {

    private Controller controller;

    @BeforeEach
    void setup() {
        controller = new Controller();

        System.out.println("setup() called and finished");
    }

    /*
    * @AfterEach
    * void tearDown() { ... }
    * */

    @Test
    void testInitHistoryList() {
        Controller controller = new Controller();
        controller.initHistoryList();
    }

    @Test
    void testRefreshLabels() {
        Controller controller = new Controller();
        controller.refreshLabels();
    }

    @Test
    void testRecordButtonAction() {
        Controller controller = new Controller();
        controller.recordButtonAction(null);
    }
}
