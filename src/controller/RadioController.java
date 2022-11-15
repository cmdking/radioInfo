/*
 * FILE:
 *      ProgramLoader.java
 *
 * DESCRIPTION:
 *      Controller between startMenu and programController.
 *
 * AUTHOR:
 *      Elias Niko, c18eno
 *
 * CHANGES:
 *      20200129: Initial version.
 */
package controller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The class connects startMenu and ProgramController to work.
 * @author Elias Niko
 * @version 1.0
 */
public class RadioController {
    private static ProgramController controller;
    private static final int ONE_HOUR = 3600000;
    private static final int DELAY = 0;

    /**
     * Main method that runs the program.
     * @param args as String array
     */
    public static void main(final String[] args) {
        controller = new ProgramController();
        final AutoUpdate auto = new AutoUpdate();
        controller.showGraphics();
        final Timer timer = new Timer();
        timer.schedule(auto, DELAY, ONE_HOUR);
    }

    /**
     * The class connects extends of TimerTask and call a method
     * from ProgramController to update. This class will be created
     * every one-hour to update the table.
     * @author Elias Niko
     * @version 1.0
     */
    static class AutoUpdate extends TimerTask {
        @Override
        public void run() {
            controller.start();
        }
    }
}