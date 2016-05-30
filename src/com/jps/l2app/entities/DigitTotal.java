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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author org org
 */
@Table(name = "tbl1c43f381c078")
@Entity
public class DigitTotal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date raffleDate;
    private String draw;
    private String raffleType;
    private String raffleDigit;
    @Lob
    private String detail;
    private double total;
    private double forwarded;
    private boolean win;
    
    private boolean needsRefresh;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRaffleType() {
        return raffleType;
    }

    public void setRaffleType(String raffleType) {
        this.raffleType = raffleType;
    }

    public String getRaffleDigit() {
        return raffleDigit;
    }

    public void setRaffleDigit(String raffleDigit) {
        this.raffleDigit = raffleDigit;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getRaffleDate() {
        return raffleDate;
    }

    public void setRaffleDate(Date raffleDate) {
        this.raffleDate = raffleDate;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getForwarded() {
        return forwarded;
    }

    public void setForwarded(double forwarded) {
        this.forwarded = forwarded;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

}
