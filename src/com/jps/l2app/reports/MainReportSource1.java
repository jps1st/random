/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.reports;

import com.jps.l2app.main.Setup;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class MainReportSource1 implements JRDataSource {

    int current = -1;
    double grandTotal;
    int rowCount;
    private TableModel model;

    public MainReportSource1(TableModel model) {
        rowCount = model.getRowCount();
        this.model = model;
    }

    @Override
    public boolean next() throws JRException {

        current++;

        if (current == rowCount) {
            return true;
        } else if (current < rowCount) {
            Object total = model.getValueAt(current, model.getColumnCount() - 1);
            grandTotal += Double.parseDouble(total.toString());
            return true;
        }

        return false;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        String name = jrf.getName();

        if (current == rowCount) {
            switch (name) {
                case "draw":
                    return "------";
                case "raffle":
                    return "------";
                case "digit":
                    return "Gross:";
                case "total":
                    return grandTotal + "";
            }
        } else {
            switch (name) {
                case "draw":
                    return model.getValueAt(current, 1);
                case "raffle":
                    return model.getValueAt(current, 2);
                case "digit":
                    return model.getValueAt(current, 3);
                case "total":
                    return model.getValueAt(current, model.getColumnCount() - 1) + "";
                case "gross":
                    return grandTotal + "";
                case "bzn":
                    return Setup.businessName;
            }
        }

        return "n/a";
    }

}
