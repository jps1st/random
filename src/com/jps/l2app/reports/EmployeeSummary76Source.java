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
public class EmployeeSummary76Source implements JRDataSource {

    public static final String[] raffles = new String[]{"L2", "L3", "S3", "S2", "S3R", "P42", "P45", "P49", "P55", "4D"};
    private double totalAll = 0d;
    private final String draw;
    private final Date date;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");

    int maxItems = 0;
    private final Employee employee;
    private final String submitId;

    /**
     *
     * @param date
     * @param draw
     */
    private List<Detail> d1 = new ArrayList();
    private List<Detail> d2 = new ArrayList();

    private DecimalFormat nFormatter = new DecimalFormat("##.0");

    public EmployeeSummary76Source(List<Submission> submissions, Date date, String draw, Employee employee, String submitId) {
        this.date = date;
        this.draw = draw;
        this.employee = employee;
        this.submitId = submitId;

        List<Detail> detailListSummary = new ArrayList();

        for (String raffle : raffles) {

            Set<String> raffleNumbers = getRaffleNumbers(raffle, submissions);

            if (!raffleNumbers.isEmpty()) {
                d1.add(new Detail("", raffle, ""));
                d2.add(new Detail("", "", ""));
            }

            raffle = raffle.toLowerCase();
            //<editor-fold defaultstate="collapsed" desc="raffles">
            double raffleTotal = 0d;

            int half = (raffleNumbers.size() / 2) + raffleNumbers.size() % 2;

            int x = 0;

            for (String raffleNumber : raffleNumbers) {

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

                Detail d = new Detail(raffle, raffleNumber, "(" + nFormatter.format(raffleNumberTotal) + ")");
                if (++x <= half) {
                    d1.add(d);
                } else {
                    d2.add(d);
                }

            }

            if (raffleTotal == 0) {
                continue;
            }

            totalAll += raffleTotal;
            detailListSummary.add(new Detail(raffle, raffle.toUpperCase(), "(" + nFormatter.format(raffleTotal) + ")"));

            if (d1.size() != d2.size()) {
                d2.add(new Detail("", "", ""));
            }

            d1.add(new Detail("", "", ""));
            d2.add(new Detail("", "", ""));

            d1.add(new Detail("---", "---", "---"));
            d2.add(new Detail("---", "---", "---"));

            d1.add(new Detail("", "", ""));
            d2.add(new Detail("", "", ""));

            //</editor-fold>
        }

        for (Detail summ : detailListSummary) {
            d1.add(summ);
            d2.add(new Detail("", "", ""));
        }

        maxItems = d1.size();
        if (maxItems < d2.size()) {
            maxItems = d2.size();
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
                    return draw.toUpperCase();
                case "date":
                    return dateFormat.format(date);
                case "raf":
                    return d1.get(index).raffle.toUpperCase();
                case "num":
                    return d1.get(index).raffleNumber;
                case "amount": //rafflenumtotal
                    return d1.get(index).total;
                case "raf_1":
                    return d2.get(index).raffle.toUpperCase();
                case "num_1":
                    return d2.get(index).raffleNumber;
                case "amount_1": //rafflenumtotal
                    return d2.get(index).total;

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

    private static class Detail {

        String raffle, raffleNumber, total;

        public Detail(String raffle, String raffleNumber, String total) {
            this.raffle = raffle;
            this.raffleNumber = raffleNumber;
            this.total = total;
        }

    }

}
