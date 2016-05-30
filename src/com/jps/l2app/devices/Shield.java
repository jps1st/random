package com.jps.l2app.devices;

import jssc.SerialPort;
import com.jps.l2app.exceptions.CMDFailException;
import com.jps.l2app.exceptions.TimeOutException;

public class Shield extends Modem {

    public Shield(SerialPort sp) {
        super(sp);
    }

    @Override
    public void cmd_initGSM() throws TimeOutException {

        try {
            executeCommand("$init", 5000, "OK", "n/a");
        } catch (CMDFailException ex) {
        }

    }

    @Override
    public String cmdGet() {
        return "$get=";
    }

    @Override
    public String cmdModPDU() {
        return "$modpdu";
    }

    @Override
    public String cmdToggle() {
        return "$toggle";
    }

    @Override
    public String cmdStat() {
        return "$stat";
    }

    @Override
    public String cmdSQ() {
        return "$sq";
    }

    @Override
    public String cmdDel() {
        return "$del=";
    }

    @Override
    public String cmdPID() {
        return "$pid";
    }

    @Override
    public String cmdAT() {
        return "$at";
    }

    @Override
    public String cmdSGSM() {
        return "$sgsm";
    }

    @Override
    public String cmdSend() {
        return "$sendpdu=";
    }

    @Override
    public String cmdMMode() {
        return "$mmode";
    }

}
