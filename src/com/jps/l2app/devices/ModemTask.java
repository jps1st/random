package com.jps.l2app.devices;

/**
 *
 * @author admin
 */
public abstract class ModemTask implements Comparable<ModemTask> {

    public static final int MAX_PRIORITY = 1;
    public static final int MID_PRIORITY = 5;
    public static final int LOW_PRIORITY = 10;

    private int priority = LOW_PRIORITY;

    public ModemTask(int priority) {
        this.priority = priority;
    }

    public abstract void performTask();

    @Override
    public int compareTo(ModemTask o) {
        try {
            return priority > o.priority ? 1 : (priority == o.priority ? 0 : -1);
        } catch (Exception e) {
        }
        return 0;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
