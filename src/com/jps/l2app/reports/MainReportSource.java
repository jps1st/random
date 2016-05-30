/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.reports;

import com.jps.l2app.main.Setup;
import java.util.Iterator;
import java.util.List;
import com.jps.l2app.server.ui.MainFrame;
import com.jps.l2app.server.ui.MainFrame.TableData;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class MainReportSource implements JRDataSource {

    List<MainFrame.TableData> tableData;
    private final Iterator<MainFrame.TableData> iterator;
    private MainFrame.TableData current;

    public MainReportSource(List<MainFrame.TableData> tableData) {
        this.tableData = tableData;
        this.iterator = tableData.iterator();
    }

    @Override
    public boolean next() throws JRException {
        boolean next = iterator.hasNext();
        if (next) {
            this.current = iterator.next();
        }
        return next;
    }

//    	<field name="draw" class="java.lang.String"/>
//	<field name="raffle" class="java.lang.String"/>
//	<field name="digit" class="java.lang.String"/>
//	<field name="detail" class="java.lang.String"/>
//	<field name="total" class="java.lang.String"/>
//	<field name="gross" class="java.lang.String"/>
    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        String name = jrf.getName();
        switch (name) {
            case "draw":
                return current.getDraw();
            case "raffle":
                return current.getRaffle();
            case "digit":
                return current.getRaffleNum();
            case "detail":
                return current.getSimpleDetailString();
            case "total":
                return current.getTotal() + "";
            case "bzn":
                return Setup.businessName;

        }
        return "n/a";
    }

}
