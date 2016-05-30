package com.jps.l2app.server.ui;

import com.jps.l2app.utils.ListCellRendererImp;
import com.jps.l2app.entities.Administrator;
import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.EmployeeTotals;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.entities.services.AdministratorService;
import com.jps.l2app.entities.services.EmployeeService;
import com.jps.l2app.entities.services.EmployeeTotalsService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.entities.services.QueuedResponseService;
import com.jps.l2app.entities.services.RawSMSService;
import static com.jps.l2app.entities.services.RawSMSService.extractEssentialMobileNum;
import com.jps.l2app.entities.services.SubmissionRawService;
import com.jps.l2app.entities.services.SubmissionService;
import com.jps.l2app.main.Main;
import com.jps.l2app.utils.Utils;
import com.jps.l2app.main.Setup;
import com.jps.l2app.reports.EmployeeSummary76Source;
import com.jps.l2app.reports.EmployeeSummaryShortSource;
import com.jps.l2app.reports.MainReportSource;
import com.jps.l2app.reports.MainReportSource1;
import com.jps.l2app.utils.MyStyle;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
import com.jps.l2app.utils.UIUtilities;
import com.jps.l2app.utils.FormatUtils;
import java.util.Collections;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public final class MainFrame extends javax.swing.JFrame {

    private static MainFrame instance;
    private final SimpleDateFormat dispDateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private SwingWorker searchProcessor;

    private List<TableData> soldOut;

    public MainFrame() {
        initComponents();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateEmpLists();
                search();
            }
        });

        if (!Setup.master_mode) {
            soldOutBtn.getParent().remove(soldOutBtn);
            printForwardBtn.getParent().remove(printForwardBtn);
        }

    }

    private List<EmployeeTotals> employeeTotals;

    public void updateSummaryTbl() throws ParseException {

        employeeTotals = null;

        //<editor-fold defaultstate="collapsed" desc="prep">
        String draw = summaryTabDrawCombo.getSelectedItem().toString();
        summaryTotalTbl.setText("= 0");

        String emp = summaryTabFilterF.getText().trim();

        Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
        Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

        //</editor-fold>
        
        employeeTotals = EmployeeTotalsService.getInstance().queryEmpTotals(draw, emp, start, end);

        final DefaultTableModel m = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public int getRowCount() {
                if (employeeTotals == null) {
                    return 0;
                }
                return employeeTotals.size();
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {

                return super.getColumnClass(columnIndex); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getColumnCount() {
                return 12;
            }

            @Override
            public String getColumnName(int column) {

                switch (column) {
                    case 0:
                        return "";
                    case 1:
                        return "Employee";
                    case 2:
                        return "Date";
                    case 3:
                        return "Draw";
                    case 4:
                        return "L2";
                    case 5:
                        return "S3";
                    case 6:
                        return "L3";
                    case 7:
                        return "P3";
                    case 8:
                        return "4D";
                    case 9:
                        return "Gross";
                    case 10:
                        return "Comm";
                    case 11:
                        return "Net";

                }

                return "";
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                EmployeeTotals empt = employeeTotals.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return (rowIndex + 1) + ".";
                    case 1:
                        return empt.getEmployee().getShortName();
                    case 2:
                        return dispDateFormatter.format(empt.getRaffleDate());
                    case 3:
                        return empt.getDraw();
                    case 4:
                        return empt.getL2Total();
                    case 5:
                        return empt.getS3Total();
                    case 6:
                        return empt.getL3Total();
                    case 7:
                        return empt.getP3Total() + empt.getP42Total() + empt.getP45Total() + empt.getP49Total() + empt.getP55Total();
                    case 8:
                        return empt.getD4Total();
                    case 9:
                        return empt.getGross();
                    case 10:
                        return FormatUtils.currencyFormat(empt.getComm());
                    case 11:
                        return FormatUtils.currencyFormat(empt.getGross() - empt.getComm());

                }

                return "";
            }
        };

        double gross = 0d;
        double comm = 0d;

        for (EmployeeTotals e : employeeTotals) {
            gross += e.getGross();
            comm += e.getComm();
        }

        summaryTotalTbl.setText("Gross=" + FormatUtils.currencyFormat(gross) + "    Comm=" + FormatUtils.currencyFormat(comm) + "   Net=" + FormatUtils.currencyFormat((gross - comm)));

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                summaryTbl.setModel(m);
                //<editor-fold defaultstate="collapsed" desc="columnn settings">

                summaryTbl.getColumnModel().getColumn(0).setMinWidth(50);
                summaryTbl.getColumnModel().getColumn(0).setPreferredWidth(50);
                summaryTbl.getColumnModel().getColumn(0).setMaxWidth(50);

                summaryTbl.getColumnModel().getColumn(2).setMinWidth(130);
                summaryTbl.getColumnModel().getColumn(2).setPreferredWidth(130);
                summaryTbl.getColumnModel().getColumn(2).setMaxWidth(80);

                if (summaryTbl.getColumnModel().getColumnCount() > 4) {

                    for (int i = 3; i < summaryTbl.getColumnCount() - 1; i++) {
                        summaryTbl.getColumnModel().getColumn(i).setMinWidth(95);
                        summaryTbl.getColumnModel().getColumn(i).setPreferredWidth(95);
                        summaryTbl.getColumnModel().getColumn(i).setMaxWidth(95);
                    }

                }
                //</editor-fold>
            }
        });

    }

    public void updateResultTbl() throws ParseException {
        totalLbl.setText("= 0");
        String draw = drawCombo.getSelectedItem().toString();
        String raffle = raffleCombo.getSelectedItem().toString();
        String raffleDigits = raffleDigitSearch.getText().trim();

        //raffleDigitSearch.setText("");
        Employee e = (Employee) employeeCombo.getSelectedItem();

        Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
        Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

        List<Submission> submissionBuff = SubmissionService.getInstance().querySubmissions(draw, raffle, e, start, end, raffleDigits);
        if (submissionBuff.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel();
            resultTable.setModel(model);
            return;
        }

        final List<TableData> tableData = getTableData(submissionBuff);

        double gross = 0d;
        double totalRet = 0d;
        soldOut = new ArrayList();
        Settings s = SettingsService.getInstance().getSettingsObject();
        for (TableData d : tableData) {

            double sv = getSoldoutValue(d.getRaffle(), s);
            if (sv > 0 && d.getTotal() >= sv) {
                soldOut.add(d);
            }

            gross += d.getTotal();
            totalRet += d.getRetained();
        }

        String g = FormatUtils.currencyFormat(gross);
        String r = FormatUtils.currencyFormat(totalRet);
        double fw = gross - totalRet;
        String f = FormatUtils.currencyFormat(fw);

        if (Setup.master_mode) {
            totalLbl.setText("Gross=" + g + " Ret=" + r + " Fw=" + f);
        } else {
            totalLbl.setText("Gross=" + g);
        }

        resultTableModel = new DefaultTableModel() {

            @Override
            public int getColumnCount() {
                if (Setup.master_mode) {
                    return 8;
                }
                return 6;
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "#";
                    case 1:
                        return "Draw";
                    case 2:
                        return "RF";
                    case 3:
                        return "#";
                    case 4:
                        return "Details";
                    case 5:
                        return "Total";
                    case 6:
                        return "RET";
                    case 7:
                        return "FW";
                }
                return "";
            }

            @Override
            public int getRowCount() {
                return tableData.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                TableData r = tableData.get(row);
                switch (column) {
                    case 0:
                        return (row + 1) + ".";
                    case 1:
                        return r.getDraw();
                    case 2:
                        return r.getRaffle();
                    case 3:
                        return r.getRaffleNum();
                    case 4:
                        return r.getDetailString();
                    case 5:
                        return r.getTotal() + "";
                    case 6:
                        return r.getRetained();
                    case 7:
                        return r.getForwarded();

                }
                return "";
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                resultTable.setModel(resultTableModel);

            }
        };

        SwingUtilities.invokeLater(runnable);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                //<editor-fold defaultstate="collapsed" desc="columnn settings">
                resultTable.getColumnModel().getColumn(0).setMinWidth(50);
                resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
                resultTable.getColumnModel().getColumn(0).setMaxWidth(50);

                resultTable.getColumnModel().getColumn(1).setMinWidth(50);
                resultTable.getColumnModel().getColumn(1).setPreferredWidth(50);
                resultTable.getColumnModel().getColumn(1).setMaxWidth(50);

                resultTable.getColumnModel().getColumn(2).setMinWidth(50);
                resultTable.getColumnModel().getColumn(2).setPreferredWidth(50);
                resultTable.getColumnModel().getColumn(2).setMaxWidth(50);

                resultTable.getColumnModel().getColumn(3).setMinWidth(100);
                resultTable.getColumnModel().getColumn(3).setPreferredWidth(100);
                resultTable.getColumnModel().getColumn(3).setMaxWidth(100);

                resultTable.getColumnModel().getColumn(4).setMinWidth(130);
                resultTable.getColumnModel().getColumn(4).setPreferredWidth((int) (resultTable.getWidth() * 0.7));

                //</editor-fold>
            }
        });

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (!soldOut.isEmpty()) {
                    soldOutBtn.setText("SoldOut:" + soldOut.size());
                    UIUtilities.addWarningMessage("There are " + soldOut.size() + " soldout items!");
                }
            }
        });
    }

    private double getSoldoutValue(String d, Settings s) {

        double sold = 0d;
        switch (d.toUpperCase()) {
            //<editor-fold defaultstate="collapsed" desc="soldoutvalue">
            case "L2":
                sold = s.getsL2();
                break;
            case "L3":
                sold = s.getsL3();
                break;
            case "S3":
                sold = s.getsS3();
                break;
            case "S2":
                sold = s.getsS2();
                break;
            case "S3R":
                sold = s.getsS3r();
                break;
            case "P42":
            case "P45":
            case "P49":
            case "P55":
                sold = s.getSp3();
                break;
            case "4D":
                sold = s.getS4d();
                break;
            //</editor-fold>
        }
        return sold;
    }

    private List<ForwardEntriesDiag.ForwardEntriesDiagData> getForwardingData() {

        List<ForwardEntriesDiag.ForwardEntriesDiagData> data = new ArrayList();

        for (int x = 0; x < resultTableModel.getRowCount(); x++) {
            String raf = resultTableModel.getValueAt(x, 2).toString();
            String num = resultTableModel.getValueAt(x, 3).toString();
            double amt = Double.parseDouble(resultTableModel.getValueAt(x, 5).toString());
            if (amt > 0) {
                ForwardEntriesDiag.ForwardEntriesDiagData d = new ForwardEntriesDiag.ForwardEntriesDiagData();
                d.setAmount(amt);
                d.setRaffle(raf);
                d.setRaffleNumber(num);
                data.add(d);
            }
        }
        return data;
    }

    private String forward() throws NumberFormatException {

        if (resultTableModel == null) {
            return "";
        }

        StringBuilder b = new StringBuilder();

        b.append("POGIBOYS.DIGOS\n");
        b.append(drawCombo.getSelectedItem().toString());
        b.append("_");
        b.append(raffleCombo.getSelectedItem().toString());
        b.append("\n");

        for (int x = 0; x < resultTableModel.getRowCount(); x++) {
            String r = resultTableModel.getValueAt(x, 3).toString();
            String f = resultTableModel.getValueAt(x, 7).toString();
            if (Double.parseDouble(f) > 0) {
                b.append(r).append("=").append(f).append("\t").append(f).append("\n");
            }
        }

        return b.toString();

    }

    DefaultTableModel resultTableModel;

    public void updateSubmissionRaw() throws ParseException {

        Employee e = (Employee) submissionTabEmpList.getSelectedValue();

        if (e == null) {
            submissionTabSubList.setModel(new DefaultListModel());
            return;
        }

        List<SubmissionRaw> submissionListBuffer;

        String key = submissionsTabSearchF.getText();
        String draw = submissionTabDrawCombo.getSelectedItem().toString();

        Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
        Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

        submissionListBuffer = SubmissionService.getInstance().querySubmissionsRaw(draw, e, start, end, key);
        submissionTabResults.setText(submissionListBuffer.size() + "");

        final DefaultListModel<SubmissionRaw> sm = new DefaultListModel<>();
        for (SubmissionRaw sr : submissionListBuffer) {
            sm.addElement(sr);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                submissionTabSubList.setModel(sm);
            }
        });

    }

    public void updateInbox() throws ParseException {

        Employee e = (Employee) inboxTabEmpList.getSelectedValue();

        if (e == null) {
            inboxTabMessList.setModel(new DefaultListModel());
            return;
        }

        Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
        Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

        String key = inboxTabSearchF.getText();

        List<RawSMS> raw = RawSMSService.getInstance().getSMSFromEmp(e, start, end, key);

        final DefaultListModel<RawSMS> model = new DefaultListModel<>();
        for (RawSMS rawSMS : raw) {
            model.addElement(rawSMS);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                inboxTabMessList.setModel(model);
                try {
                    int index = inboxTabMessList.getModel().getSize() - 1;
                    inboxTabMessList.scrollRectToVisible(inboxTabMessList.getCellBounds(index, index));
                } catch (Exception e) {
                }
            }
        });

    }

    public void updateSentItems() throws ParseException {

        Employee e = (Employee) sentTabEmpList.getSelectedValue();
        if (e == null) {
            sentTabSentList.setModel(new DefaultListModel());
            return;
        }

        Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
        Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

        String key = sentTabSearchF.getText();

        List<QueuedResponse> qrs = QueuedResponseService.getInstance().getSentToEmp(e, start, end, key);

        final DefaultListModel<QueuedResponse> model = new DefaultListModel<>();
        for (QueuedResponse queuedResponse : qrs) {
            model.addElement(queuedResponse);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                sentTabSentList.setModel(model);

            }
        });

    }

    private void updateEmpLists() {

        employeeCombo.removeAllItems();

        Employee a = new Employee();
        a.setShortName("All");

        employeeCombo.addItem(a);

        DefaultListModel<Employee> empModel = new DefaultListModel<>();

        Employee unreg = new Employee();
        unreg.setShortName("Unregistered");

        empModel.addElement(a);
        empModel.addElement(unreg);

        DefaultListModel<Employee> submissionRawModel = new DefaultListModel<>();

        List<Employee> allEmployees = EmployeeService.getInstance().getAllowedEmployees();
        for (Employee employee : allEmployees) {
            employeeCombo.addItem(employee);
            empModel.addElement(employee);
            submissionRawModel.addElement(employee);
        }

        submissionTabEmpList.setModel(submissionRawModel);
        inboxTabEmpList.setModel(empModel);
        sentTabEmpList.setModel(empModel);

    }

    private void refreshEmpDiagEmpList() {
        List<Employee> allEmployees = EmployeeService.getInstance().getAllEmployees();
        DefaultListModel<Employee> m = new DefaultListModel<>();
        for (Employee employee : allEmployees) {
            m.addElement(employee);
        }
        empDiagEmpList.setModel(m);
    }

    public void submissionReceived() {
        search();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        employeeDiag = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        empDiagEmpList = new javax.swing.JList<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        shortNameF = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        prontonTypeCheckbox = new javax.swing.JCheckBox();
        fullNameF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        mobileF = new javax.swing.JTextField();
        commissionPanel = new javax.swing.JPanel();
        p49 = new javax.swing.JSpinner();
        jLabel22 = new javax.swing.JLabel();
        p55 = new javax.swing.JSpinner();
        jLabel47 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        pl3 = new javax.swing.JSpinner();
        ps2 = new javax.swing.JSpinner();
        ps3r = new javax.swing.JSpinner();
        p4d = new javax.swing.JSpinner();
        jLabel50 = new javax.swing.JLabel();
        pl2 = new javax.swing.JSpinner();
        ps3 = new javax.swing.JSpinner();
        jLabel48 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        p42 = new javax.swing.JSpinner();
        p45 = new javax.swing.JSpinner();
        winPanel = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        wl3 = new javax.swing.JSpinner();
        ws2 = new javax.swing.JSpinner();
        ws3r = new javax.swing.JSpinner();
        wl2 = new javax.swing.JSpinner();
        ws3 = new javax.swing.JSpinner();
        jLabel56 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        wp3 = new javax.swing.JSpinner();
        w4d = new javax.swing.JSpinner();
        jButton13 = new javax.swing.JButton();
        Delete = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        sendSMSDiag = new javax.swing.JDialog();
        jButton7 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        sendToF = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        messageF = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        characters = new javax.swing.JLabel();
        nonConcatCheck = new javax.swing.JCheckBox();
        sendToAllCheck = new javax.swing.JCheckBox();
        administratorDiag = new javax.swing.JDialog();
        oldPass = new javax.swing.JPasswordField();
        jLabel16 = new javax.swing.JLabel();
        newPass = new javax.swing.JPasswordField();
        jLabel17 = new javax.swing.JLabel();
        confPass = new javax.swing.JPasswordField();
        jLabel18 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        resultDiag = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        resultDiagRaffF = new javax.swing.JTextField();
        resultDiagTotalF = new javax.swing.JTextField();
        resultDiagRaffNF = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        resultDiagDrawF = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        resultDiagDetailsF = new javax.swing.JTextPane();
        jLabel23 = new javax.swing.JLabel();
        jPopupMenu4 = new javax.swing.JPopupMenu();
        empTotalPrint = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        messageDialog = new javax.swing.JDialog();
        jScrollPane13 = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        mobileF1 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        newForwardBtn = new javax.swing.JButton();
        groupLbl = new javax.swing.JLabel();
        groupCombo = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        startDateSpin = new javax.swing.JSpinner();
        endDateSpin = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        tabPane = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        summaryTbl = new javax.swing.JTable();
        summaryTotalTbl = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        summaryTabFilterF = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        summaryTabDrawCombo = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        totalLbl = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        drawCombo = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        raffleCombo = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        employeeCombo = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        raffleDigitSearch = new javax.swing.JTextField();
        resultSearchBtn = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        soldOutBtn = new javax.swing.JButton();
        printForwardBtn = new javax.swing.JButton();
        orderByAmtCheck = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        submissionTabSubList = new javax.swing.JList();
        jScrollPane8 = new javax.swing.JScrollPane();
        submissionTabEmpList = new javax.swing.JList();
        jLabel30 = new javax.swing.JLabel();
        submissionsTabFilterF = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        submissionsTabSearchF = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        submissionTabDrawCombo = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        submissionTabResults = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        inboxTabMessList = new javax.swing.JList();
        jScrollPane9 = new javax.swing.JScrollPane();
        inboxTabEmpList = new javax.swing.JList();
        jLabel29 = new javax.swing.JLabel();
        inboxTabFilterF = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        inboxTabSearchF = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        inboxTab_smsDetailTextArea = new javax.swing.JTextArea();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sentTabSentList = new javax.swing.JList();
        jScrollPane10 = new javax.swing.JScrollPane();
        sentTabEmpList = new javax.swing.JList();
        jLabel31 = new javax.swing.JLabel();
        sentTabFilterF = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        sentTabSearchF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        autoReplyCheckBox = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        employeeDiag.setTitle("Employees");

        jLabel2.setText("Stations");

        empDiagEmpList.setCellRenderer(new ListCellRendererImp());
        empDiagEmpList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                empDiagEmpListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(empDiagEmpList);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Area:");

        jLabel8.setText("Mobile:");

        prontonTypeCheckbox.setText("<html><b>Pronton Type</b><br>Only prontons installed with TPR Pronton System</html>");

        jLabel4.setText("Name:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prontonTypeCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shortNameF, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mobileF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fullNameF, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel10Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fullNameF, mobileF, shortNameF});

        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(prontonTypeCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(shortNameF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fullNameF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mobileF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(14, 14, 14))
        );

        jTabbedPane1.addTab("Station Information", jPanel10);

        jLabel22.setText("L2");

        jLabel47.setText("L3");

        jLabel45.setText("S3");

        jLabel50.setText("P49");

        jLabel48.setText("P42");

        jLabel52.setText("4D");

        jLabel46.setText("S3R");

        jLabel49.setText("P45");

        jLabel51.setText("P55");

        jLabel44.setText("S2");

        javax.swing.GroupLayout commissionPanelLayout = new javax.swing.GroupLayout(commissionPanel);
        commissionPanel.setLayout(commissionPanelLayout);
        commissionPanelLayout.setHorizontalGroup(
            commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commissionPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(commissionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pl3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p55, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(commissionPanelLayout.createSequentialGroup()
                        .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel47))
                        .addGap(12, 12, 12)
                        .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(commissionPanelLayout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p42, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(commissionPanelLayout.createSequentialGroup()
                                .addComponent(pl2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ps2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(commissionPanelLayout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p4d, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(commissionPanelLayout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ps3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ps3r, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(commissionPanelLayout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p45, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p49, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        commissionPanelLayout.setVerticalGroup(
            commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commissionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(pl2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(ps2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(ps3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46)
                    .addComponent(ps3r, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pl3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(p42, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(p45, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(p49, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(p4d, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel52))
                    .addGroup(commissionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(p55, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Commission (%)", commissionPanel);

        jLabel43.setText("L2");

        jLabel53.setText("L3");

        jLabel54.setText("S3");

        jLabel56.setText("P42");

        jLabel58.setText("S3R");

        jLabel59.setText("4D");

        jLabel61.setText("S2");

        javax.swing.GroupLayout winPanelLayout = new javax.swing.GroupLayout(winPanel);
        winPanel.setLayout(winPanelLayout);
        winPanelLayout.setHorizontalGroup(
            winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(winPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(winPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(wl3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(winPanelLayout.createSequentialGroup()
                        .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(jLabel53))
                        .addGap(12, 12, 12)
                        .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(winPanelLayout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(wp3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(winPanelLayout.createSequentialGroup()
                                .addComponent(wl2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ws2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(winPanelLayout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ws3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ws3r, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(winPanelLayout.createSequentialGroup()
                                .addComponent(jLabel59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(w4d, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        winPanelLayout.setVerticalGroup(
            winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(winPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(wl2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61)
                    .addComponent(ws2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(ws3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58)
                    .addComponent(ws3r, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(winPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wl3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(wp3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56)
                    .addComponent(w4d, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Wins (Per Peso)", winPanel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton13.setMnemonic('c');
        jButton13.setText("Create New");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        Delete.setMnemonic('d');
        Delete.setText("Delete");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        jButton4.setMnemonic('s');
        jButton4.setText("Save");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout employeeDiagLayout = new javax.swing.GroupLayout(employeeDiag.getContentPane());
        employeeDiag.getContentPane().setLayout(employeeDiagLayout);
        employeeDiagLayout.setHorizontalGroup(
            employeeDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, employeeDiagLayout.createSequentialGroup()
                .addGroup(employeeDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(employeeDiagLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(employeeDiagLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        employeeDiagLayout.setVerticalGroup(
            employeeDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(employeeDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(employeeDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(Delete)
                    .addComponent(jButton13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sendSMSDiag.setTitle("Send SMS");

        jButton7.setMnemonic('s');
        jButton7.setText("Send");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel10.setText("Send to:");

        messageF.setColumns(20);
        messageF.setRows(5);
        messageF.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                messageFCaretUpdate(evt);
            }
        });
        jScrollPane4.setViewportView(messageF);

        jLabel12.setText("Message:");

        characters.setText("0");

        nonConcatCheck.setSelected(true);
        nonConcatCheck.setText("Send as multiple SMS");

        sendToAllCheck.setText("Send to all");
        sendToAllCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sendToAllCheckItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout sendSMSDiagLayout = new javax.swing.GroupLayout(sendSMSDiag.getContentPane());
        sendSMSDiag.getContentPane().setLayout(sendSMSDiagLayout);
        sendSMSDiagLayout.setHorizontalGroup(
            sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sendSMSDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sendSMSDiagLayout.createSequentialGroup()
                        .addComponent(characters, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7))
                    .addComponent(jScrollPane4)
                    .addGroup(sendSMSDiagLayout.createSequentialGroup()
                        .addComponent(sendToF, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nonConcatCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendToAllCheck)
                        .addGap(0, 48, Short.MAX_VALUE)))
                .addContainerGap())
        );
        sendSMSDiagLayout.setVerticalGroup(
            sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sendSMSDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(sendToF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nonConcatCheck)
                    .addComponent(sendToAllCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sendSMSDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addComponent(characters))
                .addContainerGap())
        );

        administratorDiag.setTitle("Admin Password");

        jLabel16.setText("Old Password:");

        jLabel17.setText("New Password:");

        jLabel18.setText("Confirm Password:");

        jButton8.setText("Change Password");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout administratorDiagLayout = new javax.swing.GroupLayout(administratorDiag.getContentPane());
        administratorDiag.getContentPane().setLayout(administratorDiagLayout);
        administratorDiagLayout.setHorizontalGroup(
            administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(administratorDiagLayout.createSequentialGroup()
                .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(administratorDiagLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(26, 26, 26)
                        .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newPass)
                            .addComponent(confPass)
                            .addComponent(oldPass)))
                    .addGroup(administratorDiagLayout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(jButton8)))
                .addGap(52, 52, 52))
        );
        administratorDiagLayout.setVerticalGroup(
            administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(administratorDiagLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(administratorDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addComponent(jButton8)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jMenuItem5.setText("Refresh");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem5);

        jMenuItem17.setText("Show");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem17);

        jMenuItem18.setText("Resend");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem18);

        jMenuItem6.setText("Refresh");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem6);

        jMenuItem12.setText("Reply");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem12);

        jMenuItem7.setText("Refresh");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem7);

        jMenuItem13.setText("Print Selected");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem13);

        jMenuItem15.setText("Remove");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem15);

        resultDiag.setTitle("Results");
        resultDiag.setResizable(false);

        jLabel25.setText("Total:");

        jLabel26.setText("Raffle Number:");

        jLabel27.setText("Result Details");

        jLabel21.setText("Draw:");

        jLabel24.setText("Details:");

        resultDiagRaffF.setEditable(false);

        resultDiagTotalF.setEditable(false);

        resultDiagRaffNF.setEditable(false);

        jButton9.setText("Close");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        resultDiagDrawF.setEditable(false);

        resultDiagDetailsF.setEditable(false);
        jScrollPane11.setViewportView(resultDiagDetailsF);

        jLabel23.setText("Raffle:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel21)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resultDiagDrawF)
                            .addComponent(resultDiagRaffF)
                            .addComponent(resultDiagRaffNF)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(60, 60, 60)
                        .addComponent(resultDiagTotalF))
                    .addComponent(jLabel24)
                    .addComponent(jLabel27)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(resultDiagDrawF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(resultDiagRaffF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(resultDiagRaffNF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(resultDiagTotalF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addContainerGap())
        );

        javax.swing.GroupLayout resultDiagLayout = new javax.swing.GroupLayout(resultDiag.getContentPane());
        resultDiag.getContentPane().setLayout(resultDiagLayout);
        resultDiagLayout.setHorizontalGroup(
            resultDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        resultDiagLayout.setVerticalGroup(
            resultDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        empTotalPrint.setText("Print Summary");
        empTotalPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empTotalPrintActionPerformed(evt);
            }
        });
        jPopupMenu4.add(empTotalPrint);

        jMenuItem16.setText("Mark as Collected");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jPopupMenu4.add(jMenuItem16);

        jCheckBoxMenuItem2.setText("AutoPrint New Submissions");
        jCheckBoxMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem2ActionPerformed(evt);
            }
        });

        messageDialog.setTitle("Message");

        messageTextArea.setColumns(20);
        messageTextArea.setRows(5);
        jScrollPane13.setViewportView(messageTextArea);

        javax.swing.GroupLayout messageDialogLayout = new javax.swing.GroupLayout(messageDialog.getContentPane());
        messageDialog.getContentPane().setLayout(messageDialogLayout);
        messageDialogLayout.setHorizontalGroup(
            messageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messageDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        messageDialogLayout.setVerticalGroup(
            messageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messageDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton6.setText("Open File");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton11.setText("Cancel File");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        mobileF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mobileF1ActionPerformed(evt);
            }
        });

        jLabel55.setText("Mobile2:");

        newForwardBtn.setText("Forward (*New)");
        newForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newForwardBtnActionPerformed(evt);
            }
        });

        groupLbl.setText("Group:");

        groupCombo.setEditable(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Main");

        jLabel11.setText("Date:");

        startDateSpin.setModel(new javax.swing.SpinnerDateModel());
        startDateSpin.setToolTipText("00:00:00 hrs");
        startDateSpin.setDoubleBuffered(true);
        startDateSpin.setEditor(new javax.swing.JSpinner.DateEditor(startDateSpin, "dd/MMMM/yyyy"));
        startDateSpin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                startDateSpinStateChanged(evt);
            }
        });

        endDateSpin.setModel(new javax.swing.SpinnerDateModel());
        endDateSpin.setToolTipText("23:59:59 hrs");
        endDateSpin.setEditor(new javax.swing.JSpinner.DateEditor(endDateSpin, "dd/MMMM/yyyy"));
        endDateSpin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                endDateSpinStateChanged(evt);
            }
        });

        jLabel9.setText("to");

        summaryTbl.setAutoCreateRowSorter(true);
        summaryTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        summaryTbl.setToolTipText("This is the result table");
        summaryTbl.setComponentPopupMenu(jPopupMenu4);
        summaryTbl.setRowHeight(24);
        summaryTbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(summaryTbl);

        summaryTotalTbl.setBackground(new java.awt.Color(0, 0, 0));
        summaryTotalTbl.setForeground(new java.awt.Color(255, 255, 255));
        summaryTotalTbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        summaryTotalTbl.setText("= 0");
        summaryTotalTbl.setOpaque(true);

        jLabel28.setText("Filter Employee:");

        summaryTabFilterF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summaryTabFilterFActionPerformed(evt);
            }
        });

        jLabel35.setText("Draw:");

        summaryTabDrawCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "D1", "D2", "D3" }));
        summaryTabDrawCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summaryTabDrawComboActionPerformed(evt);
            }
        });

        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton16.setText("Send Summary");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1125, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryTabDrawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryTabFilterF, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton16)
                .addContainerGap())
            .addComponent(summaryTotalTbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(summaryTabFilterF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(summaryTabDrawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jButton3)
                    .addComponent(jButton16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryTotalTbl, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabPane.addTab("<html>Summary</html>", jPanel4);

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        resultTable.setToolTipText("This is the result table");
        resultTable.setRowHeight(24);
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(resultTable);

        totalLbl.setBackground(new java.awt.Color(0, 0, 0));
        totalLbl.setForeground(new java.awt.Color(255, 255, 255));
        totalLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLbl.setText("= 0");
        totalLbl.setOpaque(true);

        jLabel5.setText("Draw:");

        drawCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "D1", "D2", "D3" }));
        drawCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawComboActionPerformed(evt);
            }
        });

        jLabel14.setText("Raffle: ");

        raffleCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "L2", "L3", "S3", "S2", "S3R", "P3", "4D" }));
        raffleCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raffleComboActionPerformed(evt);
            }
        });

        jLabel13.setText("Employee: ");

        employeeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeComboActionPerformed(evt);
            }
        });

        jLabel15.setText(" Digits:");

        raffleDigitSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raffleDigitSearchActionPerformed(evt);
            }
        });

        resultSearchBtn.setMnemonic('s');
        resultSearchBtn.setText("Search");
        resultSearchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultSearchBtnActionPerformed(evt);
            }
        });

        jButton12.setMnemonic('p');
        jButton12.setText("Print");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        soldOutBtn.setMnemonic('s');
        soldOutBtn.setText("Soldout");
        soldOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soldOutBtnActionPerformed(evt);
            }
        });

        printForwardBtn.setText("Print Forward");
        printForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printForwardBtnActionPerformed(evt);
            }
        });

        orderByAmtCheck.setSelected(true);
        orderByAmtCheck.setText("Order by amount");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(totalLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(raffleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(employeeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(raffleDigitSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orderByAmtCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultSearchBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printForwardBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(soldOutBtn)
                        .addGap(124, 124, 124)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultSearchBtn)
                    .addComponent(employeeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(raffleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(drawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel15)
                    .addComponent(raffleDigitSearch)
                    .addComponent(jButton12)
                    .addComponent(soldOutBtn)
                    .addComponent(printForwardBtn)
                    .addComponent(orderByAmtCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabPane.addTab("Results", jPanel3);

        submissionTabSubList.setCellRenderer(new ListCellRendererImp());
        submissionTabSubList.setComponentPopupMenu(jPopupMenu3);
        submissionTabSubList.setFixedCellHeight(30);
        submissionTabSubList.setSelectionBackground(new java.awt.Color(204, 204, 255));
        submissionTabSubList.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollPane6.setViewportView(submissionTabSubList);

        submissionTabEmpList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1. Smeagul", "2. Frodo", "3. Sam", "4. Merril", "5. Gandalf" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        submissionTabEmpList.setCellRenderer(new ListCellRendererImp());
        submissionTabEmpList.setFixedCellHeight(25);
        submissionTabEmpList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                submissionTabEmpListValueChanged(evt);
            }
        });
        jScrollPane8.setViewportView(submissionTabEmpList);

        jLabel30.setText("Filter:");

        submissionsTabFilterF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                submissionsTabFilterFKeyTyped(evt);
            }
        });

        jLabel32.setText("Search:");

        submissionsTabSearchF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                submissionsTabSearchFKeyTyped(evt);
            }
        });

        jLabel36.setText("Draw:");

        submissionTabDrawCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "D1", "D2", "D3" }));
        submissionTabDrawCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submissionTabDrawComboActionPerformed(evt);
            }
        });

        jLabel20.setText("Results:");

        submissionTabResults.setText("0");

        jButton14.setText("Print Selected");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submissionsTabFilterF))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submissionsTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submissionTabDrawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submissionTabResults, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton14)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(submissionTabDrawCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel36))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(submissionsTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel30)
                        .addComponent(submissionsTabFilterF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(submissionTabResults)
                            .addComponent(jButton14))
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );

        tabPane.addTab("Submissions", jPanel5);

        inboxTabMessList.setCellRenderer(new ListCellRendererImp());
        inboxTabMessList.setComponentPopupMenu(jPopupMenu2);
        inboxTabMessList.setSelectionBackground(new java.awt.Color(204, 204, 255));
        inboxTabMessList.setSelectionForeground(new java.awt.Color(0, 0, 0));
        inboxTabMessList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                inboxTabMessListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(inboxTabMessList);

        inboxTabEmpList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1. Smeagul", "2. Frodo", "3. Sam", "4. Merril", "5. Gandalf" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        inboxTabEmpList.setCellRenderer(new ListCellRendererImp());
        inboxTabEmpList.setFixedCellHeight(25);
        inboxTabEmpList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                inboxTabEmpListValueChanged(evt);
            }
        });
        jScrollPane9.setViewportView(inboxTabEmpList);

        jLabel29.setText("Filter:");

        inboxTabFilterF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inboxTabFilterFKeyTyped(evt);
            }
        });

        jLabel33.setText("Search:");

        inboxTabSearchF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inboxTabSearchFKeyTyped(evt);
            }
        });

        inboxTab_smsDetailTextArea.setEditable(false);
        inboxTab_smsDetailTextArea.setColumns(20);
        inboxTab_smsDetailTextArea.setRows(5);
        jScrollPane12.setViewportView(inboxTab_smsDetailTextArea);

        jLabel38.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 11)); // NOI18N
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_notsubmit.png"))); // NOI18N
        jLabel38.setText("Unprocessed");

        jLabel39.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 11)); // NOI18N
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_expired.png"))); // NOI18N
        jLabel39.setText("Expired");

        jLabel40.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 11)); // NOI18N
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_ok.png"))); // NOI18N
        jLabel40.setText("Ok");

        jLabel41.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 11)); // NOI18N
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_error.png"))); // NOI18N
        jLabel41.setText("Error");

        jLabel42.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 11)); // NOI18N
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/mini_notsubmit.png"))); // NOI18N
        jLabel42.setText("Not a submit or Incomplete");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inboxTabFilterF)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inboxTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel39)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel41)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel42)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane12))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel33)
                        .addComponent(inboxTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38)
                        .addComponent(jLabel39)
                        .addComponent(jLabel40)
                        .addComponent(jLabel41)
                        .addComponent(jLabel42))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29)
                        .addComponent(inboxTabFilterF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tabPane.addTab("Inbox", jPanel6);

        sentTabSentList.setCellRenderer(new ListCellRendererImp());
        sentTabSentList.setComponentPopupMenu(jPopupMenu1);
        sentTabSentList.setSelectionBackground(new java.awt.Color(204, 204, 255));
        sentTabSentList.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(sentTabSentList);

        sentTabEmpList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1. Smeagul", "2. Frodo", "3. Sam", "4. Merril", "5. Gandalf" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        sentTabEmpList.setCellRenderer(new ListCellRendererImp());
        sentTabEmpList.setFixedCellHeight(25);
        sentTabEmpList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sentTabEmpListValueChanged(evt);
            }
        });
        jScrollPane10.setViewportView(sentTabEmpList);

        jLabel31.setText("Filter:");

        sentTabFilterF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sentTabFilterFKeyTyped(evt);
            }
        });

        jLabel34.setText("Search:");

        sentTabSearchF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentTabSearchFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sentTabFilterF)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 881, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sentTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel34)
                        .addComponent(sentTabSearchF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(sentTabFilterF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabPane.addTab("Sent Items", jPanel7);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/Numbers16.png"))); // NOI18N
        jLabel1.setText("Version: " + Setup.VERSION);

        jLabel3.setFont(new java.awt.Font("Lucida Console", 0, 10)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Powered by Dominoff IT Inc. Copyright 2015. All rights reserved.");

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));

        menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/stock_execute_16.png"))); // NOI18N
        menu.setMnemonic('s');
        menu.setText("Setup   ");
        menu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                menuFocusGained(evt);
            }
        });
        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuMouseClicked(evt);
            }
        });

        jMenuItem2.setMnemonic('g');
        jMenuItem2.setText("Bet Limits");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem2);

        jMenuItem8.setMnemonic('m');
        jMenuItem8.setText("Stations");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem8);

        jMenuItem3.setMnemonic('c');
        jMenuItem3.setText("Cutoff");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem3);

        jMenuItem1.setText("Admin Password");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem1);

        jMenuItem14.setText("Super Admin Password");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem14);
        menu.add(jSeparator4);

        autoReplyCheckBox.setForeground(new java.awt.Color(255, 0, 0));
        autoReplyCheckBox.setMnemonic('a');
        autoReplyCheckBox.setText("<html><u>A</u>uto Reply<br/><font size=2>ALWAYS check to allow the system to reply!</html>");
        autoReplyCheckBox.setActionCommand("<html>Auto Reply<br/><font size=1px>Check to allow the system to reply!</html>");
        autoReplyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoReplyCheckBoxActionPerformed(evt);
            }
        });
        menu.add(autoReplyCheckBox);
        menu.add(jSeparator2);

        jMenuItem10.setMnemonic('s');
        jMenuItem10.setText("Send Messages");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem10);

        jMenuItem4.setForeground(new java.awt.Color(204, 0, 0));
        jMenuItem4.setMnemonic('x');
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        menu.add(jMenuItem4);

        jMenuBar1.add(menu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startDateSpin, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endDateSpin, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tabPane))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endDateSpin, startDateSpin});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDateSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultSearchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultSearchBtnActionPerformed
        search();
    }//GEN-LAST:event_resultSearchBtnActionPerformed

    public void search() {
        if (searchProcessor != null && !searchProcessor.isDone()) {
            searchProcessor.cancel(true);
        }

        searchProcessor = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                searchProcedure();
                return null;
            }

            @Override
            protected void done() {
                searchProcessor = null;
                super.done();
            }

        };
        searchProcessor.execute();
    }

    public void searchProcedure() throws ParseException {

        switch (tabPane.getSelectedIndex()) {
            case 0:
                updateSummaryTbl();
                break;
            case 1:
                updateResultTbl();
                break;
            case 2:
                updateSubmissionRaw();
                break;
            case 3:
                updateInbox();
                break;
            case 4:
                updateSentItems();
                break;
        }

    }

    public static void setupUI() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                doSetupUI();
            }

        };
        java.awt.EventQueue.invokeLater(runnable);
    }

    public static void doSetupUI() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                @Override
                public UIDefaults getDefaults() {
                    UIDefaults ret = super.getDefaults();
                    ret.put("defaultFont", new Font(Setup.FONT_NAME, Font.PLAIN, Setup.fontSize)); // supersize me
                    return ret;
                }
            });
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Error in Setting-up UI.");
        }
    }

    public void createNewEmployee() {

        long employeeCount = EmployeeService.getInstance().getEmployeeCount();

        if (employeeCount > Setup.users - 1) {
            UIUtilities.addWarningMessage("Number of employees is at the limit. Please contact your provider.");
            return;
        }

        Employee e = new Employee();
        e.setStationArea(fullNameF.getText());
        e.setShortName(shortNameF.getText());
        e.setMobileNumber(mobileF.getText());
        //e.setPasskey(passKeyF.getValue().toString());

        boolean already = EmployeeService.getInstance().checkShortName(e.getShortName());
        if (already) {
            UIUtilities.addWarningMessage("Short name already taken. Please try something else.");
            return;
        }

        boolean already1 = EmployeeService.getInstance().checkMobileNumber(e.getMobileNumber());
        if (already1) {
            UIUtilities.addWarningMessage("Mobile Number already registered.");
            return;
        }

        EmployeeService.getInstance().insert(e);
        refreshEmpDiagEmpList();
        updateEmpLists();

    }

    private void employeeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeComboActionPerformed
        search();
    }//GEN-LAST:event_employeeComboActionPerformed

    private void empDiagEmpListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_empDiagEmpListValueChanged

        Employee e = empDiagEmpList.getSelectedValue();
        if (e == null) {

            shortNameF.setText("");
            fullNameF.setText("");
            mobileF.setText("");
            mobileF1.setText("");
            prontonTypeCheckbox.setSelected(false);

            p42.setValue(0d);
            p45.setValue(0d);
            p49.setValue(0d);
            p4d.setValue(0d);
            p55.setValue(0d);
            pl2.setValue(0d);
            pl3.setValue(0d);
            ps2.setValue(0d);
            ps3.setValue(0d);
            ps3r.setValue(0d);

            w4d.setValue(0d);
            wl2.setValue(0);
            wl3.setValue(0);
            wp3.setValue(0);
            ws2.setValue(0);
            ws3.setValue(0);
            ws3r.setValue(0);

            return;
        }

        shortNameF.setText(e.getShortName());
        fullNameF.setText(e.getStationArea());
        mobileF.setText(e.getMobileNumber());
        mobileF1.setText(e.getMobileNumber1());
        prontonTypeCheckbox.setSelected(e.isPronton());

        p42.setValue(e.getP42());
        p45.setValue(e.getP45());
        p49.setValue(e.getP49());
        p4d.setValue(e.getP4d());
        p55.setValue(e.getP55());
        pl2.setValue(e.getpL2());
        pl3.setValue(e.getpL3());
        ps2.setValue(e.getpS2());
        ps3.setValue(e.getpS3());
        ps3r.setValue(e.getpS3r());

        w4d.setValue(e.getW4d());
        wl2.setValue(e.getwL2());
        wl3.setValue(e.getwL3());
        wp3.setValue(e.getwP3());
        ws2.setValue(e.getwS2());
        ws3.setValue(e.getwS3());
        ws3r.setValue(e.getwS3r());

    }//GEN-LAST:event_empDiagEmpListValueChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (prontonTypeCheckbox.isSelected()) {
            boolean confirmation = UIUtilities.getConfirmation("You've set this station as pronton, which means that it is installed with the TRP Pronton System. Continue?");
            if (!confirmation) {
                return;
            }

        }

        Employee e = empDiagEmpList.getSelectedValue();
        if (e == null) {
            createNewEmployee();
            return;
        }

