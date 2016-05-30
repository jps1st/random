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
@Table(name = "tbl624ce82d7f6f")
@Entity
public class ForwardFinancer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String mobileNumber;
    private String mobileNumber2;
    private String mobileNumber3;
    private String emailAddress1;
    private String emailAddress2;
    
    private double priority;

    private boolean active;

    private double pL2;
    private double pL3;
    private double pS3;
    private double pS2;
    private double pS3r;
    private double pP3;
    private double p4d;

    private double winL2;
    private double winL3;
    private double winS3;
    private double winS2;
    private double winS3r;
    private double winp3;
    private double win4d;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getpL2() {
        return pL2;
    }

    public void setpL2(double pL2) {
        this.pL2 = pL2;
    }

    public double getpL3() {
        return pL3;
    }

    public void setpL3(double pL3) {
        this.pL3 = pL3;
    }

    public double getpS3() {
        return pS3;
    }

    public void setpS3(double pS3) {
        this.pS3 = pS3;
    }

    public double getpS2() {
        return pS2;
    }

    public void setpS2(double pS2) {
        this.pS2 = pS2;
    }

    public double getpS3r() {
        return pS3r;
    }

    public void setpS3r(double pS3r) {
        this.pS3r = pS3r;
    }

    public double getP4d() {
        return p4d;
    }

    public void setP4d(double p4d) {
        this.p4d = p4d;
    }

    public double getWinL2() {
        return winL2;
    }

    public void setWinL2(double winL2) {
        this.winL2 = winL2;
    }

    public double getWinL3() {
        return winL3;
    }

    public void setWinL3(double winL3) {
        this.winL3 = winL3;
    }

    public double getWinS3() {
        return winS3;
    }

    public void setWinS3(double winS3) {
        this.winS3 = winS3;
    }

    public double getWinS2() {
        return winS2;
    }

    public void setWinS2(double winS2) {
        this.winS2 = winS2;
    }

    public double getWinS3r() {
        return winS3r;
    }

    public void setWinS3r(double winS3r) {
        this.winS3r = winS3r;
    }

    public double getWinp3() {
        return winp3;
    }

    public void setWinp3(double winp3) {
        this.winp3 = winp3;
    }

    public double getWin4d() {
        return win4d;
    }

    public void setWin4d(double win4d) {
        this.win4d = win4d;
    }

    public double getpP3() {
        return pP3;
    }

    public void setpP3(double pP3) {
        this.pP3 = pP3;
    }

    public String getMobileNumber2() {
        return mobileNumber2;
    }

    public void setMobileNumber2(String mobileNumber2) {
        this.mobileNumber2 = mobileNumber2;
    }

    public String getMobileNumber3() {
        return mobileNumber3;
    }

    public void setMobileNumber3(String mobileNumber3) {
        this.mobileNumber3 = mobileNumber3;
    }

    public String getEmailAddress1() {
        return emailAddress1;
    }

    public void setEmailAddress1(String emailAddress1) {
        this.emailAddress1 = emailAddress1;
    }

    public String getEmailAddress2() {
        return emailAddress2;
    }

    public void setEmailAddress2(String emailAddress2) {
        this.emailAddress2 = emailAddress2;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getName() + " - " + (active ? "Active" : "Inactive");
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

}
