/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.reports;

import com.jps.l2app.entities.services.ProntonTransactionDetailService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class ProntonSummarySource implements JRDataSource {

    public static final String[] raffles = new String[]{"L2", "L3", "S3", "S2", "S3R", "P42", "P45", "P49", "P55", "4D"};
    private double totalAll = 0d;
    private final String draw;
    private final Date date;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    int maxItems = 0;

    private static class Detail {

        String raffle, raffleNumber, total;

        public Detail(String raffle, String raffleNumber, String total) {
            this.raffle = raffle;
            this.raffleNumber = raffleNumber;
            this.total = total;
        }

    }

    /**
     *
     * @param date
     * @param draw
     */
    private List<Detail> detailList1 = new ArrayList();
    private List<Detail> detailList2 = new ArrayList();

    private DecimalFormat nFormatter = new DecimalFormat("##");

    public ProntonSummarySource(Date date, String draw) {
        this.date = date;
        this.draw = draw;

        List<Detail> detailList = new ArrayList();

        for (String raffle : raffles) {
            //<editor-fold defaultstate="collapsed" desc="raffles">
            double raffleTotal = 0d;

            List<String> raffleNumbers = ProntonTransactionDetailService.getInstance().getRaffleNumbers(date, draw, raffle);

            for (String raffleNumber : raffleNumbers) {
                double raffleNumberTotal = ProntonTransactionDetailService.getInstance().getRaffleNumberTotal(date, draw, raffle, raffleNumber);
                raffleTotal += raffleNumberTotal;

                detailList.add(new Detail(raffle, raffleNumber, nFormatter.format(raffleNumberTotal)));
            }

            totalAll += raffleTotal;

            detailList.add(new Detail(raffle, "Total", nFormatter.format(raffleTotal)));

            if (raffleNumbers.size() % 2 != 1) {
                detailList.add(new Detail("", "", ""));
            }

            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            //</editor-fold>
        }

        // this.iterator = detailList.iterator();
        boolean x = true;
        for (Detail d : detailList) {

            if (x) {
                detailList1.add(d);
                x = false;
                continue;
            }
            detailList2.add(d);
            x = true;
        }

        maxItems = detailList1.size();
        if (maxItems < detailList2.size()) {
            maxItems = detailList2.size();
        }

    }

    int index = -1;

    @Override
    public boolean next() throws JRException {
        return index++ < maxItems;

    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        try {
            switch (jrf.getName()) {
                case "draw":
                    return draw;
                case "date":
                    return dateFormat.format(date);
                case "raf":
                    return detailList1.get(index).raffle;
                case "num":
                    return detailList1.get(index).raffleNumber;
                case "amount": //rafflenumtotal
                    return detailList1.get(index).total;
                case "raf_1":
                    return detailList2.get(index).raffle;
                case "num_1":
                    return detailList2.get(index).raffleNumber;
                case "amount_1": //rafflenumtotal
                    return detailList2.get(index).total;
                case "total": //total all
                    return totalAll + "";
                case "station":
                    return SettingsService.getInstance().getSettingsObject().getProntonStationName();
                case "cname":
                    return Setup.businessName;
            }
        } catch (Exception e) {
        }
        return "";
    }

}
