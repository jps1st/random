/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.services.EmployeeService;
import com.jps.l2app.entities.services.QueuedResponseService;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.main.Main;
import java.text.DecimalFormat;
import java.util.Date;

/**
 *
 * @author admin
 */
public class RawSMSGenerator {

    private static String[] numbers = {
        "09125176201", "09125176202", "09125176203", "09125176204",
        "09125176205", "09125176206", "09125176207", "09125176208",
        "09125176209", "09125176210", "09125176211", "09125176212"
    };

    private static void registerEmps() {

        int count = 0;
        for (String number : numbers) {
            count++;
            Employee e = new Employee();
            e.setMobileNumber(number);
            e.setShortName("station" + count);
            e.setStationArea("area" + count);
            EmployeeService.getInstance().insert(e);
        }
    }

    static DecimalFormat l2 = new DecimalFormat("00");
    static DecimalFormat s3 = new DecimalFormat("000");
    static DecimalFormat l3 = new DecimalFormat("000");
    static DecimalFormat p3 = new DecimalFormat("000000");
    static DecimalFormat d4 = new DecimalFormat("0000");

    public static int random(int from, int to) {
        return (int) (from + ((Math.random() * to + from) - from));
    }

    static double totalSent = 0d;

    public static String genSubmit() {

        StringBuilder msg = new StringBuilder("d3.t");

        int tn = random(1, 100);//text number
        msg.append(tn);
        msg.append(".l2.");

        int i = (int) (Math.random() * 100); //number of submissions
        for (int j = 0; j < i; j++) {
            msg.append(l2.format(random(0, 99)));
            int value = 5 * random(1, 20);
            totalSent += value;
            msg.append(value);
            msg.append(".");
        }

        msg.append("e");

        return msg.toString();

    }

    public static void main(String[] args) throws InterruptedException {
//        while (true) {
//            Thread.sleep(1000 * random(0, 5));
//
//        }
    }
    
    public static void createAQR(){
        String number = "";
        
        String msg = genSubmit();  
        QueuedResponse q = new QueuedResponse();
        q.setAutoResponse(false);
        q.setQueuedDate(new Date());
        q.setMessage(msg);
        q.setSendStatus(QueuedResponse.UNPROCESSED);
        q.setSendTo(number);
        QueuedResponseService.getInstance().insert(q);
        System.out.println("totalSent: " + totalSent);
        
        
    }

    public static void createARawSMS() {
        String num = numbers[random(0, numbers.length)];
        Employee emp = EmployeeService.getInstance().getEmployee(num);
        String msg = genSubmit();
        Date date = new Date();

        RawSMS s = new RawSMS();
        s.setReceivedDate(date);
        s.setSender(num);
        s.setMessage(msg);
        s.setMultipart(false);
        s.setProcessStatus(RawSMS.PROC_STAT_UNPROCESSED);
        s.setHeader(date + "");
        s.setEmployee(emp);
        s = RawSMSService.getInstance().insertIfNotDuplicate(s);

        Main.log(s.getId() + ", " + date + ", " + num + ", " + emp.getShortName() + ", " + msg);
    }

}
