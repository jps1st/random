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
@Table(name = "tbl8d57a39f670c")
@Entity
public class GlobalLimit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String raffle;
    private String raffleNumber;
    private double limitValue;
    
    private int draw; //0 all
    private double winPerPiso = 0d;
    
    @ManyToOne
    private ForwardFinancer financer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        if (raffleNumber == null || raffleNumber.isEmpty()) {
            this.raffleNumber = "ALL";
        } else {
            this.raffleNumber = raffleNumber;
        }
    }

    public double getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(double limitValue) {
        this.limitValue = limitValue;
    }

    @Override
    public String toString() {
        return "RN: " + raffleNumber + "    LIM: " + limitValue;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public ForwardFinancer getFinancer() {
        return financer;
    }

    public void setFinancer(ForwardFinancer financer) {
        this.financer = financer;
    }

    public double getWinPerPiso() {
        return winPerPiso;
    }

    public void setWinPerPiso(double winPerPiso) {
        this.winPerPiso = winPerPiso;
    }

}
