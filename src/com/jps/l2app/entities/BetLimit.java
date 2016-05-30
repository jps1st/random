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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author org org
 */
@Table(name = "tbl230db526b923")
@Entity
public class BetLimit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int draw; //0 all, 1 - d1, 2- d2, 3-d3

    private String raffleId; //ALL, l2, l3, s3, s3r, s2, 4d p42, p45, p49, p55

    private int limitType; //0- Shared 1- Per Employee 2-Specific

    @ManyToOne
    private Employee employee;

    private String raffleNumber;//3_ all numbers starting 3

    private double limitValue; //-1 for CLOSED

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRaffleNumber() {
        return raffleNumber;
    }

    public void setRaffleNumber(String raffleNumber) {
        this.raffleNumber = raffleNumber;
    }

    public double getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(double limitValue) {
        this.limitValue = limitValue;
    }

    @Override
    public String toString() {
        return raffleNumber + ":  " + limitValue;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public String getRaffleId() {
        return raffleId;
    }

    public void setRaffleId(String raffleId) {
        this.raffleId = raffleId;
    }

}