//        String groupName = groupCombo.getSelectedItem().toString();
//        StationGroup group = StationGroupService.getInstance().findByName(groupName);
//        if (group == null) {
//            group = new StationGroup();
//            group.setGroupName(groupName);
//            group = StationGroupService.getInstance().insert(group);
//        }
//
//        e.setStationGroup(group);
        e.setStationArea(fullNameF.getText());
        e.setShortName(shortNameF.getText());
        e.setMobileNumber(mobileF.getText());
        e.setMobileNumber1(mobileF1.getText());
        e.setPronton(prontonTypeCheckbox.isSelected());

        e.setP42(Double.parseDouble(p42.getValue().toString()));
        e.setP45(Double.parseDouble(p45.getValue().toString()));
        e.setP49(Double.parseDouble(p49.getValue().toString()));
        e.setP4d(Double.parseDouble(p4d.getValue().toString()));
        e.setP55(Double.parseDouble(p55.getValue().toString()));
        e.setpL2(Double.parseDouble(pl2.getValue().toString()));
        e.setpL3(Double.parseDouble(pl3.getValue().toString()));
        e.setpS2(Double.parseDouble(ps2.getValue().toString()));
        e.setpS3(Double.parseDouble(ps3.getValue().toString()));
        e.setpS3r(Double.parseDouble(ps3r.getValue().toString()));

        e.setW4d(Double.parseDouble(w4d.getValue().toString()));
        e.setwL2(Double.parseDouble(wl2.getValue().toString()));
        e.setwL3(Double.parseDouble(wl3.getValue().toString()));
        e.setwP3(Double.parseDouble(wp3.getValue().toString()));
        e.setwS2(Double.parseDouble(ws2.getValue().toString()));
        e.setwS3(Double.parseDouble(ws3.getValue().toString()));
        e.setwS3r(Double.parseDouble(ws3r.getValue().toString()));

