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

@Table(name = "tbla5f4ae8b6d5f")
@Entity
public class SubmissionRaw implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String draw;
    private String submissionId;
    private double totalValue;

    @ManyToOne
    private Employee sender;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date receivedDate;

    public SubmissionRaw() {
    }

    public SubmissionRaw(String draw, String submissionId, Employee sender, Date receivedDate) {
        this.draw = draw;
        this.submissionId = submissionId;
        this.sender = sender;
        this.receivedDate = receivedDate;
        
    }
    
    

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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Employee getSender() {
        return sender;
    }

    public void setSender(Employee sender) {
        this.sender = sender;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

}
