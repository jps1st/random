/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.devices;

/**
 *
 * @author org org
 */
public class AT_COMMANDS {

    public static final String CBC = "$cbc";
    public static final String POWD = "$powd";

    public static enum DEVICE_TYPE {

        ARDUINO, WAVECOM
    }

    public static final DEVICE_TYPE DEVICE = DEVICE_TYPE.ARDUINO; //0 for arduino | 1 for wavecom

    public static final String GET = DEVICE == DEVICE_TYPE.ARDUINO ? "$get=" : "AT+CMGR=";
    public static final String TOGGLE = "$toggle";
    public static final String SGSM = "$sgsm";
    public static final String INIT = "$init";
    public static final String AT = DEVICE == DEVICE_TYPE.ARDUINO ? "$at" : "AT";
    public static final String PID = DEVICE == DEVICE_TYPE.ARDUINO ? "$pid" : "AT+CGSN";
    public static final String DELA = DEVICE == DEVICE_TYPE.ARDUINO ? "$dela" : "AT+CMGD=0,3";
    public static final String DEL = DEVICE == DEVICE_TYPE.ARDUINO ? "$del=" : "AT+CMGD=";
    public static final String SQ = DEVICE == DEVICE_TYPE.ARDUINO ? "$sq" : "AT+CSQ";
    public static final String STAT = DEVICE == DEVICE_TYPE.ARDUINO ? "$stat" : "AT+CPAS";
    public static final String SEND_PDU = DEVICE == DEVICE_TYPE.ARDUINO ? "$sendpdu=" : "AT+CMGS=";
    public static final String SENDM = DEVICE == DEVICE_TYPE.ARDUINO ? "$sendm=" : "";
    public static final String MOD_PDU = DEVICE == DEVICE_TYPE.ARDUINO ? "$modpdu" : "AT+CMGF=0";
    public static final String MMODE = "$mmode"; //messaging mode

}
