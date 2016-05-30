/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.utils;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.entities.services.EmployeeService;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author org org
 */
public class ListCellRendererImp extends DefaultListCellRenderer {

    private final SimpleDateFormat dispDateFormatter = new SimpleDateFormat("hh:mm:ss aa dd-MMM-yyyy");

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int i = index + 1;
        if (value instanceof RawSMS) {
            //<editor-fold defaultstate="collapsed" desc="rawsms">
            RawSMS val = (RawSMS) value;

            if (val.isSubmitError()) {
                setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_error.png")));
            } else {

                int stat = val.getProcessStatus();

                switch (stat) {
                    case -1:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_notsubmit.png")));
                        break;
                    case 0:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_unprocessed.png")));
                        break;
                    case 1:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_expired.png")));
                        break;
                    case 2:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_error.png")));
                        break;
                    case 3:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_ok.png")));
                        break;
                    case 4:
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_notsubmit.png")));
                        break;
                    default:
                        setIcon(null);

                }

            }

            String sender = val.getSender() + " ";

            if (val.getEmployee() != null) {
                Employee emp = val.getEmployee();
                sender = emp.getShortName() != null ? emp.getShortName() + " " : "n/a";
            }

            String text
                    = "<html>"
                    + i + ". "
                    + "<b>"
                    + sender
                    + "</b>"
                    + "<font color=red >"
                    + dispDateFormatter.format(val.getReceivedDate())
                    + "</font>"
                    + "<br/>"
                    + "Message: "
                    + (val.getMessage().length() > 60 ? (val.getMessage().substring(0, 60).concat("...")) : val.getMessage())
                    + "</html>";
            setText(text);
//</editor-fold>
        } else if (value instanceof QueuedResponse) {
            //<editor-fold defaultstate="collapsed" desc="queuedresponse">
            QueuedResponse val = (QueuedResponse) value;
            String text
                    = "<html>"
                    + i + ". "
                    + "<b>"
                    + "<font color=green>"
                    + dispDateFormatter.format(val.getQueuedDate())
                    + "</font>"
                    + "</b>"
                    + " | "
                    + "<font color=orange size=3>"
                    + " Status: " + val.getStatusString()
                    + "</font>"
                    + "<br/>"
                    + "<font size=3>"
                    + "Message: " + val.getMessage()
                    + "</font>"
                    + "</html>";

            setText(text);

            //</editor-fold>
        } else if (value instanceof SubmissionRaw) {
            //<editor-fold defaultstate="collapsed" desc="submission">
            SubmissionRaw val = (SubmissionRaw) value;
            Date rd = val.getReceivedDate();
            String text
                    = "<html>"
                    + i + ". "
                    + " "
                    + "<font color=green>"
                    +" "
                    + val.getDraw()
                    + ".t"
                    + val.getSubmissionId()
                    + " = "
                    + val.getTotalValue()
                    + "</font>"
                    + "</b> &nbsp"
                    + "<font color=red>"
                    + dispDateFormatter.format(rd)
                    + "</font>"
                    + "</html>";
            setText(text);

            //</editor-fold>
        } else {
            setText(new StringBuilder(" ").append(i).append(". ").append(value.toString()).toString());
        }

        return this;//To change body of generated methods, choose Tools | Templates.

    }
}
