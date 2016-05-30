/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pint;

import com.jps.l2app.main.Main;
import javax.swing.SwingWorker;

/**
 *
 * @author admin
 */
public abstract class InterpretWorker implements Runnable {

    private static int workers;

    public static int workers() {
        return workers;
    }

    public InterpretWorker() {
        Main.log("iworkers: " + ++workers);
    }

    @Override
    public void run() {

        perform();
        Main.log("iworkers: " + --workers);

    }

    public abstract void perform();

}
