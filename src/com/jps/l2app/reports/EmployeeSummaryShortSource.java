/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.reports;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.Submission;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class EmployeeSummaryShortSource implements JRDataSource {

    public static final String[] raffles = new String[]{"L2", "L3", "S3", "S2", "S3R", "P42", "P45", "P49", "P55", "4D"};
    private double totalAll = 0d;

    private final Employee employee;
    private final String submitId;
    private final String draw;
    private final Date date;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private DecimalFormat nFormatter = new DecimalFormat("##.0");

    private List<Detail> detailList = new ArrayList();

    public EmployeeSummaryShortSource(List<Submission> submissions, Date date, String draw, Employee employee, String submitId) {
        this.date = date;
        this.draw = draw;
        this.employee = employee;
        this.submitId = submitId;

        for (String raffle : raffles) {
            //<editor-fold defaultstate="collapsed" desc="raffles">
            double raffleTotal = 0d;

            Set<String> raffleNumbers = getRaffleNumbers(raffle, submissions);

            raffle = raffle.toLowerCase();

            for (String raffleNumber : raffleNumbers) {

                raffleNumber = raffleNumber.toLowerCase();

                List<Submission> toRemove = new ArrayList<>();
                double raffleNumberTotal = 0d;

                for (Submission s : submissions) {
                    if (s.getRaffleType().toLowerCase().equals(raffle) && s.getRaffleNumber().equals(raffleNumber)) {
                        raffleNumberTotal += s.getBetValue();
                        toRemove.add(s);
                    }
                }

                submissions.removeAll(toRemove);
                raffleTotal += raffleNumberTotal;

                detailList.add(new Detail(raffle, raffleNumber, "( " + nFormatter.format(raffleNumberTotal) + " )"));

            }

            totalAll += raffleTotal;

            if (raffleTotal == 0) {
                continue;
            }

//<editor-fold defaultstate="collapsed" desc="pads">
            int padding = 4 - ((raffleNumbers.size()) % 4);

            for (int x = 0; x < padding; x++) {
                detailList.add(new Detail("", "", ""));
            }

            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));

            detailList.add(new Detail(raffle, "TOTAL", nFormatter.format(raffleTotal)));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));

            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
            detailList.add(new Detail("", "", ""));
//</editor-fold>
            //</editor-fold>
        }

    }

    int index = -1;

    @Override
    public boolean next() throws JRException {
        return index++ < detailList.size();

    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        try {
            switch (jrf.getName()) {
                case "draw":
                    return draw.toUpperCase();
                case "date":
                    return dateFormat.format(date);
                case "raf":
                    return detailList.get(index).raffle.toUpperCase();
                case "num":
                    return detailList.get(index).raffleNumber;
                case "amount": //rafflenumtotal
                    return detailList.get(index).total;
                case "total": //total all
                    return totalAll + "";
                case "station":
                    return this.employee.getShortName() + "    " + draw.toUpperCase() + "." + submitId.toUpperCase();
            }
        } catch (Exception e) {
        }
        return "";
    }

    private Set<String> getRaffleNumbers(String raffleType, List<Submission> submissions) {
        Set<String> nums = new LinkedHashSet<>();
        for (Submission submission : submissions) {
            if (submission.getRaffleType().toLowerCase().equals(raffleType.toLowerCase())) {
                nums.add(submission.getRaffleNumber());
            }
        }
        return nums;
    }

}

class Detail {

    String raffle, raffleNumber, total;

    public Detail(String raffle, String raffleNumber, String total) {
        this.raffle = raffle;
        this.raffleNumber = raffleNumber;
        this.total = total;
    }

}
