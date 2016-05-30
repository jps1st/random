/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.devices;

import com.jps.l2app.main.Main;

/**
 *
 * @author admin
 */
public class DeviceSpecifications {

    private Main.DeviceType deviceType;
    private String portId;//can be auto
    private Main.DeviceFunctionType function;
    private Modem modem;
   

    public Main.DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Main.DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public Main.DeviceFunctionType getFunction() {
        return function;
    }

    public void setFunction(Main.DeviceFunctionType function) {
        this.function = function;
    }

    public Modem getModem() {
        return modem;
    }

    public void setModem(Modem modem) {
        this.modem = modem;
    }

}
