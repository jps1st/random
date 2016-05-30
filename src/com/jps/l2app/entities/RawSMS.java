package com.jps.l2app.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "tbl59dbae0281bb")
@Entity
public class RawSMS implements Serializable {

    public static transient final int PROC_STAT_INCOMPLETE = 404;
    public static transient final int PROC_STAT_UNPROCESSED = 0;
    public static transient final int PROC_STAT_EXPIRED = 1;
    public static transient final int PROC_STAT_ERROR = 2;
    public static transient final int PROC_STAT_OK = 3;
    public static transient final int PROC_STAT_NONSUBMIT = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String header;
    @Lob
    private String message;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date receivedDate;
    private String sender;
    @Basic(optional = false)
    private int processStatus = 0; 
    @ManyToOne
    private Employee employee;
    @ManyToOne
    private QueuedResponse response;

    private boolean submitError;

    private boolean multipart = false;
    private String multipartId;
    private int multi_parts;
    private int multi_partNum;
    @ManyToOne
    private RawSMS multi_parent; //if this is a multipart sms this there must be a parent
    private int multi_partsFound = 0;

    @Basic(optional = false)
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified_timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.getSender() + ": " + this.getMessage();
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public QueuedResponse getResponse() {
        return response;
    }

    public void setResponse(QueuedResponse response) {
       // this.response = response;
    }

    public boolean isSubmitError() {
        return submitError;
    }

    public void setSubmitError(boolean submitError) {
        this.submitError = submitError;
    }

    public boolean isMultipart() {
        return multipart;
    }

    public void setMultipart(boolean multipart) {
        this.multipart = multipart;
    }

    public String getMultipartId() {
        return multipartId;
    }

    public void setMultipartId(String multipartId) {
        this.multipartId = multipartId;
    }

    public int getMulti_parts() {
        return multi_parts;
    }

    public void setMulti_parts(int multi_parts) {
        this.multi_parts = multi_parts;
    }

    public int getMulti_partNum() {
        return multi_partNum;
    }

    public void setMulti_partNum(int multi_partNum) {
        this.multi_partNum = multi_partNum;
    }

    public RawSMS getMulti_parent() {
        return multi_parent;
    }

    public void setMulti_parent(RawSMS multi_parent) {
        this.multi_parent = multi_parent;
    }

    public int getMulti_partsFound() {
        return multi_partsFound;
    }

    public void setMulti_partsFound(int multi_partsFound) {
        this.multi_partsFound = multi_partsFound;
    }

    public Date getModified_timestamp() {
        return modified_timestamp;
    }

    public void setModified_timestamp(Date modified_timestamp) {
        this.modified_timestamp = modified_timestamp;
    }

}
