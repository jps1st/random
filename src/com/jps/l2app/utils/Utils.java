package com.jps.l2app.utils;

import com.jps.l2app.main.Main;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 * @author org org
 */
public class Utils {


    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int date = c.get(Calendar.DATE);
        Main.log("date = " + date);
    }

    public static double checkForWin6(String raffNum, double value) {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int date = c.get(Calendar.DATE);

        boolean win6 = false;
        switch (raffNum) {
            case "01":
            case "02":
            case "03":
            case "12":
            case "13":
                win6 = true;
        }

        if (!win6) {
            try {
                int i = Integer.parseInt(raffNum);
                win6 = i == date;
            } catch (Exception e) {
            }
        }

        if (win6) {
            double d = value + (value * 0.25);
            d = d + (5 - (d % 5));
            return d;
        }

        return value;
    }

    private static String[] closed = new String[]{"01", "15", "17", "29", "30"};

    public static boolean isClosed(String raffN) {

        for (String c : closed) {
            if (raffN.contains(c)) {
                return true;
            }
        }
        return false;

    }

    public static double solveRetained(double val, double firstSlot, double secondslot, double thirdslot, double fourthslot) {
        double retdom = 0;
        double f1 = val - firstSlot;
        if (f1 > 0) {
            double f2 = f1 - secondslot;
            if (f2 > 0) {
                retdom = retdom + secondslot;
                double f3 = f2 - secondslot;
                if (f3 > 0) {
                    double f4 = f3 - thirdslot;
                    if (f4 > 0) {
                        retdom = retdom + fourthslot;
                    } else if (fourthslot > 1) {
                        retdom = retdom + f3;
                    }
                }
            } else {
                retdom = retdom + f1;
            }
        }
        return retdom;
    }

    public static int getDays(Date bday) {
        if (bday == null) {
            return 0;
        }
        LocalDate systrt = new LocalDate(new java.util.Date());
        LocalDate bdate = new LocalDate(bday);
        Period p = new Period(bdate, systrt, PeriodType.days());
        return p.getDays();

    }

    public static int getMonths(Date bday) {
        if (bday == null) {
            return 0;
        }
        LocalDate systrt = new LocalDate(new java.util.Date());
        LocalDate bdate = new LocalDate(bday);
        Period p = new Period(bdate, systrt, PeriodType.months());
        return p.getMinutes();
    }

    public static int getWeeks(Date bday) {
        if (bday == null) {
            return 0;
        }
        LocalDate systrt = new LocalDate(new java.util.Date());
        LocalDate bdate = new LocalDate(bday);
        Period p = new Period(bdate, systrt, PeriodType.weeks());
        return p.getMinutes();
    }

    public static int getHours(Date bday) {
        if (bday == null) {
            return 0;
        }
        LocalDate systrt = new LocalDate(new java.util.Date());
        LocalDate bdate = new LocalDate(bday);
        Period p = new Period(bdate, systrt, PeriodType.hours());
        return p.getMinutes();
    }

    public static int getMinutes(Date bday) {
        if (bday == null) {
            return 0;
        }
        LocalDate systrt = new LocalDate(new java.util.Date());
        LocalDate bdate = new LocalDate(bday);
        Period p = new Period(bdate, systrt, PeriodType.minutes());
        return p.getMinutes();
    }

    public static double getBet(String s, int raffleDigits) {
        long raw = Long.parseLong(s);
        boolean neg = raw < 0;
        if (neg) {
            s = "" + (raw * -1);
        }

        try {
            double bet = Double.parseDouble(s.substring(raffleDigits));
            if (neg) {
                bet = bet * -1;
            }
            return bet;
        } catch (Exception e) {
        }

        return 0d;
    }

    public static String getDigits(String s, int min_expected_digits) {

        long raw = Long.parseLong(s);
        boolean neg = raw < 0;
        if (neg) {
            s = "" + (raw * -1);
        }

        try {

            return (s.substring(0, min_expected_digits));
        } catch (NumberFormatException e) {
        }
        return "";
    }

    public static String interpretSignalQuality(int signal) {

        if (signal <= 9) {
            return "BAD";
        }

        if (signal <= 14) {
            return "OK";
        }

        if (signal <= 19) {
            return "GOOD";
        }

        if (signal <= 30) {
            return "EXCELLENT";
        }

        return "OFFLINE";

    }

}
