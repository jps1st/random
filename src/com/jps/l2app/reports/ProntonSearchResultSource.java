/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.reports;

import com.jps.l2app.entities.ProntonTransactionDetail;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class ProntonSearchResultSource implements JRDataSource {

    private final Iterator<ProntonTransactionDetail> iterator;
    private ProntonTransactionDetail current;

    public ProntonSearchResultSource(List<ProntonTransactionDetail> details) {
        this.iterator = details.iterator();
    }

    @Override
    public boolean next() throws JRException {
        if (iterator.hasNext()) {
            this.current = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        switch (jrf.getName()) {
            case "det":
                return Integer.toString(current.getId(), 36).toUpperCase();
            case "draw":
                return current.getDraw();
            case "raf":
                return current.getRaffle();
            case "amount":
                return current.getBet() + "";
            case "num":
                return current.getRaffleNumber();
        }
        return "";
    }

}
