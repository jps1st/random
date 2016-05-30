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

@Table(name = "tbl8ef7988b3a94")
@Entity
public class Submission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String submissionId;
    private String draw;
    private String raffleType;
    private String raffleNumber;
    private double betValue;

    @ManyToOne
    private Employee sender;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date receivedDate;

    @ManyToOne
    private SubmissionRaw submissionRaw;

    @ManyToOne
    private EmployeeTotals employeeTotals;

    @ManyToOne
    private DigitTotal raffleDigitTotal;

    public Submission() {
    }

    public Submission(String draw, String raffleType, String raffleNumber, double betValue) {
        this.draw = draw;
        this.raffleType = raffleType;
        this.raffleNumber = raffleNumber;
        this.betValue = betValue;
        
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

    public String getRaffleType() {
        return raffleType;
    }

    public void setRaffleType(String raffleType) {
        this.raffleType = raffleType;
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

    public String getRaffleNumber() {
        return raffleNumber;
    }

    public void setRaffleNumber(String raffleNumber) {
        this.raffleNumber = raffleNumber;
    }

    public double getBetValue() {
        return betValue;
    }

    public void setBetValue(double betValue) {
        this.betValue = betValue;
    }

    public SubmissionRaw getSubmissionRaw() {
        return submissionRaw;
    }

    public void setSubmissionRaw(SubmissionRaw submissionRaw) {
        this.submissionRaw = submissionRaw;
    }

    public EmployeeTotals getEmployeeTotals() {
        return employeeTotals;
    }

    public void setEmployeeTotals(EmployeeTotals employeeTotals) {
        this.employeeTotals = employeeTotals;
    }

    public DigitTotal getDigitTotal() {
        return raffleDigitTotal;
    }

    public void setDigitTotal(DigitTotal raffleDigitTotal) {
        this.raffleDigitTotal = raffleDigitTotal;
    }

}
