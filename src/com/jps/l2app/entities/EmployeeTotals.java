/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author org org
 */
@Table(name = "tbl03bd63238950")
@Entity
public class EmployeeTotals implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private Employee employee;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date raffleDate;

    private String draw;

    private double l2Total;
    private double l3Total;
    private double s3Total;
    private double s2Total;
    private double s3rTotal;
    private double d4Total;
    private double p3Total;
    private double gross;
    private double comm;
    //will be deprecated.
    private double p42Total;
    private double p45Total;
    private double p49Total;
    private double p55Total;
    private boolean collected;

    private boolean needsRefresh;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public Date getRaffleDate() {
        return raffleDate;
    }

    public void setRaffleDate(Date raffleDate) {
        this.raffleDate = raffleDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public double getL2Total() {
        return l2Total;
    }

    public void setL2Total(double l2Total) {
        this.l2Total = l2Total;
 
    }

    public double getL3Total() {
        return l3Total;
    }

    public void setL3Total(double l3Total) {
        this.l3Total = l3Total;

    }

    public double getS3Total() {
        return s3Total;
    }

    public void setS3Total(double s3Total) {
        this.s3Total = s3Total;

    }

    public double getP42Total() {
        return p42Total;
    }

    public void setP42Total(double p42Total) {
        this.p42Total = p42Total;

    }

    public double getP45Total() {
        return p45Total;
    }

    public void setP45Total(double p45Total) {
        this.p45Total = p45Total;

    }

    public double getP49Total() {
        return p49Total;
    }

    public void setP49Total(double p49Total) {
        this.p49Total = p49Total;

    }

    public double getP55Total() {
        return p55Total;

    }

    public void setP55Total(double p55Total) {
        this.p55Total = p55Total;
      
    }

    public double getD4Total() {
        return d4Total;
    }

    public void setD4Total(double d4Total) {
        this.d4Total = d4Total;
      
    }

    public void updateGross() {
        gross = l2Total
                + l3Total
                + s3Total
                + s2Total
                + s3rTotal
                + d4Total
                + p3Total
                + p42Total
                + p45Total
                + p49Total
                + p55Total;

        comm = l2Total * (employee.getpL2() / 100)
                + l3Total * (employee.getpL3() / 100)
                + s3Total * (employee.getpS3() / 100)
                + s2Total * (employee.getpS2() / 100)
                + s3rTotal * (employee.getpS3r() / 100)
                + d4Total * (employee.getP4d() / 100)
                + p3Total * (employee.getP3() / 100)
                + p42Total * (employee.getP42() / 100)
                + p45Total * (employee.getP45() / 100)
                + p49Total * (employee.getP49() / 100)
                + p55Total * (employee.getP55() / 100);
    }

    public double getGross() {
        return gross;
    }

    public void setGross(double gross) {
        this.gross = gross;
    }

    public double getS2Total() {
        return s2Total;
    }

    public void setS2Total(double s2Total) {
        this.s2Total = s2Total;
       
    }

    public double getS3rTotal() {
        return s3rTotal;
    }

    public void setS3rTotal(double s3rTotal) {
        this.s3rTotal = s3rTotal;
        
    }

    public double getComm() {
        return comm;
    }

    public void setComm(double comm) {
        this.comm = comm;
    }

    public double getP3Total() {
        return p3Total;
    }

    public void setP3Total(double p3Total) {
        this.p3Total = p3Total;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

}
