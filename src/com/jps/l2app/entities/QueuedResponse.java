/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Table(name = "tbl75b86a679706")
@Entity
public class QueuedResponse implements Serializable {

    public static final transient int UNPROCESSED = 0;
    public static final transient int EXPIRED = 1;
    public static final transient int ERROR = 2;
    public static final transient int SENT = 3;
    public static final transient int PRONTON_REPLY = 4;
    public static final transient int PENDING = 5;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Lob
    private String message;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date queuedDate;
    private String sendTo;
    @Basic(optional = false)
    private int sendStatus; //0 unprocessed, 1 expired, 2 error, 3 Sent
    private boolean autoResponse;
    @ManyToOne
    private Employee employee;

    public QueuedResponse() {
    }

    public QueuedResponse(String message, boolean autoResponse, Employee employee) {
        this.message = message;
        this.autoResponse = autoResponse;
        this.employee = employee;
        this.queuedDate = new Date();
        this.sendStatus = UNPROCESSED;
        this.sendTo = employee.getMobileNumber();
    }
    
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getQueuedDate() {
        return queuedDate;
    }

    public void setQueuedDate(Date queuedDate) {
        this.queuedDate = queuedDate;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    @Override
    public String toString() {
        String stat = ""; //0 unprocessed, 1 expired, 2 error, 3 Sent
        switch (sendStatus) {
            case 0:
                stat = "PENDING: ";
                break;
            case 1:
                stat = "EXPIRED: ";
                break;
            case 2:
                stat = "ERROR SENDING: ";
                break;
            case 3:
                stat = "SENT: ";
                break;
        }

        return stat + " TO: " + getSendTo() + "; MESSAGE: " + getMessage();
    }
    
    public String getStatusString(){
        String stat = ""; //0 unprocessed, 1 expired, 2 error, 3 Sent
        switch (sendStatus) {
            case 0:
                stat = "PENDING";
                break;
            case 1:
                stat = "EXPIRED";
                break;
            case 2:
                stat = "ERROR SENDING";
                break;
            case 3:
                stat = "SENT";
                break;
        }

        return stat;
    }

    public boolean isAutoResponse() {
        return autoResponse;
    }

    public void setAutoResponse(boolean autoResponse) {
        this.autoResponse = autoResponse;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
