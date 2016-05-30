package com.jps.l2app.main;

import com.jps.l2app.devices.DeviceSpecifications;
import com.jps.l2app.devices.Shield;
import com.jps.l2app.devices.Modem;
import com.jps.l2app.devices.Wave;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.jps.l2app.exceptions.TimeOutException;
import static com.jps.l2app.main.Main.log;
import com.jps.l2app.pronton.ui.ProntonMain;
import com.jps.l2app.pronton.ui.ProntonMain_BN;
import com.jps.l2app.server.ui.LoginDialog;
import com.jps.l2app.utils.InstanceUtil;
import com.jps.l2app.utils.UIUtilities;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public final static List<DeviceSpecifications> DEV_SPECS = new ArrayList();

    public static void main(String[] args) {

        processArguments(args);

        Setup.registerIds();

        interpretSystemType();

        log("Randompi " + Setup.VERSION + " - " + Setup.users);
        log("Exclusive for " + Setup.businessName + " Stations only.");

        while (true) {

            try {

                for (DeviceSpecifications spec : DEV_SPECS) {

                    if (spec.getModem() == null || !spec.getModem().isWorking()) {
                        Main.log("Searching device for " + spec.getFunction());
                        spec.setModem(greedySearchModem(spec));
                    }
                }

                Thread.sleep(5000);
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static Modem greedySearchModem(final DeviceSpecifications spec) throws InterruptedException {
        Modem m = null;

        while (m == null) {
            m = getModem(spec);
            Thread.sleep(100);
        }

        return m;
    }

    private static Modem getModem(DeviceSpecifications specs) throws InterruptedException {

        String[] ports = specs.getPortId().matches("auto") ? SerialPortList.getPortNames() : new String[]{specs.getPortId()};

        int baudRate = specs.getDeviceType() == DeviceType.MINI ? 115200 : 4800;

        for (String p : ports) {

            //<editor-fold defaultstate="collapsed" desc="init modem">
            SerialPort s = openPort(p, baudRate);

            if (s == null) {
                continue;
            }

            Modem modem = specs.getDeviceType() == DeviceType.MINI ? new Wave(s) : new Shield(s);
            modem.cmd_start();

            Main.log(p + " listener booting...");

            Thread.sleep(10_000); //wait for modem to prepare itself.

            Main.log(p + " listener started.");

            try {
                modem.cmd_toggle();
                modem.cmd_sgsm();

                if (!modem.cmd_ATOk()) {
                    modem.cmd_toggle();
                }

                do {
                    //Thread.sleep(1000);
                } while (!modem.cmd_isDeviceFunctional());

                if (modem.cmd_ATOk()) {
                    modem.cmd_initGSM();
                    modem.cmd_modPDU();
                }

                String devId = modem.cmd_getDevId();

                if (Setup.checkDevice && !isDeviceValid(devId)) {
                    log("dvd_err");
                    System.exit(1);
                }

                modem.setWorking(true);

                if (Setup.systemType == SystemType.CLIENT || specs.getFunction() == DeviceFunctionType.TX) {
                    log("Enabling responder module.");
                    modem.startRespondThread();
                } else {
                    modem.startSQThread();
                    modem.cmd_scanInbox();
                }

                return modem;

            } catch (TimeOutException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                modem.onATError(69);
            }
            //</editor-fold>

        }

        return null;

    }

    private static SerialPort openPort(String portName, int baudRate) {
        SerialPort s = new SerialPort(portName);

        if (s.isOpened()) {
            return null;
        }

        try {

            boolean init = s.openPort() && s.setParams(baudRate, 8, 1, 0, true, true);

            s.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN);

            if (!init) {

                if (s.isOpened()) {
                    s.closePort();
                }

                return null;
            }

        } catch (SerialPortException ex) {
            return null;
        }

        return s;
    }

    private static void interpretSystemType() {
        if (Setup.systemType == null) {
            log("Function type unspecified.");
            System.exit(1);
        }

        if (Setup.systemType == SystemType.SERVER) {

            if (InstanceUtil.checkIfAlreadyRunning("server")) {
                log("server already running.");
                System.exit(1);
            }

            LoginDialog.showDiaglog();

        } else if (Setup.systemType == SystemType.CLIENT && Setup.allowClient) {

            if (InstanceUtil.checkIfAlreadyRunning("client")) {
                log("client already running.");
                System.exit(1);
            }

            if (Setup.s3Only) {
                ProntonMain_BN.showMainFrame();
            } else {
                ProntonMain.showMainFrame();
            }
        }
    }

    private static boolean isDeviceValid(String devId) {
        for (String id : Setup.DEV_IDS) {
            if (devId.matches(id)) {
                return true;
            }
        }
        return false;
    }

    private static void processArguments(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="process arguments">
        for (String arg : args) {
            switch (arg) {

                case "server":
                    Setup.systemType = SystemType.SERVER;
                    continue;

                case "client":
                    Setup.systemType = SystemType.CLIENT;
                    continue;
                    
                case "diag":
                    Setup.diag = true;
                    continue;
            }

            if (arg.startsWith("tx") || arg.startsWith("rx")) {
                String[] spec = arg.split(":");

                if (spec.length != 3) {
                    System.out.println("Invalid devspec.");
                    System.exit(1);
                }

                DeviceSpecifications specs = new DeviceSpecifications();
                specs.setFunction(spec[0].matches("rx") ? DeviceFunctionType.RX : DeviceFunctionType.TX);
                String devTyp = spec[1];

                switch (devTyp) {
                    case "shield":
                        specs.setDeviceType(DeviceType.SHIELD);
                        break;
                    case "mini":
                        specs.setDeviceType(DeviceType.MINI);
                        break;
                    default:
                        System.out.println("devspec_invalid.");
                        System.exit(1);
                }

                specs.setPortId(spec[2]);

                DEV_SPECS.add(specs);

                continue;

            }

            if (arg.contains("@")) {
                String[] split = arg.split("@");
                if (split.length == 2) {
                    Setup.dbaseUser = split[0];
                    Main.log(Setup.dbaseUser);
                    Setup.dbaseHost = split[1];
                    Main.log(Setup.dbaseHost);
                }
            } else if (arg.startsWith("fs:")) {
                try {
                    Setup.fontSize = Integer.parseInt(arg.replace("fs:", ""));
                } catch (NumberFormatException ex) {
                }
            } else {
                System.out.println(arg);
                System.out.println("arg_invalid.");
                System.exit(1);
            }

        }
        //</editor-fold>
    }

    public static void log(Object message) {
        System.out.println(new StringBuilder("[").append(UIUtilities.showTimeOnly(new Date())).append("]: ").append(message).toString());
    }

    public static enum DeviceFunctionType {
        RX, TX;
    }

    public static enum DeviceType {
        MINI, SHIELD
    }

}
