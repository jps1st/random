package com.jps.l2app.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Table(name = "tblb72cc0c70ae7")
@Entity
public class TransactionDetail implements Serializable {

    public static final int TYPE_WIN = 1;
    public static final int TYPE_EXP = 2;
    public static final int TYPE_COLLECTION = 3;

    @Id
    private Long id;

    @ManyToOne
    private Employee employee;

    private int type;

    private double amount;
    
    private String description;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date transactionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

}
