package com.jps.l2app.utils;

import com.jps.l2app.main.Main;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstanceUtil {

    static File file;
    static FileChannel fileChannel;
    static FileLock lock;
    static boolean running = false;

    public static boolean checkIfAlreadyRunning(String lockName) {
        try {
            file = new File(lockName + ".lock");
            if (!file.exists()) {
                file.createNewFile();
            }            
            
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            lock = fileChannel.tryLock();
            
            if (lock == null) {
                fileChannel.close();
                return true;
            }
            
            ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } catch (IOException ex) {
              Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static void unlockFile() {
        try {
            if (lock != null) {
                lock.release();
            }
            fileChannel.close();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ShutdownHook extends Thread {

        @Override
        public void run() {
            unlockFile();
        }
    }

}
