/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author org org
 */
@Table(name = "tblfca0fffe1b42")
@Entity
public class Settings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private boolean autoReplyEnabled;

    private String draw1Cutoff = "10:30 AM";
    private String draw2Cutoff = "3:30 PM";
    private String draw3Cutoff = "8:30 PM";

    private String serverMobileNumber;
    private String prontonStationName;

    private boolean allowAdvanceSubmit;
    //pronton, if enabled rambolito bet for 6 combinations will be divided by 6 else all combinations' bets will be the same as the original bet.
    private boolean s3rDivideBet = true;

    //soldout
    private double sL2;
    private double sL3;
    private double sS3;
    private double sS2;
    private double sS3r;
    private double sp3;
    private double s4d;

    private int printerPaperSize;

    private boolean autoPrintIncomingSubmission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isAutoReplyEnabled() {
        return autoReplyEnabled;
    }

    public void setAutoReplyEnabled(boolean autoReplyEnabled) {
        this.autoReplyEnabled = autoReplyEnabled;
    }

    public String getDraw1Cutoff() {
        return draw1Cutoff;
    }

    public void setDraw1Cutoff(String draw1Cutoff) {
        this.draw1Cutoff = draw1Cutoff;
    }

    public String getDraw2Cutoff() {
        return draw2Cutoff;
    }

    public void setDraw2Cutoff(String draw2Cutoff) {
        this.draw2Cutoff = draw2Cutoff;
    }

    public String getDraw3Cutoff() {
        return draw3Cutoff;
    }

    public void setDraw3Cutoff(String draw3Cutoff) {
        this.draw3Cutoff = draw3Cutoff;
    }

    public String getServerMobileNumber() {
        return serverMobileNumber;
    }

    public void setServerMobileNumber(String serverMobileNumber) {
        this.serverMobileNumber = serverMobileNumber;
    }

    public String getProntonStationName() {
        return prontonStationName;
    }

    public void setProntonStationName(String prontonStationName) {
        this.prontonStationName = prontonStationName;
    }

    public int getPrinterPaperSize() {
        if (printerPaperSize == 0) {
            printerPaperSize = 1;
        }
        return printerPaperSize;
    }

    public void setPrinterPaperSize(int printerPaperSize) {
        this.printerPaperSize = printerPaperSize;
    }

    public boolean isAutoPrintIncomingSubmission() {
        return autoPrintIncomingSubmission;
    }

    public void setAutoPrintIncomingSubmission(boolean autoPrintIncomingSubmission) {
        this.autoPrintIncomingSubmission = autoPrintIncomingSubmission;
    }

    public double getsL2() {
        return sL2;
    }

    public void setsL2(double sL2) {
        this.sL2 = sL2;
    }

    public double getsL3() {
        return sL3;
    }

    public void setsL3(double sL3) {
        this.sL3 = sL3;
    }

    public double getsS3() {
        return sS3;
    }

    public void setsS3(double sS3) {
        this.sS3 = sS3;
    }

    public double getsS2() {
        return sS2;
    }

    public void setsS2(double sS2) {
        this.sS2 = sS2;
    }

    public double getsS3r() {
        return sS3r;
    }

    public void setsS3r(double sS3r) {
        this.sS3r = sS3r;
    }

    public double getS4d() {
        return s4d;
    }

    public void setS4d(double s4d) {
        this.s4d = s4d;
    }

    public double getSp3() {
        return sp3;
    }

    public void setSp3(double sp3) {
        this.sp3 = sp3;
    }

    public boolean isAllowAdvanceSubmit() {
        return allowAdvanceSubmit;
    }

    public void setAllowAdvanceSubmit(boolean allowAdvanceSubmit) {
        this.allowAdvanceSubmit = allowAdvanceSubmit;
    }

    public boolean isS3rDivideBet() {
        return s3rDivideBet;
    }

    public void setS3rDivideBet(boolean s3rDivideBet) {
        this.s3rDivideBet = s3rDivideBet;
    }

}
