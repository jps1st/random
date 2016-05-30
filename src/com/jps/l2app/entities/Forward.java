
package com.jps.l2app.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author org org
 */
@Table(name = "tbl1a7c2a8cc712")
@Entity
public class Forward implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date forwardDate;

    private String draw;

    @Lob
    private String forwardScript;
    
    private double grossCollection;
    
    private double commission;
    
    private double totalWins;

    private String winDetails;
    
    private double netForwardAmount;//grossCollection - commission - totalwins
    
    @ManyToOne
    private ForwardFinancer financer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getForwardDate() {
        return forwardDate;
    }

    public void setForwardDate(Date forwardDate) {
        this.forwardDate = forwardDate;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public ForwardFinancer getFinancer() {
        return financer;
    }

    public void setFinancer(ForwardFinancer financer) {
        this.financer = financer;
    }

    public double getGrossCollection() {
        return grossCollection;
    }

    public void setGrossCollection(double grossCollection) {
        this.grossCollection = grossCollection;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getNetRemittance() {
        return netForwardAmount;
    }

    public void setNetRemittance(double netRemittance) {
        this.netForwardAmount = netRemittance;
    }

    public String getForwardScript() {
        return forwardScript;
    }

    public void setForwardScript(String forwardScript) {
        this.forwardScript = forwardScript;
    }

    public double getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(double totalWins) {
        this.totalWins = totalWins;
    }

    public String getWinDetails() {
        return winDetails;
    }

    public void setWinDetails(String winDetails) {
        this.winDetails = winDetails;
    }

    public double getNetForwardAmount() {
        return netForwardAmount;
    }

    public void setNetForwardAmount(double netForwardAmount) {
        this.netForwardAmount = netForwardAmount;
    }

}
