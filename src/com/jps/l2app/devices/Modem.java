package com.jps.l2app.devices;

import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.services.QueuedResponseService;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.utils.PduUtils;
import com.jps.l2app.utils.SMSTools;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import com.jps.l2app.exceptions.CMDFailException;
import com.jps.l2app.exceptions.TimeOutException;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import com.jps.l2app.utils.ProcessorService;
import com.jps.l2app.utils.Utils;

public abstract class Modem extends ModemTaskScheduler {

    private SerialPort port;

    private boolean working = false;//flag if this modem works

    private SwingWorker respondThread = null;
    private SwingWorker signalCheckThread = null;

    private List<String> cmdOutputBuffer = null; //if null then gsm is not busy
    private boolean cmdOutputBufferLocked = false;
    private boolean busy = false;

    public Modem(SerialPort sp) {
        this.port = sp;
    }

    public boolean cmd_ATOk() {

        try {
            executeCommand(cmdAT(), 500, "OK", "ERROR");
            return true;
        } catch (CMDFailException | TimeOutException ex) {
            //ex.printStackTrace();
        }

        return false;

    }

    public void cmd_toggle() throws TimeOutException {

        try {
            executeCommand(cmdToggle(), 5000, "OK", "n/a");
        } catch (CMDFailException ex) {

        }
    }

