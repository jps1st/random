/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.devices;

import com.jps.l2app.main.Main;
import java.util.PriorityQueue;
import javax.swing.SwingWorker;

/**
 *
 * @author admin
 */
public class ModemTaskScheduler {

    private final PriorityQueue<ModemTask> tasks = new PriorityQueue<>();
    private SwingWorker taskPerformer;

    public void enqueueTask(ModemTask task) {

        tasks.add(task);

        if (taskPerformer != null) {
            return;
        }

        taskPerformer = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                while (tasks.peek() != null) {

                    ModemTask task = tasks.poll();
                    if (task != null) {
                        task.performTask();
                    }
                    Main.log("mtasks: " + tasks.size());
                }

                return null;
            }

            @Override
            protected void done() {
                super.done();
                taskPerformer = null;
            }

        };

        taskPerformer.execute();

    }

    public void stopTaskPerformer() {
        if (taskPerformer != null) {
            taskPerformer.cancel(true);
        }
    }

}
