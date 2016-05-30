package com.jps.l2app.devices;

import jssc.SerialPort;
import com.jps.l2app.exceptions.CMDFailException;
import com.jps.l2app.exceptions.TimeOutException;
import com.jps.l2app.main.Main;

public class Wave extends Modem {

    public Wave(SerialPort sp) {
        super(sp);
    }

    @Override
    public void cmd_initGSM() throws TimeOutException {
        Main.log("initGSM started.");
        try {
            
            int timeOut = 2_000;
            
            executeCommand("AT+CPMS?", timeOut, "OK", "ERROR");

            executeCommand("ATE0", timeOut, "OK", "ERROR"); //echo off

            executeCommand("AT+CMGF=0", timeOut, "OK", "ERROR"); //pdu mode

            executeCommand("AT+CNMI=2, 1", timeOut, "OK", "ERROR"); //cnmi

            executeCommand("AT&W", timeOut, "OK", "ERROR"); //cnmi


        } catch (CMDFailException ex) {
        }

    }

    @Override
    public String cmdGet() {
        return "AT+CMGR=";
    }

    @Override
    public String cmdModPDU() {
        return "AT+CMGF=0";
    }

    @Override
    public String cmdToggle() {
        return null;
    }

    @Override
    public String cmdStat() {
        return "AT+CPAS";
    }

    @Override
    public String cmdSQ() {
        return "AT+CSQ";
    }

    @Override
    public String cmdDel() {
        return "AT+CMGD=";
    }

    @Override
    public String cmdPID() {
        return "AT+CGSN";
    }

    @Override
    public String cmdAT() {
        return "AT";
    }

    @Override
    public String cmdSGSM() {
        return null;
    }

    @Override
    public String cmdSend() {
        return "AT+CMGS=";
    }

    @Override
    public String cmdMMode() {
        return null;
    }

}
