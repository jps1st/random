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
@Table(name = "tblba31560e228f")
@Entity
public class ProntonTransactionDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String draw;
    private String raffle;
    private String raffleNumber;
    private double bet;

    @ManyToOne
    private ProntonTransaction transaction;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRaffle() {
        return raffle;
    }

    public void setRaffle(String raffle) {
        this.raffle = raffle;
    }

    public String getRaffleNumber() {
        return raffleNumber;
    }

    public void setRaffleNumber(String raffleNumber) {
        this.raffleNumber = raffleNumber;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
    }

    public ProntonTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(ProntonTransaction transaction) {
        this.transaction = transaction;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

}
