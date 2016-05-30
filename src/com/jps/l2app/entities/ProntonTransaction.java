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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author org org
 */
@Table(name = "tbl1edd50d9f0a0")
@Entity
public class ProntonTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String draw;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date transactionTimeStamp;

    private double total;

    @OneToOne
    private QueuedResponse submitSMS;

    @OneToOne
    private RawSMS serverResponse;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionTimeStamp(Date transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }

    public RawSMS getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(RawSMS serverResponse) {
        this.serverResponse = serverResponse;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public QueuedResponse getSubmitSMS() {
        return submitSMS;
    }

    public void setSubmitSMS(QueuedResponse submitSMS) {
        this.submitSMS = submitSMS;
    }

}