    public boolean cmd_isDeviceFunctional() throws TimeOutException {

        try {

            if (!this.cmd_ATOk()) {
                return false;
            } else {
                System.out.println("at-ok");
            }

            List<String> ex = executeCommand(cmdStat(), 5000, "OK", "ERROR");

            if (ex.isEmpty()) {
                System.out.println("Empty-cpas-response.");
                return false;
            }

            for (String string : ex) {
                if (string.trim().startsWith("%CPAS:")) {
                    Integer func = Integer.parseInt(string.replace("%CPAS:", "").trim());
                    switch (func) {
                        case 0:
                            Main.log("ME Ready");
                            return true;
                        case 2:
                            Main.log("ME status unknown.");
                            return false;
                    }

                    return func == 0;
                }
            }

        } catch (CMDFailException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void cmd_start() {

        try {
            port.addEventListener(new SerialPortEventListener() {

                StringBuilder messageBuffer = new StringBuilder();

                boolean rxCharEvent;
                byte[] buffer = null;

                @Override
                public void serialEvent(SerialPortEvent event) {

                    rxCharEvent = event.isRXCHAR() && event.getEventValue() > 0;

                    if (!rxCharEvent) {
                        return;
                    }

                    //<editor-fold defaultstate="collapsed" desc="fill buffer">
                    buffer = null;

                    try {
                        buffer = getPort().readBytes();
                    } catch (SerialPortException ex) {
                        System.err.println(ex);
                    }

                    if (buffer == null) {
                        return;
                    }
                    //</editor-fold>

                    for (byte b : buffer) {

                        //replace all + with '%' NOTE: this affects all text messages
                        if (b == '+') {
                            b = '%';
                        }

                        if (b == '>' && cmdOutputBuffer != null) { //for sms send proceed
                            cmdOutputBufferLocked = true;
                            cmdOutputBuffer.add(">");
                            cmdOutputBufferLocked = false;
                            continue;
                        }

                        boolean lineFound = ((b == '\r' || b == '\n') && (messageBuffer.length() > 0));

                        if (!lineFound) {
                            messageBuffer.append((char) b);
                            continue;
                        }

                        String msg = messageBuffer.toString().trim();

                        if (msg.isEmpty() || msg.matches("Call Ready")) {
                            //ignore
                        } else if (msg.startsWith("%CMTI:")) {
                            procNewSMS(msg);
                        } else if (cmdOutputBuffer != null) { //add to the command response buffer
                            cmdOutputBufferLocked = true;
                            cmdOutputBuffer.add(msg);
                            cmdOutputBufferLocked = false;
                        }

                        messageBuffer.setLength(0); //reset messageBuffer

                    }

                }

                public void procNewSMS(final String line) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int i = 30;
                            try {
                                i = Integer.parseInt(line.replace("%CMTI:", "").trim().split(",")[1]);
                            } catch (Exception ex) {
                                Main.log("Error@ProcessNewSMS will read from 30.");
                            }
                            evt_receivedNewMessage(i);
                        }
                    }).start();
                }

            });
        } catch (SerialPortException ex) {
            onATError(175);
            Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cmd_scanInbox() {
        for (int i = 1; i <= 30; i++) {
            readInbox(i);
        }
    }

    private void evt_receivedNewMessage(int indexNumber) {

        Main.log("Received New SMS: " + indexNumber);

        while (indexNumber >= 1) {
            readInbox(indexNumber--);
        }

    }

    public void readInbox(final int index) {
        //read
        enqueueTask(new ModemTask(ModemTask.MAX_PRIORITY) {
            @Override
            public void performTask() {
                boolean success = cmd_processInboxItem(index);
                if (success) {
                    enqueueTask(new ModemTask(ModemTask.MID_PRIORITY) {
                        @Override
                        public void performTask() {
                            _deleteSMS(index);
                        }
                    });
                }
            }
        });
        //delete

    }

    protected boolean cmd_processInboxItem(int index) {

        RawSMS sms;
        try {
            sms = processRawPDUSms(executeCommand(cmdGet() + index, 2000, "OK", "ERROR"));
        } catch (TimeOutException | CMDFailException e) {

            if (!cmd_ATOk()) {
                onATError(225);
                return false;
            }

            return cmd_processInboxItem(index);

        }

        sms = RawSMSService.getInstance().insertIfNotDuplicate(sms);

        if (sms == null) {
            return true;//duplicate
        }

        if (sms.isMultipart()) {

            if (sms.getMulti_partNum() > 1) {
                ProcessorService.processMultipartSMS(sms.getSender()); //interpret if parent is completed
            }

        } else {
            ProcessorService.interpret(sms); //interpret if singlepage
        }

        return true;

    }

    private RawSMS processRawPDUSms(List<String> data) {

        if (data == null) {
            return null;
        }

        String m = "";

        for (String string : data) {
            if (string == null || string.isEmpty()) {
                continue;
            }

            string = string.replace("+", "%");
            if (string.trim().startsWith("%CMGR:")) {
                continue;
            }

            if (!string.trim().matches("OK")) {
                m += string + "\n";
            }
        }

        if (m.isEmpty()) {
            return null;
        }

        return SMSTools.getRawSMS(m);

    }

    public void onATError(int id) {

        Main.log("DevError: " + id + ". Will reboot.");

        try {
            if (port.isOpened()) {
                port.closePort();
            }
        } catch (Exception ex) {
            Logger.getLogger(Shield.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Note: onATError method will most likely be called by the following threads.
        //Hence, their termination should be the last call;
        if (signalCheckThread != null) {
            signalCheckThread.cancel(true);
        }

        if (respondThread != null) {
            respondThread.cancel(true);
        }

        stopTaskPerformer();

        working = false;

    }

    protected List<String> executeCommand(String cmdStr, int timeOutMillis, String expSucc, String expFail) throws CMDFailException, TimeOutException {

        if (cmdStr == null) {
            return null;
        }

        if (Setup.diag) {
            Main.log("cmdStr: " + cmdStr);
        }

        while (isBusy()) {
        }

        setBusy(true);

        cmdStr = cmdStr + "\r\n";

        try {
            port.writeBytes(cmdStr.getBytes());
        } catch (SerialPortException ex) {
            onATError(316);
            return null;
        }

        expFail = expFail.replace("+", "%");

        cmdOutputBuffer = new ArrayList<>();
        long start = System.currentTimeMillis();

        while ((System.currentTimeMillis() - start) < timeOutMillis) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException i) {
            }

            if (cmdOutputBufferLocked) {
                continue;
            }

            boolean fail = false;
            try {

                for (int i = 0; i < cmdOutputBuffer.size(); i++) {

                    String string = cmdOutputBuffer.get(i);

                    if (string.trim().endsWith(expSucc)) {

                        List<String> output = cmdOutputBuffer;
                        cmdOutputBuffer = null;
                        output.remove(i);
                        setBusy(false);
                        //Main.log("Success: " + cmdStr);
                        return output;

                    } else if (string.trim().contains(expFail)) {

                        cmdOutputBuffer = null;
                        setBusy(false);

                        fail = true;

                    }
                }
            } catch (Exception ex) {
            }

            if (fail) {
                throw new CMDFailException();
            }

        }

        cmdOutputBuffer = null;

        setBusy(false);

        throw new TimeOutException();

    }

    public void cmd_sgsm() throws TimeOutException {

        try {
            executeCommand(cmdSGSM(), 15_000, "OK", "n/a");
        } catch (CMDFailException ex) {
        }

    }

    public abstract void cmd_initGSM() throws TimeOutException;

    private void _deleteSMS(int index) {

        try {
            executeCommand(cmdDel() + index, 1000, "OK", "ERROR");
        } catch (CMDFailException | TimeOutException ex) {
            //ex.printStackTrace();
        }

    }

    public String cmd_getDevId() throws TimeOutException {

        try {

            List<String> output = executeCommand(cmdPID(), 1000, "OK", "n/a");
            if (output.isEmpty()) {
                throw new TimeOutException();
            }

            return output.get(0);

        } catch (CMDFailException ex) {

        }

        throw new TimeOutException();

    }

    public void startSQThread() {

        if (signalCheckThread != null) {
            return;
        }

        signalCheckThread = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                while (true) {

                    Thread.sleep(15_000);

                    enqueueTask(new ModemTask(ModemTask.LOW_PRIORITY) {
                        @Override
                        public void performTask() {

                            try {
                                int signal = cmd_getSignalRSSI();
                                Main.log(getPort().getPortName() + " signal: " + Utils.interpretSignalQuality(signal));
                            } catch (TimeOutException ex) {
                                if (!cmd_ATOk()) {
                                    onATError(440);
                                }
                            }

                        }
                    });

                }

            }

            @Override
            protected void done() {
                super.done(); //To change body of generated methods, choose Tools | Templates.
            }

        };
        signalCheckThread.execute();

    }

    private int cmd_getSignalRSSI() throws TimeOutException {

        List<String> response = null;

        try {
            response = executeCommand(cmdSQ(), 1000, "OK", "n/a");
        } catch (CMDFailException ex) {
            Logger.getLogger(Shield.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (response == null || response.isEmpty()) {
            return 0;
        }

        String r = response.get(0);

        if (r.trim().startsWith("%CSQ:")) {
            return Integer.parseInt(r.replace("%CSQ:", "").trim().split(",")[0]);
        }

        return 0;
    }

    public void startRespondThread() {

        if (respondThread != null) {
            return;
        }

        respondThread = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                while (true) {
                    try {

                        if (SettingsService.getInstance().isAutoReplyEnabled()) {
                            procQueResp(true);
                        }

                        procQueResp(false);

                        Thread.sleep(500);

                    } catch (Exception ex) {
                        Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        };

        respondThread.execute();

    }

    protected void procQueResp(boolean procAutoResp) {

        List<QueuedResponse> r = QueuedResponseService.getInstance().getUnExpiredQueuedResponses(procAutoResp);//sets these to pending

        for (final QueuedResponse q : r) {

            enqueueTask(new ModemTask(ModemTask.MAX_PRIORITY) {
                @Override
                public void performTask() {

                    try {

                        String num = q.getSendTo();
                        String msg = q.getMessage();

                        boolean sendSMS = cmd_sendSMS(num, msg);
                        Main.log("SENT: " + sendSMS + ", " + msg);
                        q.setSendStatus(QueuedResponse.SENT);

                    } catch (TimeOutException | CMDFailException ex) {

                        if (!cmd_ATOk()) {
                            onATError(541);
                        }

                        q.setSendStatus(QueuedResponse.ERROR);

                    }

                    QueuedResponseService.getInstance().update(q);
                }
            });

        }

    }

    private boolean cmd_sendSMS(String number, String msg) throws TimeOutException, CMDFailException {
        number = "63" + RawSMSService.extractEssentialMobileNum(number);
        List<String> pduData = PduUtils.getRawPDUs(msg, number);
        for (String pdu : pduData) {
            if (!sendPDU(pdu)) {
                return false;
            }
        }
        return true;
    }

    private boolean sendPDU(String pdu) throws TimeOutException, CMDFailException {

        int atNotOkC = 0;
        while (!cmd_ATOk()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (atNotOkC > 3) {
                return false;
            }
            atNotOkC++;
        }

        while (isBusy()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setBusy(true);

        String expSucc = "OK";
        String expFail = "ERROR";

        int octets = (pdu.length() / 2) - 1;

        //<editor-fold defaultstate="collapsed" desc="sendpdu">
        String sendn = cmdSend() + octets + "\r";

        try {
            port.writeString(sendn);
            Main.log("sendn: " + sendn);
        } catch (SerialPortException ex) {
            onATError(600);
            setBusy(false);
            startSQThread();
            return false;
        }

        cmdOutputBuffer = new ArrayList<>();
        long start = System.currentTimeMillis();
        boolean foundSMSChar = false;
        while ((System.currentTimeMillis() - start) < 3000) {
            //<editor-fold defaultstate="collapsed" desc="search >">
            try {
                Thread.sleep(10);
            } catch (InterruptedException i) {
            }

            if (cmdOutputBufferLocked) {
                continue;
            }

            try {
                for (int i = 0; i < cmdOutputBuffer.size(); i++) {
                    String string = cmdOutputBuffer.get(i);
                    if (string.trim().equals(">")) {
                        foundSMSChar = true;
                        break;
                    }
                }
            } catch (Exception ex) {
            }

            if (foundSMSChar) {
                break;
            }
//</editor-fold>
        }

        cmdOutputBuffer = null;

        if (!foundSMSChar) {
            Main.log("sendPdu > not found!");
            cmdOutputBuffer = null;
            try {

                byte c = 26;
                Thread.sleep(256);
                port.writeByte(c);
                Thread.sleep(256);
                port.writeByte(c);
                Thread.sleep(256);
                port.writeString("\r\n");

            } catch (InterruptedException | SerialPortException ex) {
                Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
            }

            setBusy(false);
            startSQThread();
            throw new CMDFailException();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="mmode">
        if (cmdMMode() != null) {
            try {
                port.writeString(cmdMMode().concat("\n"));
            } catch (SerialPortException ex) {
                onATError(667);
                setBusy(false);
                startSQThread();
                return false;
            }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sleep">
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Shield.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try {
            port.writeString(pdu);
            byte c = 26;
            port.writeByte(c);
        } catch (SerialPortException ex) {
            onATError(688);
            setBusy(false);
            startSQThread();
            return false;
        }

        cmdOutputBuffer = new ArrayList<>();
        start = System.currentTimeMillis();
        int status = 0; //0: ok/fail not yet found 1: ok 2: fail
        while ((System.currentTimeMillis() - start) < 16000) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException i) {
            }

            if (cmdOutputBufferLocked) {
                continue;
            }

            try {
                for (int i = 0; i < cmdOutputBuffer.size(); i++) {
                    String string = cmdOutputBuffer.get(i);
                    if (string.trim().contains(expSucc)) {
                        status = 1;
                        break;
                    } else if (string.trim().contains(expFail)) {
                        status = 2;
                        break;
                    }
                }
            } catch (Exception ex) {
            }

            if (status > 0) {
                break;
            }

        }

        cmdOutputBuffer = null;
        setBusy(false);

        if (status == 2) {
            throw new CMDFailException();
        } else if (status == 1) {
            return true;
        }

        throw new TimeOutException();

    }

    public void cmd_modPDU() throws TimeOutException {
        try {
            executeCommand(cmdModPDU(), 5000, "OK", "n/a");
        } catch (CMDFailException ex) {
        }
    }

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }

    protected synchronized boolean isBusy() {
        return busy;
    }

    protected synchronized void setBusy(boolean busyVal) {
        busy = busyVal;
    }

    public abstract String cmdModPDU();

    public abstract String cmdGet();

    public abstract String cmdToggle();

    public abstract String cmdStat();

    public abstract String cmdSQ();

    public abstract String cmdDel();

    public abstract String cmdPID();

    public abstract String cmdAT();

    public abstract String cmdSGSM();

    public abstract String cmdSend();

    public abstract String cmdMMode();

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

}