//        e.setPasskey(passKeyF.getValue().toString());
        EmployeeService.getInstance().update(e);

        UIUtilities.addInfoMessage("Your changes were saved.");

        refreshEmpDiagEmpList();
        updateEmpLists();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        Employee e = empDiagEmpList.getSelectedValue();
        if (e == null) {
            return;
        }

        boolean confirmation = UIUtilities.getConfirmation("Are you sure?");
        if (!confirmation) {
            return;
        }

        try {
            EmployeeService.getInstance().remove(e);
            refreshEmpDiagEmpList();
            updateEmpLists();
        } catch (Exception ex) {
            UIUtilities.addWarningMessage("Could not remove this employee due to dependent records/references in data.");
        }
    }//GEN-LAST:event_DeleteActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        String message = messageF.getText().trim();
        if (message.isEmpty()) {
            UIUtilities.addWarningMessage("Please add a message. Empty not allowed.");
            return;
        }

        String mobile = sendToF.getText().trim();

        boolean sendToAll = sendToAllCheck.isSelected();

        if (mobile.isEmpty() && !sendToAll) {
            UIUtilities.addWarningMessage("Please add receiver's number.");
            return;
        }

        if (sendToAll) {
            List<Employee> allEmployees = EmployeeService.getInstance().getAllEmployees();
            for (Employee e : allEmployees) {
                processSMSSubmit(nonConcatCheck.isSelected(), message, RawSMSService.extractEssentialMobileNum(e.getMobileNumber()));
            }
        } else {
            processSMSSubmit(nonConcatCheck.isSelected(), message, RawSMSService.extractEssentialMobileNum(mobile));
        }

        UIUtilities.addInfoMessage("Message now pending to be sent.");

        messageF.setText("");
        sendToAllCheck.setSelected(false);
        //sendToF.setText("");
        //sendSMSDiag.setVisible(false);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void processSMSSubmit(boolean multipleSMS, String message, String mobile) {
        if (!multipleSMS) {
            sendAsSingle(message, mobile);
        } else {
            sendAsMulti(message, mobile);
        }
    }

    private void sendAsMulti(String message, String mobile) {

        List<String> str = new ArrayList();

        int boundary = 153;
        int length = message.length();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(message.charAt(i));
            if (b.length() == boundary) {
                str.add(b.toString());
                b = new StringBuilder();
            }
        }

        if (b.length() > 0) {
            str.add(b.toString());
        }

        int x = 0;
        for (String part : str) {
            x++;
            sendAsSingle(x + "/" + str.size() + "\n" + part, mobile);
        }

    }

    private void sendAsSingle(String message, String mobile) {
        QueuedResponse response = new QueuedResponse();
        response.setMessage(message);
        response.setQueuedDate(new java.util.Date());
        response.setSendStatus(QueuedResponse.UNPROCESSED);
        response.setSendTo(mobile);
        response.setEmployee(EmployeeService.getInstance().getEmployeeWithMobileNumber(mobile));
        response.setAutoResponse(false);
        QueuedResponseService.getInstance().insert(response);
    }

    private void processSMSSubmit(String message, String mobile, boolean nonconcat) {
        if (!nonconcat) {
            sendAsSingle(message, mobile);
        } else {

            int characters = 155;
            int pages = message.length() / characters;

        }
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        administratorDiag.pack();
        administratorDiag.setLocationRelativeTo(this);
        administratorDiag.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String s = new String(oldPass.getPassword());
        Administrator administrator = AdministratorService.getInstance().getAdministrator();
        boolean oldPassMatch = administrator.getPassword().matches(UIUtilities.getMD5EncryptedPassword(s));
        if (!oldPassMatch) {
            UIUtilities.addWarningMessage("Invalid password. Please enter the valid password.");
            return;
        }
        String ns = new String(newPass.getPassword());
        String ns1 = new String(confPass.getPassword());
        boolean matched = ns.matches(ns1);
        if (!matched) {
            UIUtilities.addWarningMessage("New password does not match with confirmation password.");
            return;
        }
        administrator.setPassword(UIUtilities.getMD5EncryptedPassword(ns));
        AdministratorService.getInstance().update(administrator);
        UIUtilities.addInfoMessage("Password changed.");
        administratorDiag.setVisible(false);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        GlobalLimits dialog = new GlobalLimits(new javax.swing.JFrame(), true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void drawComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawComboActionPerformed
        search();
    }//GEN-LAST:event_drawComboActionPerformed

    private void raffleComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raffleComboActionPerformed
        search();
    }//GEN-LAST:event_raffleComboActionPerformed

    private void raffleDigitSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raffleDigitSearchActionPerformed
        search();
    }//GEN-LAST:event_raffleDigitSearchActionPerformed

    private void startDateSpinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_startDateSpinStateChanged
        search();
    }//GEN-LAST:event_startDateSpinStateChanged

    private void endDateSpinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_endDateSpinStateChanged
        search();
    }//GEN-LAST:event_endDateSpinStateChanged

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        boolean confirmation = UIUtilities.getConfirmation("Exiting this program will stop processing of received SMS submission!\nAre you sure you want to exit?");
        if (confirmation) {
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {

            String draw = drawCombo.getSelectedItem().toString();
            String raffle = raffleCombo.getSelectedItem().toString();
            String raffleDigits = raffleDigitSearch.getText().trim();

            Employee e = (Employee) employeeCombo.getSelectedItem();

            Date start = FormatUtils.getDayStart((Date) startDateSpin.getValue());
            Date end = FormatUtils.getDayEnd((Date) endDateSpin.getValue());

            List<Submission> submissions = SubmissionService.getInstance().querySubmissions(draw, raffle, e, start, end, raffleDigits);

            List<TableData> data = getTableData(submissions);

            double grandTotal = 0;
            for (MainFrame.TableData tableData1 : data) {
                grandTotal += tableData1.getTotal();
            }

            TableData d = new TableData();
            //d.setDetails(null);
            d.setDraw("---");
            d.setRaffle("---");
            d.setRaffleNum("Gross:");
            d.total = grandTotal;

            data.add(d);

            String r = "/com/jps/l2app/reports/MainReport_3.jasper";
            final JasperPrint p = JasperFillManager.fillReport(MainFrame.class.getResourceAsStream(r), new HashMap(), new MainReportSource(data));

            JasperViewer.viewReport(p, false);

            //JasperPrintManager.printReport(p, true);
        } catch (JRException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        empDiagEmpList.getSelectionModel().clearSelection();
        shortNameF.setText("");
        fullNameF.setText("");
        mobileF.setText("");
//        passKeyF.setValue(0);

        shortNameF.requestFocus();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        try {
            updateSentItems();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            updateInbox();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        try {
            updateSubmissionRaw();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void sentTabEmpListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sentTabEmpListValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        try {
            updateSentItems();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_sentTabEmpListValueChanged

    private void inboxTabEmpListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_inboxTabEmpListValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }
        try {
            updateInbox();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_inboxTabEmpListValueChanged

    private void submissionTabEmpListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_submissionTabEmpListValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }

        try {
            updateSubmissionRaw();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        submissionsTabSearchF.setText("");
    }//GEN-LAST:event_submissionTabEmpListValueChanged

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        resultDiag.setVisible(false);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void resultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseClicked
        if (evt.getClickCount() == 2) {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            String draw = resultTable.getModel().getValueAt(selectedRow, 1).toString();
            String raffle = resultTable.getModel().getValueAt(selectedRow, 2).toString();
            String raffleN = resultTable.getModel().getValueAt(selectedRow, 3).toString();
            String details = resultTable.getModel().getValueAt(selectedRow, 4).toString();
            String total = resultTable.getModel().getValueAt(selectedRow, 5).toString();

            resultDiagDetailsF.setText(details);
            resultDiagDrawF.setText(draw);
            resultDiagRaffF.setText(raffle);
            resultDiagRaffNF.setText(raffleN);
            resultDiagTotalF.setText(total);

            resultDiag.pack();
            resultDiag.setLocationRelativeTo(null);
            resultDiag.setVisible(true);
        }
    }//GEN-LAST:event_resultTableMouseClicked

    private void summaryTabDrawComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summaryTabDrawComboActionPerformed
        search();
    }//GEN-LAST:event_summaryTabDrawComboActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        boolean superAdminLoggedIn = PasswordDiag.superAdminLoggedIn();
        if (!superAdminLoggedIn) {
            UIUtilities.addWarningMessage("Super admin password check failed.");
            return;
        }

        CutoffSettings d = new CutoffSettings(this, true);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        employeeDiag.pack();
        employeeDiag.setLocationRelativeTo(null);
        refreshEmpDiagEmpList();
        employeeDiag.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void autoReplyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoReplyCheckBoxActionPerformed
        if (!autoReplyCheckBox.isSelected()) {
            boolean confirmation = UIUtilities.getConfirmation("Are you sure you want to disable SMS responses?");
            if (confirmation) {
                SettingsService.getInstance().enableAutoReply(false);
            } else {
                SettingsService.getInstance().enableAutoReply(true);
                autoReplyCheckBox.setSelected(true);
            }
            return;
        } else {
            SettingsService.getInstance().enableAutoReply(true);
        }

        UIUtilities.addInfoMessage("Your changes were saved.");
    }//GEN-LAST:event_autoReplyCheckBoxActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        sendSMSDiag.pack();
        sendSMSDiag.setLocationRelativeTo(null);
        sendSMSDiag.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        search();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void summaryTabFilterFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summaryTabFilterFActionPerformed
        search();
    }//GEN-LAST:event_summaryTabFilterFActionPerformed

    private void submissionsTabFilterFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_submissionsTabFilterFKeyTyped
        filter1();
    }//GEN-LAST:event_submissionsTabFilterFKeyTyped

    private void sentTabFilterFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sentTabFilterFKeyTyped
        filter1();
    }//GEN-LAST:event_sentTabFilterFKeyTyped

    private void inboxTabFilterFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inboxTabFilterFKeyTyped
        filter1();
    }//GEN-LAST:event_inboxTabFilterFKeyTyped

    private void submissionTabDrawComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submissionTabDrawComboActionPerformed
        filter2();
    }//GEN-LAST:event_submissionTabDrawComboActionPerformed

    private void submissionsTabSearchFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_submissionsTabSearchFKeyTyped
        filter2();
    }//GEN-LAST:event_submissionsTabSearchFKeyTyped

    private void inboxTabSearchFKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inboxTabSearchFKeyTyped
        filter2();
    }//GEN-LAST:event_inboxTabSearchFKeyTyped

    private void sentTabSearchFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sentTabSearchFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sentTabSearchFActionPerformed

    private static String getSMSStatusStringRep(int stat) {
        //0 unprocessed, 1 expired, 2 error, 3 Ok
        switch (stat) {
            case -1:
                return "Incomplete";
            case 0:
                return "Unprocessed";
            case 1:
                return "Expired";
            case 2:
                return "Error";
            case 3:
                return "Ok";
            case 4:
                return "Not a submission";
        }
        return "n/a";
    }

    private void inboxTabMessListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_inboxTabMessListValueChanged
        if (evt.getValueIsAdjusting() || inboxTabMessList.getSelectedValue() == null) {
            inboxTab_smsDetailTextArea.setText("");
            return;
        }

        RawSMS r = (RawSMS) inboxTabMessList.getSelectedValue();
        QueuedResponse response = r.getResponse();

        String toDisplay
                = ""
                + "ID: " + r.getId()
                + "\n"
                + "STATUS: " + getSMSStatusStringRep(r.getProcessStatus())
                + "\n"
                + "----------"
                + "\n"
                + "RESPONSE : " + (response == null ? "N/A" : response.getMessage())
                + "\n"
                + "----------"
                + "\n"
                + "MESSAGE: "
                + r.getMessage();
        inboxTab_smsDetailTextArea.setText(toDisplay);
        inboxTab_smsDetailTextArea.setCaretPosition(1);

    }//GEN-LAST:event_inboxTabMessListValueChanged

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed

        if (inboxTabMessList.getSelectedIndex() == -1) {
            return;
        }

        RawSMS s = (RawSMS) inboxTabMessList.getSelectedValue();

        sendToF.setText(s.getSender());

        sendSMSDiag.pack();
        sendSMSDiag.setLocationRelativeTo(null);
        sendSMSDiag.setVisible(true);

    }//GEN-LAST:event_jMenuItem12ActionPerformed

    File numbersFile = null;

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(this);
        numbersFile = chooser.getSelectedFile();
        if (numbersFile != null) {
            sendToF.setEnabled(false);
            sendToF.setText(numbersFile.getName());
        }

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        numbersFile = null;
        sendToF.setEnabled(true);
        sendToF.setText("");
        sendToF.requestFocus();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void messageFCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_messageFCaretUpdate
        characters.setText(messageF.getText().length() + "");
    }//GEN-LAST:event_messageFCaretUpdate

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        printSelectedSubmissions();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        printSelectedSubmissions();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jCheckBoxMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem2ActionPerformed

        boolean selected = jCheckBoxMenuItem2.isSelected();
        Settings settingsObject = SettingsService.getInstance().getSettingsObject();
        settingsObject.setAutoPrintIncomingSubmission(selected);
        SettingsService.getInstance().update(settingsObject);
        updateMenuSettings();

    }//GEN-LAST:event_jCheckBoxMenuItem2ActionPerformed

    private void empTotalPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empTotalPrintActionPerformed
        int selectedRow = summaryTbl.getSelectedRow();
        if (selectedRow == -1) {
            UIUtilities.addWarningMessage("Please select an item first.");
            return;
        }

        EmployeeTotals empTotal = employeeTotals.get(selectedRow);
        printEmployeeTotals(empTotal);
    }//GEN-LAST:event_empTotalPrintActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        SuperAdminPasswordChangeDiag d = new SuperAdminPasswordChangeDiag(this, true);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void menuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_menuFocusGained
        Main.log("gained");
    }//GEN-LAST:event_menuFocusGained

    private void menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuMouseClicked
        updateMenuSettings();

    }//GEN-LAST:event_menuMouseClicked

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        Object v = submissionTabSubList.getSelectedValue();
        if (v == null) {
            return;
        }
        boolean confirmation = UIUtilities.getConfirmation("Continue?");
        if (!confirmation) {
            return;
        }

        boolean superAdminLoggedIn = PasswordDiag.superAdminLoggedIn();
        if (!superAdminLoggedIn) {
            UIUtilities.addWarningMessage("Super admin password check failed.");
            return;
        }

        SubmissionRaw s = (SubmissionRaw) v;
        SubmissionRawService.getInstance().removeSubmissionRaw(s);
        try {
            updateSubmissionRaw();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        EmployeeTotals e = EmployeeTotalsService.getInstance().getEmployeeTotals(s.getSender(), s.getDraw(), s.getReceivedDate());
        e.setNeedsRefresh(true);
        EmployeeTotalsService.getInstance().update(e);

        UIUtilities.addInfoMessage("Changes were saved.");
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        int[] selectedRows = summaryTbl.getSelectedRows();
        if (selectedRows.length == 0) {
            return;
        }

        boolean c = UIUtilities.getConfirmation("Are you sure you want to mark selected items as collected? This cannot be undone.");
        if (!c) {
            return;
        }

        for (int r : selectedRows) {
            EmployeeTotals t = employeeTotals.get(r);
            t.setCollected(true);
            EmployeeTotalsService.getInstance().update(t);
        }

        search();
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void mobileF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mobileF1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mobileF1ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed

        if (sentTabSentList.getSelectedIndex() == -1) {
            return;
        }

        QueuedResponse r = (QueuedResponse) sentTabSentList.getSelectedValue();
        messageTextArea.setText(r.getMessage());
        messageDialog.pack();
        messageDialog.setLocationRelativeTo(null);
        messageDialog.setVisible(true);

    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        Object selectedValue = sentTabSentList.getSelectedValue();
        if (selectedValue == null) {
            return;
        }

        QueuedResponse r = (QueuedResponse) selectedValue;
        r.setSendStatus(QueuedResponse.UNPROCESSED);
        QueuedResponseService.getInstance().update(r);
        try {
            updateSentItems();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void soldOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soldOutBtnActionPerformed
        boolean c = UIUtilities.getConfirmation("Do you want to inform everybody of soldout items?");
        if (c) {
            StringBuilder b = new StringBuilder("SOLD OUT FOR DRAW:\n\n");
            for (TableData s : soldOut) {
                b.append(s.getDraw().toUpperCase()).append(".").append(s.getRaffle().toUpperCase()).append(".").append(s.getRaffleNum()).append("\n");
            }
            b.append("\nPlease do not accept any more bets.");
            String m = b.toString();

            boolean confirmation = UIUtilities.getConfirmation(m);
            if (!confirmation) {
                return;
            }

            List<Employee> all = EmployeeService.getInstance().getAllEmployees();
            for (Employee e : all) {
                QueuedResponse r = new QueuedResponse();
                r.setAutoResponse(false);
                r.setEmployee(e);
                r.setMessage(m);
                r.setSendTo(extractEssentialMobileNum(e.getMobileNumber()));
                r.setQueuedDate(new Date());
                r.setSendStatus(QueuedResponse.UNPROCESSED);
                QueuedResponseService.getInstance().insert(r);
            }
        }
    }//GEN-LAST:event_soldOutBtnActionPerformed

    private void newForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newForwardBtnActionPerformed
        List<ForwardEntriesDiag.ForwardEntriesDiagData> fd = getForwardingData();
        ForwardEntriesDiag d = new ForwardEntriesDiag(fd);
        d.pack();
        d.setVisible(true);
        d.setLocationRelativeTo(null);
    }//GEN-LAST:event_newForwardBtnActionPerformed

    private void sendToAllCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sendToAllCheckItemStateChanged
        sendToF.setEnabled(!sendToAllCheck.isSelected());
    }//GEN-LAST:event_sendToAllCheckItemStateChanged

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed

        boolean confirmation = UIUtilities.getConfirmation("Continue sending summary to all stations?");
        if (!confirmation) {
            return;
        }
        DecimalFormat f = new DecimalFormat("##");
        SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yyyy");

        for (EmployeeTotals empTotal : employeeTotals) {
            String d = empTotal.getDraw();
            double g = empTotal.getGross();
            double c = empTotal.getComm();
            double n = g - c;

            String shortName = empTotal.getEmployee().getShortName();
            Date raffleDate = empTotal.getRaffleDate();
            String cm = c > 0 ? "\nCommission: " + f.format(c) : "";
            String t = sf.format(raffleDate) + " " + shortName + "\nDraw: " + d + "\nGross: " + f.format(g) + cm + "\nNet: " + f.format(n);
            processSMSSubmit(true, t, RawSMSService.extractEssentialMobileNum(empTotal.getEmployee().getMobileNumber()));
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void printForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printForwardBtnActionPerformed
        try {

            String r = "/com/jps/l2app/reports/MainReport_3.jasper";
            TableModel model = resultTable.getModel();
            final JasperPrint p = JasperFillManager.fillReport(MainFrame.class.getResourceAsStream(r), new HashMap(), new MainReportSource1(model));
            JasperViewer.viewReport(p, false);

            //JasperPrintManager.printReport(p, true);
        } catch (JRException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_printForwardBtnActionPerformed

    private void printSelectedSubmissions() {
        List<SubmissionRaw> selectedValuesList = submissionTabSubList.getSelectedValuesList();
        for (SubmissionRaw s : selectedValuesList) {
            printSubmissionRaw(s);
        }
        try {
            updateSubmissionRaw();
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private SwingWorker filterWorker1 = null;

    private void filter1() {
        if (filterWorker1 != null) {
            filterWorker1.cancel(true);
        }
        filterWorker1 = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(400);

                String key = "";
                JList list = null;

                Employee all = new Employee();
                all.setShortName("All");

                Employee unreg = new Employee();
                unreg.setShortName("Unregistered");

                final DefaultListModel<Employee> dlm = new DefaultListModel<>();

                switch (tabPane.getSelectedIndex()) {
                    case 2:
                        key = submissionsTabFilterF.getText();
                        list = submissionTabEmpList;
                        break;
                    case 3:
                        key = inboxTabFilterF.getText();
                        list = inboxTabEmpList;
                        dlm.addElement(all);
                        dlm.addElement(unreg);
                        break;
                    case 4:
                        key = sentTabFilterF.getText();
                        list = sentTabEmpList;
                        dlm.addElement(all);
                        dlm.addElement(unreg);
                }

                List<Employee> emp = EmployeeService.getInstance().search(key);
                for (Employee employee : emp) {
                    dlm.addElement(employee);
                }

                list.setModel(dlm);

                filterWorker1 = null;
                return null;
            }
        };
        filterWorker1.execute();
    }

    private SwingWorker filterWorker2 = null;

    private void filter2() {
        if (filterWorker2 != null) {
            filterWorker2.cancel(true);
        }
        filterWorker2 = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(400);

                switch (tabPane.getSelectedIndex()) {
                    case 2:
                        updateSubmissionRaw();
                        break;
                    case 3:
                        updateInbox();
                        break;
                    case 4:
                        updateSentItems();
                }

                filterWorker2 = null;
                return null;
            }
        };
        filterWorker2.execute();
    }

    public static void showMainFrame() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame m = getInstance();                //will invoke login 
                m.pack();
                m.setLocationRelativeTo(null);
                m.setExtendedState(Frame.MAXIMIZED_BOTH);
                m.setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Delete;
    private javax.swing.JDialog administratorDiag;
    private javax.swing.JCheckBoxMenuItem autoReplyCheckBox;
    private javax.swing.JLabel characters;
    private javax.swing.JPanel commissionPanel;
    private javax.swing.JPasswordField confPass;
    private javax.swing.JComboBox drawCombo;
    private javax.swing.JList<Employee> empDiagEmpList;
    private javax.swing.JMenuItem empTotalPrint;
    private javax.swing.JComboBox<Employee> employeeCombo;
    private javax.swing.JDialog employeeDiag;
    private javax.swing.JSpinner endDateSpin;
    private javax.swing.JTextField fullNameF;
    private javax.swing.JComboBox<String> groupCombo;
    private javax.swing.JLabel groupLbl;
    private javax.swing.JList inboxTabEmpList;
    private javax.swing.JTextField inboxTabFilterF;
    private javax.swing.JList inboxTabMessList;
    private javax.swing.JTextField inboxTabSearchF;
    private javax.swing.JTextArea inboxTab_smsDetailTextArea;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JPopupMenu jPopupMenu4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenu menu;
    private javax.swing.JDialog messageDialog;
    private javax.swing.JTextArea messageF;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JTextField mobileF;
    private javax.swing.JTextField mobileF1;
    private javax.swing.JButton newForwardBtn;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JCheckBox nonConcatCheck;
    private javax.swing.JPasswordField oldPass;
    private javax.swing.JCheckBox orderByAmtCheck;
    private javax.swing.JSpinner p42;
    private javax.swing.JSpinner p45;
    private javax.swing.JSpinner p49;
    private javax.swing.JSpinner p4d;
    private javax.swing.JSpinner p55;
    private javax.swing.JSpinner pl2;
    private javax.swing.JSpinner pl3;
    private javax.swing.JButton printForwardBtn;
    private javax.swing.JCheckBox prontonTypeCheckbox;
    private javax.swing.JSpinner ps2;
    private javax.swing.JSpinner ps3;
    private javax.swing.JSpinner ps3r;
    private javax.swing.JComboBox raffleCombo;
    private javax.swing.JTextField raffleDigitSearch;
    private javax.swing.JDialog resultDiag;
    private javax.swing.JTextPane resultDiagDetailsF;
    private javax.swing.JTextField resultDiagDrawF;
    private javax.swing.JTextField resultDiagRaffF;
    private javax.swing.JTextField resultDiagRaffNF;
    private javax.swing.JTextField resultDiagTotalF;
    private javax.swing.JButton resultSearchBtn;
    private javax.swing.JTable resultTable;
    private javax.swing.JDialog sendSMSDiag;
    private javax.swing.JCheckBox sendToAllCheck;
    private javax.swing.JTextField sendToF;
    private javax.swing.JList sentTabEmpList;
    private javax.swing.JTextField sentTabFilterF;
    private javax.swing.JTextField sentTabSearchF;
    private javax.swing.JList sentTabSentList;
    private javax.swing.JTextField shortNameF;
    private javax.swing.JButton soldOutBtn;
    private javax.swing.JSpinner startDateSpin;
    private javax.swing.JComboBox submissionTabDrawCombo;
    private javax.swing.JList submissionTabEmpList;
    private javax.swing.JLabel submissionTabResults;
    private javax.swing.JList submissionTabSubList;
    private javax.swing.JTextField submissionsTabFilterF;
    private javax.swing.JTextField submissionsTabSearchF;
    private javax.swing.JComboBox summaryTabDrawCombo;
    private javax.swing.JTextField summaryTabFilterF;
    private javax.swing.JTable summaryTbl;
    private javax.swing.JLabel summaryTotalTbl;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JLabel totalLbl;
    private javax.swing.JSpinner w4d;
    private javax.swing.JPanel winPanel;
    private javax.swing.JSpinner wl2;
    private javax.swing.JSpinner wl3;
    private javax.swing.JSpinner wp3;
    private javax.swing.JSpinner ws2;
    private javax.swing.JSpinner ws3;
    private javax.swing.JSpinner ws3r;
    // End of variables declaration//GEN-END:variables

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    public static void setInstance(MainFrame aInstance) {
        instance = aInstance;
    }

    private void printEmployeeTotals(EmployeeTotals empTotals) {

        String file = "/com/jps/l2app/reports/EmployeeSummaryShort.jasper";
        List<Submission> submissions = SubmissionService.getInstance().querySubmissions(empTotals);
        EmployeeSummaryShortSource src = new EmployeeSummaryShortSource(submissions, empTotals.getRaffleDate(), empTotals.getDraw(), empTotals.getEmployee(), "FULL");

        try {
            JasperPrint p = JasperFillManager.fillReport(MainFrame.class.getResourceAsStream(file), new HashMap(), src);
            JasperViewer.viewReport(p, false);
        } catch (JRException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

    }

    public void printSubmissionRaw(SubmissionRaw s) {

        String file = "/com/jps/l2app/reports/EmployeeSummary76.jasper";
        List<Submission> submissions = SubmissionService.getInstance().querySubmissions(s);
        EmployeeSummary76Source src = new EmployeeSummary76Source(submissions, s.getReceivedDate(), s.getDraw(), s.getSender(), s.getSubmissionId());

        JasperPrint p = null;

        try {
            p = JasperFillManager.fillReport(MainFrame.class.getResourceAsStream(file), new HashMap(), src);
            //JasperPrintManager.printReport(p, false);
            JasperViewer.viewReport(p, false);
        } catch (JRException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updateMenuSettings() {

        Settings s = SettingsService.getInstance().getSettingsObject();
        //jCheckBoxMenuItem2.setSelected(s.isAutoPrintIncomingSubmission());
        autoReplyCheckBox.setSelected(s.isAutoReplyEnabled());
    }

    public class TableData implements Comparable<TableData> {

        private String draw;
        private String raffle;
        private String raffleNum;
        private List<Submission> details = new ArrayList<>();

        private double ret = -1;
        private double total = -1;

        public double getRetained() {
            if (ret != -1) {
                return ret;
            }

            ret = 0;
            String raf = getRaffle().toLowerCase();

            if (Utils.isClosed(raffleNum)) {
                return 0;
            }
            if (raf.equalsIgnoreCase("l2")) {
                ret = MyStyle.retainedL2(getTotal());
            } else if (raf.equalsIgnoreCase("s3") || raf.equalsIgnoreCase("l3")) {
                ret = MyStyle.retaineds3(getTotal());
            }

            return ret;
        }

        public String getDetailString() {

            if (details == null) {
                return "--";
            }

            String sb = "";

            HashMap<String, Double> buff = new HashMap<>();

            for (Submission s : details) {

                String k = "n/a";
                try {
                    k = s.getSender().getShortName();
                } catch (Exception e) {
                }
                Double get = buff.get(k);
                if (get == null) {
                    buff.put(k, s.getBetValue());
                } else {
                    buff.put(k, s.getBetValue() + get);
                }

            }

            DecimalFormat f = new DecimalFormat("####");
            int count = 0;
            for (Map.Entry<String, Double> entry : buff.entrySet()) {
                String string = entry.getKey();
                Double double1 = entry.getValue();

                count++;
                sb += " " + string + "(" + f.format(double1) + ")";
                if (count != details.size()) {
                    sb += ",";
                }
            }

            return sb;
        }

        public double getTotal() {

            if (total == -1) {
                total = 0d;
                for (Submission submission : details) {
                    total += submission.getBetValue();
                }

                //TODO: change in the future
//                if (Setup.master_mode) {
//                    total = Utils.checkForWin6(raffleNum, total);
//                }
            }

            return total;
        }

        public String getDraw() {
            return draw;
        }

        public void setDraw(String draw) {
            this.draw = draw;
        }

        public String getRaffle() {
            return raffle;
        }

        public void setRaffle(String raffle) {
            this.raffle = raffle;
        }

        public String getRaffleNum() {
            return raffleNum;
        }

        public void setRaffleNum(String raffleNum) {
            this.raffleNum = raffleNum;
        }

        public List<Submission> getDetails() {
            return details;
        }

        public void setDetails(List<Submission> details) {
            this.details = details;
        }

        public String getSimpleDetailString() {

            if (details == null) {
                return "--";
            }
            DecimalFormat f = new DecimalFormat("####");

            String all = "";
            for (Submission s : details) {
                all += " " + f.format(s.getBetValue()) + ", ";
            }

            return all;

        }

        @Override
        public int compareTo(TableData o) {

            if (o.getRaffleNum().equalsIgnoreCase("gross:")) {
                return 1;
            }

            if (orderByAmtCheck.isSelected()) {
                if (o.getTotal() > getTotal()) {
                    return 1;
                } else if (o.getTotal() == getTotal()) {
                    return 0;
                } else {
                    return -1;
                }
            }

            int oi;
            int ti;
            if (o.getRaffle().equalsIgnoreCase(getRaffle())) {
                oi = Integer.parseInt(o.getRaffleNum());
                ti = Integer.parseInt(getRaffleNum());
            } else {
                oi = rafVal(o.getRaffle());
                ti = rafVal(getRaffle());
            }

            if (oi == ti) {
                return 0;
            } else if (oi < ti) {
                return 1;
            } else {
                return -1;
            }

        }

        public int rafVal(String raf) {

            int v = 0;
            switch (raf.toUpperCase()) {
                case "L2":
                    v = 1;
                    break;
                case "L3":
                    v = 2;
                    break;
                case "S3":
                    v = 3;
                    break;
                case "S2":
                    v = 4;
                    break;
                case "S3R":
                    v = 5;
                    break;
                case "P42":
                    v = 6;
                case "P45":
                    v = 7;
                case "P49":
                case "P55":
                    v = 8;
                    break;
                case "4D":
                    v = 9;
                    break;

            }
            return v;
        }

        private double getForwarded() {
            double forward = getTotal() - getRetained();
            return forward;
        }

    }

    private List<TableData> getTableData(List<Submission> submissions) {
        List<TableData> tableData = new ArrayList<>();
        for (Submission s : submissions) {
            String sdraw = s.getDraw();
            String sraffleType = s.getRaffleType();
            String sraffleNum = s.getRaffleNumber();

            TableData tData = null;
            for (TableData td : tableData) {
                if (td.getDraw().matches(sdraw) && td.getRaffle().matches(sraffleType) && td.getRaffleNum().matches(sraffleNum)) {
                    tData = td;
                    break;
                }
            }

            if (tData == null) {
                tData = new TableData();
                tData.setDraw(sdraw);
                tData.setRaffle(sraffleType);
                tData.setRaffleNum(sraffleNum);
                tableData.add(tData);
            }

            tData.getDetails().add(s);

        }

        Collections.sort(tableData);

        return tableData;
    }

}
