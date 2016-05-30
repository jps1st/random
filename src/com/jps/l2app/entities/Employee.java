package com.jps.l2app.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "tbl603128823bb5")
@Entity
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String mobileNumber;
    private String mobileNumber1;
    
    private String shortName;
    private String stationArea;//area
    private String passkey;
    private boolean pronton = false;
    
    private double pL2;
    private double pL3;
    private double pS3;
    private double pS2;
    private double pS3r;
    private double p3;
    private double p4d;

    private double p42;
    private double p45;
    private double p49;
    private double p55;

    private double wL2;
    private double wL3;
    private double wS3;
    private double wS2;
    private double wS3r;
    private double wP3;
    private double w4d;
    
    @ManyToOne
    private StationGroup stationGroup;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStationArea() {
        return stationArea;
    }

    public void setStationArea(String stationArea) {
        this.stationArea = stationArea;
    }

    @Override
    public String toString() {
        return shortName;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public boolean isPronton() {
        return pronton;
    }

    public void setPronton(boolean pronton) {
        this.pronton = pronton;
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

    public double getP42() {
        return p42;
    }

    public void setP42(double p42) {
        this.p42 = p42;
    }

    public double getP45() {
        return p45;
    }

    public void setP45(double p45) {
        this.p45 = p45;
    }

    public double getP49() {
        return p49;
    }

    public void setP49(double p49) {
        this.p49 = p49;
    }

    public double getP55() {
        return p55;
    }

    public void setP55(double p55) {
        this.p55 = p55;
    }

    public double getP4d() {
        return p4d;
    }

    public void setP4d(double p4d) {
        this.p4d = p4d;
    }

    public double getP3() {
        return p3;
    }

    public void setP3(double p3) {
        this.p3 = p3;
    }

    public double getwL2() {
        return wL2;
    }

    public void setwL2(double wL2) {
        this.wL2 = wL2;
    }

    public double getwL3() {
        return wL3;
    }

    public void setwL3(double wL3) {
        this.wL3 = wL3;
    }

    public double getwS3() {
        return wS3;
    }

    public void setwS3(double wS3) {
        this.wS3 = wS3;
    }

    public double getwS2() {
        return wS2;
    }

    public void setwS2(double wS2) {
        this.wS2 = wS2;
    }

    public double getwS3r() {
        return wS3r;
    }

    public void setwS3r(double wS3r) {
        this.wS3r = wS3r;
    }

    public double getwP3() {
        return wP3;
    }

    public void setwP3(double wP3) {
        this.wP3 = wP3;
    }

    public double getW4d() {
        return w4d;
    }

    public void setW4d(double w4d) {
        this.w4d = w4d;
    }

    public String getMobileNumber1() {
        return mobileNumber1;
    }

    public void setMobileNumber1(String mobileNumber1) {
        this.mobileNumber1 = mobileNumber1;
    }

    public StationGroup getStationGroup() {
        return stationGroup;
    }

    public void setStationGroup(StationGroup stationGroup) {
        this.stationGroup = stationGroup;
    }

}
