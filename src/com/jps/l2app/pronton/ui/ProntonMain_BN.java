/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.pronton.ui;

import com.jps.l2app.devices.DeviceSpecifications;
import com.jps.l2app.entities.Administrator;
import com.jps.l2app.entities.GlobalLimit;
import com.jps.l2app.entities.ProntonTransaction;
import com.jps.l2app.entities.ProntonTransactionDetail;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.services.AdministratorService;
import com.jps.l2app.entities.services.GlobalLimitService;
import com.jps.l2app.entities.services.ProntonTransactionDetailService;
import com.jps.l2app.entities.services.ProntonTransactionService;
import com.jps.l2app.entities.services.QueuedResponseService;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import com.jps.l2app.reports.ProntonReceiptSource;
import com.jps.l2app.reports.ProntonSearchResultSource;
import com.jps.l2app.reports.ProntonSummarySource;
import static com.jps.l2app.reports.ProntonSummarySource.raffles;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
import com.jps.l2app.server.ui.CutoffSettings;
import com.jps.l2app.server.ui.GlobalLimits_BN;
import com.jps.l2app.server.ui.PasswordDiag;
import com.jps.l2app.server.ui.PrinterSettings;
import com.jps.l2app.server.ui.SendingProgress;
import com.jps.l2app.utils.UIUtilities;
import com.jps.l2app.utils.FormatUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JSpinner;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author admin
 */
public final class ProntonMain_BN extends javax.swing.JFrame {

    private static final boolean showPrintPreview = false;

    private static ProntonMain_BN instance;

    public static ProntonMain_BN getInstance() {
        if (instance == null) {
            Main.log("Pronton_BN");
            instance = new ProntonMain_BN();
            UIUtilities.addInfoMessage("This copy is exclusive for " + Setup.businessName + " Stations only.");
        }
        return instance;
    }

    final DecimalFormat formatter = new DecimalFormat("000000");
    final SimpleDateFormat dformatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss aa");

    ProntonTransaction prontonTransaction;
    List<ProntonTransactionDetail> details = new ArrayList<>();

    /**
     * Creates new form ProntonMain
     */
    public static void initCom(Component c) {

        if (!Setup.master_mode) {
            return;
        }

        if (c instanceof JMenuBar || c instanceof JSpinner || c instanceof JComboBox) {
            return;
        }

        c.setBackground(Color.BLACK);
        c.setForeground(Color.WHITE);

        if (c instanceof Component) {
            Component[] components = ((Container) c).getComponents();
            for (Component com : components) {
                initCom(com);
            }
        }

    }

    public ProntonMain_BN() {
        initComponents();
        //initCom(this);
        //initCom(jDialog1);
        //initCom(administratorDiag);
        if (!Setup.allowPrinterSetup) {
            jMenuItem43.getParent().remove(jMenuItem43);
        }

        tabPane.setMnemonicAt(0, 1);

        SwingWorker w = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                updateMainTable();
                updateDetailSummTable();
                return null;
            }
        };
        w.execute();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ProntonMain_BN.this.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
        new Thread(new Runnable() {

            SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10_000);
                        String format = time.format(new Date());
                        timeLbl.setText("TIME: " + format);

                        //ProntonTransactionService.getInstance().resendPending();
                    } catch (Exception e) {
                    }
                }
            }
        }).start();

        headerLbl.setText("TRP Client Version " + Setup.VERSION + " - Exclusive for " + Setup.businessName + " Stations.");

    }

    public void sendingError() {
        setSendStatus("Sending status: ERROR! Check Load/Signal");
    }

    public void sendingSuccess() {
        setSendStatus("Sending status: OK");
    }

    public void setSendStatus(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                stat3.setText("Send Status: " + msg);
            }
        });
    }

    public boolean timeLimitReached(String draw) {

        Settings settingsObject = SettingsService.getInstance().getSettingsObject();
        SimpleDateFormat cutofFormatter = new SimpleDateFormat("h:mm a");

        String cutOffTime = null;
        switch (draw.toLowerCase()) {
            case "d1":
                cutOffTime = settingsObject.getDraw1Cutoff();
                break;
            case "d2":
                cutOffTime = settingsObject.getDraw2Cutoff();
                break;
            case "d3":
                cutOffTime = settingsObject.getDraw3Cutoff();
                break;
        }

        try {
            Date cutoff = cutofFormatter.parse(cutOffTime);
            Date now = cutofFormatter.parse(cutofFormatter.format(new Date()));
            return !now.before(cutoff);

        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        raffleNum = new javax.swing.JTextField();
        raffleCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detailTable = new javax.swing.JTable();
        jDialog1_save = new javax.swing.JButton();
        jdialog1_addBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jDialog1_remove = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        betFieldSp = new javax.swing.JFormattedTextField();
        jdialog1_balance = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        administratorDiag = new javax.swing.JDialog();
        oldPass = new javax.swing.JPasswordField();
        jLabel16 = new javax.swing.JLabel();
        newPass = new javax.swing.JPasswordField();
        jLabel17 = new javax.swing.JLabel();
        confPass = new javax.swing.JPasswordField();
        jLabel18 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        goBtn = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        transactionSearchField = new javax.swing.JTextField();
        mainTotalLbl = new javax.swing.JLabel();
        drawMain = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        mainTransactionTable = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        detailSummTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        detailSearchField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        detailsTotal = new javax.swing.JLabel();
        detailSearch_draw = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        detailSearch_raffle = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        detailSearch_type = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        headerLbl = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        globalDateSpinner = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        timeLbl = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem41 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem39 = new javax.swing.JMenuItem();
        jMenuItem42 = new javax.swing.JMenuItem();
        jMenuItem43 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem36 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        stat1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        stat3 = new javax.swing.JMenu();

        jDialog1.setTitle("Transactions");
        jDialog1.setModal(true);
        jDialog1.setUndecorated(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        jLabel4.setText("Raffle No.:");

        raffleNum.setNextFocusableComponent(raffleCombo);
        raffleNum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                raffleNumFocusGained(evt);
            }
        });
        raffleNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raffleNumActionPerformed(evt);
            }
        });
        raffleNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                raffleNumKeyPressed(evt);
            }
        });

        raffleCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "S3", "S3R" }));
        raffleCombo.setFocusable(false);
        raffleCombo.setNextFocusableComponent(betFieldSp);
        raffleCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                raffleComboItemStateChanged(evt);
            }
        });

        jLabel2.setText("Total: <>");

        detailTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Draw", "Raffle", "Raffle Number", "Bet"
            }
        ));
        detailTable.setRowHeight(24);
        jScrollPane1.setViewportView(detailTable);

        jDialog1_save.setMnemonic('s');
        jDialog1_save.setText("Finalize and Print Transaction");
        jDialog1_save.setFocusable(false);
        jDialog1_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDialog1_saveActionPerformed(evt);
            }
        });

        jdialog1_addBtn.setMnemonic('n');
        jdialog1_addBtn.setText("New");
        jdialog1_addBtn.setFocusable(false);
        jdialog1_addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdialog1_addBtnActionPerformed(evt);
            }
        });

        jLabel3.setText("Bet:");

        jLabel1.setText("Transaction Details:");

        jDialog1_remove.setMnemonic('e');
        jDialog1_remove.setText("Remove Selected");
        jDialog1_remove.setFocusable(false);
        jDialog1_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDialog1_removeActionPerformed(evt);
            }
        });

        jButton10.setMnemonic('c');
        jButton10.setText("Cancel");
        jButton10.setFocusable(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        betFieldSp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        betFieldSp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                betFieldSpFocusGained(evt);
            }
        });
        betFieldSp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                betFieldSpActionPerformed(evt);
            }
        });

        jdialog1_balance.setForeground(new java.awt.Color(204, 0, 0));
        jdialog1_balance.setText("<>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDialog1_remove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDialog1_save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10))
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(raffleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdialog1_balance, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(raffleNum, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(betFieldSp, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdialog1_addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {betFieldSp, raffleNum});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(raffleNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdialog1_addBtn)
                    .addComponent(raffleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jdialog1_balance)
                    .addComponent(betFieldSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDialog1_save)
                    .addComponent(jDialog1_remove)
                    .addComponent(jButton10)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jMenu9.setMnemonic('t');
        jMenu9.setText("Select");

        jMenuItem34.setMnemonic('f');
        jMenuItem34.setText("Select First in table");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem34);

        jMenuItem35.setMnemonic('s');
        jMenuItem35.setText("Select Last in table");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem35);

        jMenuBar2.add(jMenu9);

        jMenu5.setMnemonic('r');
        jMenu5.setText("Raffle Type");

        jMenuItem9.setMnemonic('1');
        jMenuItem9.setText("1. S3");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);

        jMenuItem10.setMnemonic('2');
        jMenuItem10.setText("2. S3R");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuBar2.add(jMenu5);

        jDialog1.setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        administratorDiag.setTitle("Admin Password");

        jLabel16.setText("Old Password:");

        jLabel17.setText("New Password:");

        jLabel18.setText("Confirm Password:");

        jButton9.setText("Change Password");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
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
                        .addComponent(jButton9)))
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
                .addComponent(jButton9)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jButton8.setMnemonic('e');
        jButton8.setText("Submit Draw Summary");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TRP Client - Exclusive for LR Stations");
        setUndecorated(true);

        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneStateChanged(evt);
            }
        });

        jLabel5.setText("Search Transaction/Draw:");

        goBtn.setMnemonic('g');
        goBtn.setText("Go");
        goBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goBtnActionPerformed(evt);
            }
        });

        jButton5.setMnemonic('c');
        jButton5.setText("Create New Transaction");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        transactionSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transactionSearchFieldActionPerformed(evt);
            }
        });

        mainTotalLbl.setText("Total: 00");

        drawMain.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "D1", "D2", "D3" }));
        drawMain.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                drawMainItemStateChanged(evt);
            }
        });

        jLabel9.setText("Draw:");

        mainTransactionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        mainTransactionTable.setRowHeight(24);
        mainTransactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainTransactionTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(mainTransactionTable);

        jButton7.setMnemonic('n');
        jButton7.setText("Print Selected");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel14.setForeground(new java.awt.Color(204, 0, 0));
        jLabel14.setText("Note: Please Click 'Go' to refresh total.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(transactionSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(goBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drawMain, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(mainTotalLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transactionSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(goBtn)
                    .addComponent(jButton5)
                    .addComponent(drawMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 284, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(mainTotalLbl)
                    .addComponent(jLabel14))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(46, 46, 46)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addGap(36, 36, 36)))
        );

        tabPane.addTab("Transactions", jPanel2);

        detailSummTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        detailSummTable.setRowHeight(24);
        jScrollPane3.setViewportView(detailSummTable);

        jLabel7.setText("Draw:");

        detailSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailSearchFieldActionPerformed(evt);
            }
        });

        jButton1.setMnemonic('g');
        jButton1.setText("Go");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        detailsTotal.setText("Total: 0");

        detailSearch_draw.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "D1", "D2", "D3" }));
        detailSearch_draw.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                detailSearch_drawItemStateChanged(evt);
            }
        });
        detailSearch_draw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailSearch_drawActionPerformed(evt);
            }
        });

        jLabel8.setText("Raffle:");

        detailSearch_raffle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "S3", "S3R" }));
        detailSearch_raffle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                detailSearch_raffleItemStateChanged(evt);
            }
        });
        detailSearch_raffle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailSearch_raffleActionPerformed(evt);
            }
        });

        jLabel11.setText("Keyword:");

        jLabel12.setText("Type:");

        detailSearch_type.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Raffle Number", "Detail ID" }));
        detailSearch_type.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                detailSearch_typeItemStateChanged(evt);
            }
        });
        detailSearch_type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailSearch_typeActionPerformed(evt);
            }
        });

        jButton2.setMnemonic('e');
        jButton2.setText("Print Results");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(detailSearch_draw, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(detailSearch_raffle, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(detailSearch_type, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(detailSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(detailsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detailSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButton1)
                    .addComponent(detailSearch_draw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(detailSearch_raffle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(detailSearch_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detailsTotal)
                    .addComponent(jButton2))
                .addGap(2, 2, 2))
        );

        tabPane.addTab("Search Results", jPanel3);

        headerLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/Numbers16.png"))); // NOI18N
        headerLbl.setText("TRP Client Version 5.0 - by Dominoff Technologies Inc.");

        globalDateSpinner.setModel(new javax.swing.SpinnerDateModel());
        globalDateSpinner.setEditor(new javax.swing.JSpinner.DateEditor(globalDateSpinner, "dd-MMM-yyyy"));

        jLabel13.setText("Date:");

        timeLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLbl.setText("TIME: 00:00 aa");
        timeLbl.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jMenuBar1.setOpaque(true);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/stock_execute_16.png"))); // NOI18N
        jMenu1.setMnemonic('s');
        jMenu1.setText("Setup");

        jMenuItem41.setMnemonic('n');
        jMenuItem41.setText("Station Name");
        jMenuItem41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem41ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem41);

        jMenuItem19.setMnemonic('u');
        jMenuItem19.setText("Cut-off Schedule");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem19);

        jMenuItem1.setMnemonic('l');
        jMenuItem1.setText("Limits");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem39.setMnemonic('m');
        jMenuItem39.setText("Server Mobile Number");
        jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem39ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem39);

        jMenuItem42.setMnemonic('p');
        jMenuItem42.setText("Administrator Password");
        jMenuItem42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem42ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem42);

        jMenuItem43.setMnemonic('i');
        jMenuItem43.setText("Printer");
        jMenuItem43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem43ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem43);

        jMenuItem2.setMnemonic('x');
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/b_print.png"))); // NOI18N
        jMenu10.setMnemonic('p');
        jMenu10.setText("Print Summary");

        jMenuItem36.setMnemonic('1');
        jMenuItem36.setText("Print Draw 1");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem36);

        jMenuItem37.setMnemonic('2');
        jMenuItem37.setText("Print Draw 2");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem37);

        jMenuItem38.setMnemonic('3');
        jMenuItem38.setText("Print Draw 3");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem38);

        jMenuBar1.add(jMenu10);

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/view.png"))); // NOI18N
        jMenu6.setMnemonic('w');
        jMenu6.setText("View");

        jMenuItem3.setMnemonic('t');
        jMenuItem3.setText("Transactions");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem3);

        jMenuItem5.setMnemonic('a');
        jMenuItem5.setText("Search Transaction");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem5);

        jMenuItem4.setMnemonic('r');
        jMenuItem4.setText("Results");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem4);

        jMenuBar1.add(jMenu6);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/Numbers16.png"))); // NOI18N
        jMenu4.setMnemonic('d');
        jMenu4.setText("Draw");

        jMenuItem30.setMnemonic('t');
        jMenuItem30.setText("Select All");
        jMenuItem30.setEnabled(false);
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem30);

        jMenuItem6.setMnemonic('1');
        jMenuItem6.setText("Select Draw 1");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuItem7.setMnemonic('2');
        jMenuItem7.setText("Select Draw 2");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuItem8.setMnemonic('3');
        jMenuItem8.setText("Select Draw 3");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuBar1.add(jMenu4);

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/spellcheck.png"))); // NOI18N
        jMenu7.setMnemonic('r');
        jMenu7.setText("Raffle");
        jMenu7.setEnabled(false);

        jMenuItem31.setMnemonic('a');
        jMenuItem31.setText("ALL");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem31);

        jMenuItem20.setMnemonic('1');
        jMenuItem20.setText("1. L2");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem20);

        jMenuItem21.setMnemonic('2');
        jMenuItem21.setText("2. L3");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem21);

        jMenuItem22.setMnemonic('3');
        jMenuItem22.setText("3. S3");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem22);

        jMenuItem23.setMnemonic('4');
        jMenuItem23.setText("4. S2");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem23);

        jMenuItem24.setMnemonic('5');
        jMenuItem24.setText("5. S3R");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem24);

        jMenuItem25.setMnemonic('6');
        jMenuItem25.setText("6. P42");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem25);

        jMenuItem26.setMnemonic('7');
        jMenuItem26.setText("7. P45");
        jMenuItem26.setToolTipText("");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem26);

        jMenuItem27.setMnemonic('8');
        jMenuItem27.setText("8. P49");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem27);

        jMenuItem28.setMnemonic('9');
        jMenuItem28.setText("9. P55");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem28);

        jMenuItem29.setMnemonic('0');
        jMenuItem29.setText("10. 4D");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem29);

        jMenuBar1.add(jMenu7);

        jMenu8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/b_view.png"))); // NOI18N
        jMenu8.setMnemonic('h');
        jMenu8.setText("Search Type");
        jMenu8.setEnabled(false);

        jMenuItem32.setMnemonic('r');
        jMenuItem32.setText("Raffle Number");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem32);

        jMenuItem33.setMnemonic('d');
        jMenuItem33.setText("Detail ID");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem33);

        jMenuBar1.add(jMenu8);

        jMenu2.setText("  |  ");
        jMenuBar1.add(jMenu2);

        stat1.setText("Receiver Signal: N/A");
        jMenuBar1.add(stat1);

        jMenu3.setText("   ");
        jMenuBar1.add(jMenu3);

        stat3.setText("Sending Status: N/A");
        jMenuBar1.add(stat3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(headerLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(169, 169, 169)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(globalDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(globalDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(timeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(tabPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        prepareNewTransaction();
    }//GEN-LAST:event_jButton5ActionPerformed

    private boolean prepareNewTransaction() {
        String draw = drawMain.getSelectedItem().toString();
        boolean timeLimitReached = timeLimitReached(draw);
        if (timeLimitReached) {
            UIUtilities.addWarningMessage("Time limit reached for " + draw);
            //ProntonMain.this.setVisible(true);
            return false;
        }

        details = new ArrayList<>();

        transaction = new ProntonTransaction();
        transaction.setTransactionTimeStamp(new Date());
        transaction.setDraw(draw);
        transaction = ProntonTransactionService.getInstance().update(transaction);

        jDialog1.setTitle("Transaction # " + transaction.getId());

        jDialog1_updateTable();

        raffleCombo.setEnabled(true);
        raffleNum.setEnabled(true);
        betFieldSp.setEnabled(true);

        jdialog1_addBtn.setEnabled(true);
        jDialog1_remove.setEnabled(true);
        jDialog1_save.setEnabled(true);

        jDialog1.pack();
        jDialog1.setLocationRelativeTo(null);
        jDialog1.setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                raffleNum.requestFocus();
            }

        });

        return true;

    }

    private void jdialog1_addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdialog1_addBtnActionPerformed
        raffleNum.requestFocus();
    }//GEN-LAST:event_jdialog1_addBtnActionPerformed

    private void jDialog1_addDetail() throws NumberFormatException {

        int draw = drawMain.getSelectedIndex() + 1;

        String raffle = raffleCombo.getSelectedItem().toString();
        String raffNum = raffleNum.getText().trim();
        int bet = Integer.parseInt(betFieldSp.getValue().toString());

        Set<String> rafNums = new HashSet();
        if (raffle.equalsIgnoreCase("s3r")) {
            rafNums = FormatUtils.rambolito(raffNum);
            bet = bet / rafNums.size();
        } else {
            rafNums.add(raffNum);
        }

        for (String rn : rafNums) {
            addDetail(raffle, rn, draw, bet);
        }

        jDialog1_updateTable();

    }

    private boolean addDetail(String raffle, String raffNum, int draw, double bet) {
        //<editor-fold defaultstate="collapsed" desc="digit check">
        int digits = 0;
        switch (raffle) {
            case "L2":
            case "S2":
                digits = 2;
                break;
            case "L3":
            case "S3":
            case "S3R":
                digits = 3;
                break;
            case "P42":
            case "P45":
            case "P49":
            case "P55":
                digits = 6;
                break;
            case "4D":
                digits = 4;
        }
        if (digits != raffNum.length()) {
            UIUtilities.addWarningMessage("Invalid number of digits for raffle number. Expected: " + digits);
            raffleNum.requestFocus();
            return true;
        }
        //</editor-fold>

        GlobalLimit check = GlobalLimitService.getInstance().check(draw, raffle, raffNum, null);
        if (check != null && check.getLimitValue() == 0) {
            UIUtilities.addWarningMessage("Raffle number closed!");
            return true;
        }
        String drwStr = "D" + draw;
        double totalForRaffle = GlobalLimitService.getInstance().getTodaysTotal(drwStr, raffle, raffNum);
        GlobalLimit limit = GlobalLimitService.getInstance().check(draw, raffle, raffNum, null);
        if (limit == null) {
            UIUtilities.addWarningMessage("Cannot continue. Limit not set for " + raffNum + " @ " + raffle + "\nPlease set limits first at setup menu. Use 'ALL' to provide a general limit.");
            return true;
        }
        double globalLimit = limit.getLimitValue();
        if (globalLimit - totalForRaffle - bet < 0) {
            UIUtilities.addWarningMessage("Limit reached for raffle!");
            return true;
        }

        ProntonTransactionDetail ds = new ProntonTransactionDetail();
        ds.setTransaction(transaction);
        ds.setBet(bet);
        ds.setDraw(drwStr);
        ds.setRaffle(raffle);
        ds.setRaffleNumber(raffNum);
        ds = ProntonTransactionDetailService.getInstance().update(ds);
        details.add(ds);

        return false;
    }

    public void updateReceiverSignal(final String signal) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                stat1.setText("Reciever Signal: " + signal);
            }
        });
    }

    private ProntonTransaction transaction;

    private void jDialog1_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDialog1_saveActionPerformed

        boolean confirmation = UIUtilities.getConfirmation("Are you sure you want to submit this transaction?");
        if (!confirmation) {
            return;
        }

        double total = 0d;
        for (ProntonTransactionDetail detail : details) {
            total += detail.getBet();
            detail.setTransaction(transaction);
            ProntonTransactionDetailService.getInstance().update(detail);
        }

        if (total == 0) {
            UIUtilities.addWarningMessage("Invalid total bet: 0");
            return;
        }

        transaction.setTotal(total);
        transaction = ProntonTransactionService.getInstance().update(transaction);

        printTransaction(transaction);

        transaction = null;

        prepareNewTransaction();

    }//GEN-LAST:event_jDialog1_saveActionPerformed

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
        Main.log("evt = " + evt.getKeyCode());
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        boolean confirmation = UIUtilities.getConfirmation("Are you sure you want to cancel this transaction?");
        if (!confirmation) {
            return;
        }

        cancelTransaction();
        updateMainTable();
        jDialog1.setVisible(false);

    }//GEN-LAST:event_jButton10ActionPerformed

    private void cancelTransaction() {
        if (transaction != null) {

            for (ProntonTransactionDetail detail : details) {
                ProntonTransactionDetailService.getInstance().remove(detail);
            }

            List<ProntonTransactionDetail> details1 = ProntonTransactionService.getInstance().getDetails(transaction.getId());

            for (ProntonTransactionDetail details11 : details1) {
                ProntonTransactionDetailService.getInstance().remove(details11);
            }

            ProntonTransactionService.getInstance().remove(transaction);
        }

        transaction = null;

    }

    private void goBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goBtnActionPerformed
        updateMainTable();
    }//GEN-LAST:event_goBtnActionPerformed

    private void jDialog1_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDialog1_removeActionPerformed
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        ProntonTransactionDetail removed = details.remove(selectedRow);
        ProntonTransactionDetailService.getInstance().remove(removed);

        jDialog1_updateTable();

    }//GEN-LAST:event_jDialog1_removeActionPerformed

    private void raffleNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raffleNumActionPerformed
        betFieldSp.requestFocus();
    }//GEN-LAST:event_raffleNumActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        int selectedRow = mainTransactionTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtilities.addInfoMessage("Please select a transaction. Navigate using the TAB button until the table is selected.");
            return;
        }
        Object valueAt = mainTransactionTable.getModel().getValueAt(selectedRow, 0);
        if (valueAt != null) {
            int id = Integer.parseInt(valueAt.toString());
            ProntonTransaction found = ProntonTransactionService.getInstance().find(id);
            printTransaction(found);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void transactionSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transactionSearchFieldActionPerformed
        updateMainTable();
    }//GEN-LAST:event_transactionSearchFieldActionPerformed

    private void betFieldSpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_betFieldSpFocusGained
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                betFieldSp.setText(betFieldSp.getText());
                betFieldSp.selectAll();
            }
        });
    }//GEN-LAST:event_betFieldSpFocusGained

    private void betFieldSpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_betFieldSpActionPerformed

        jDialog1_addDetail();
        raffleNum.requestFocus();

    }//GEN-LAST:event_betFieldSpActionPerformed

    private void raffleNumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_raffleNumFocusGained
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                raffleNum.setText(raffleNum.getText());
                raffleNum.selectAll();
            }
        });
    }//GEN-LAST:event_raffleNumFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        updateDetailSummTable();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void detailSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailSearchFieldActionPerformed
        updateDetailSummTable();
    }//GEN-LAST:event_detailSearchFieldActionPerformed

    private void detailSearch_drawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailSearch_drawActionPerformed
        updateDetailSummTable();
    }//GEN-LAST:event_detailSearch_drawActionPerformed

    private void detailSearch_raffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailSearch_raffleActionPerformed
        updateDetailSummTable();
    }//GEN-LAST:event_detailSearch_raffleActionPerformed

    private void detailSearch_typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailSearch_typeActionPerformed
        updateDetailSummTable();
    }//GEN-LAST:event_detailSearch_typeActionPerformed

    private void mainTransactionTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTransactionTableMouseClicked
        if (evt.getClickCount() == 2) {
            openTransaction();
        }
    }//GEN-LAST:event_mainTransactionTableMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        GlobalLimits_BN dialog = new GlobalLimits_BN(this, true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        boolean confirmation = UIUtilities.getConfirmation("Exiting this program will stop processing of received SMS submission!\nAre you sure you want to exit?");
        if (confirmation) {
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int selectedRow = mainTransactionTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Date spindate = (Date) globalDateSpinner.getValue();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String spin = f.format(spindate);
        if (!spin.matches(f.format(new Date()))) {
            UIUtilities.addWarningMessage("Operation not allowed. Transaction not issued today.");
            return;
        }

        Object valueAt = mainTransactionTable.getModel().getValueAt(selectedRow, 0);
        if (valueAt != null) {
            int id = Integer.parseInt(valueAt.toString());
            ProntonTransaction found = ProntonTransactionService.getInstance().find(id);
            QueuedResponse submitSMS = found.getSubmitSMS();
            submitSMS.setSendStatus(QueuedResponse.UNPROCESSED);
            submitSMS.setSendTo(SettingsService.getInstance().getSettingsObject().getServerMobileNumber());
            QueuedResponseService.getInstance().update(submitSMS);
        }

        UIUtilities.addInfoMessage("Your command has been processed. Please make sure your device is working properly.");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void raffleComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_raffleComboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            jDialog_updateRemainingBalanceLabel();
            UIUtilities.addInfoMessage("Raffle " + evt.getItem().toString() + " selected.");
            raffleNum.requestFocus();
        }
    }//GEN-LAST:event_raffleComboItemStateChanged

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        tabPane.setSelectedIndex(0);
        tabPane.setEnabledAt(0, true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        tabPane.setSelectedIndex(1);
        tabPane.setEnabledAt(1, true);
        detailSearchField.requestFocus();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        tabPane.setSelectedIndex(0);
        tabPane.setEnabledAt(0, true);
        transactionSearchField.requestFocusInWindow();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        drawMain.setSelectedIndex(0);
        detailSearch_draw.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        drawMain.setSelectedIndex(1);
        detailSearch_draw.setSelectedIndex(2);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        drawMain.setSelectedIndex(2);
        detailSearch_draw.setSelectedIndex(3);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void drawMainItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_drawMainItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            UIUtilities.addInfoMessage("Draw " + evt.getItem().toString() + " is selected.");
        }
    }//GEN-LAST:event_drawMainItemStateChanged

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        raffleCombo.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        raffleCombo.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        detailSearch_raffle.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        detailSearch_raffle.setSelectedIndex(2);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        detailSearch_raffle.setSelectedIndex(3);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        detailSearch_raffle.setSelectedIndex(4);
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        detailSearch_raffle.setSelectedIndex(5);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        detailSearch_raffle.setSelectedIndex(6);
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        detailSearch_raffle.setSelectedIndex(7);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        detailSearch_raffle.setSelectedIndex(8);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        detailSearch_raffle.setSelectedIndex(9);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        detailSearch_raffle.setSelectedIndex(10);
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void tabPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneStateChanged
        if (tabPane.getSelectedIndex() == 1) {
            jMenu7.setEnabled(true);
            jMenuItem30.setEnabled(true);
            jMenu8.setEnabled(true);
        } else {
            jMenu7.setEnabled(false);
            jMenuItem30.setEnabled(false);
            jMenu8.setEnabled(false);
        }
    }//GEN-LAST:event_tabPaneStateChanged

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        detailSearch_draw.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        detailSearch_raffle.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        detailSearch_type.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        detailSearch_type.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void detailSearch_drawItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_detailSearch_drawItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            detailSearchField.requestFocus();
        }
    }//GEN-LAST:event_detailSearch_drawItemStateChanged

    private void detailSearch_raffleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_detailSearch_raffleItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            detailSearchField.requestFocus();
        }
    }//GEN-LAST:event_detailSearch_raffleItemStateChanged

    private void detailSearch_typeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_detailSearch_typeItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            detailSearchField.requestFocus();
        }
    }//GEN-LAST:event_detailSearch_typeItemStateChanged

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed

        if (!PasswordDiag.adminLoggedIn()) {
            Main.log("Super admin check failed.");
            return;
        }

        CutoffSettings d = new CutoffSettings(this, true);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                detailTable.changeSelection(0, 0, false, false);
            }
        });

    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                detailTable.changeSelection(details.size() - 1, 0, false, false);
            }
        });
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed
        printDrawSummary((Date) globalDateSpinner.getValue(), "d1");
        summarySubmit("D1");
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed
        printDrawSummary((Date) globalDateSpinner.getValue(), "d2");
        summarySubmit("D2");
    }//GEN-LAST:event_jMenuItem37ActionPerformed

    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        printDrawSummary((Date) globalDateSpinner.getValue(), "d3");
        summarySubmit("D3");
    }//GEN-LAST:event_jMenuItem38ActionPerformed

    private void jMenuItem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem39ActionPerformed
        String singleInput = UIUtilities.getSingleInput("Please enter new server mobile number.");
        if (singleInput == null) {
            return;
        }

        singleInput = singleInput.trim();
        UIUtilities.getConfirmation("Are you sure you want to change the server mobile number to " + singleInput + "?");
        Settings s = SettingsService.getInstance().getSettingsObject();
        s.setServerMobileNumber(singleInput);
        SettingsService.getInstance().update(s);
    }//GEN-LAST:event_jMenuItem39ActionPerformed

    SwingWorker limitLabelUpdater = null;

    private void raffleNumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_raffleNumKeyPressed

        jDialog_updateRemainingBalanceLabel();

    }//GEN-LAST:event_raffleNumKeyPressed

    private void jMenuItem41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem41ActionPerformed

        Settings s = SettingsService.getInstance().getSettingsObject();
        String sn = UIUtilities.getSingleInput("Please enter new station name: ");
        if (sn != null) {
            s.setProntonStationName(sn);
        } else {
            return;
        }
        SettingsService.getInstance().update(s);
        UIUtilities.addInfoMessage("Station name changed to: " + sn);
    }//GEN-LAST:event_jMenuItem41ActionPerformed

    private void jMenuItem42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem42ActionPerformed
        if (!PasswordDiag.adminLoggedIn()) {
            return;
        }

        administratorDiag.pack();
        administratorDiag.setLocationRelativeTo(this);
        administratorDiag.setVisible(true);

    }//GEN-LAST:event_jMenuItem42ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
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
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (result != null && !result.isEmpty()) {
            boolean confirmation = UIUtilities.getConfirmation("Continue printing?");
            if (confirmation) {
                printSearchResults(result);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem43ActionPerformed
        PrinterSettings s = new PrinterSettings();
        s.pack();
        s.setLocationRelativeTo(null);
        s.setVisible(true);
    }//GEN-LAST:event_jMenuItem43ActionPerformed

    public static void showMainFrame() {

        if (instance != null) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

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

                ProntonMain_BN m = getInstance();
                m.pack();
                m.setVisible(true); //not yet let login show
                m.setExtendedState(MAXIMIZED_BOTH);

                //m.setLocationRelativeTo(null);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog administratorDiag;
    private javax.swing.JFormattedTextField betFieldSp;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPasswordField confPass;
    private javax.swing.JTextField detailSearchField;
    private javax.swing.JComboBox detailSearch_draw;
    private javax.swing.JComboBox detailSearch_raffle;
    private javax.swing.JComboBox detailSearch_type;
    private javax.swing.JTable detailSummTable;
    private javax.swing.JTable detailTable;
    private javax.swing.JLabel detailsTotal;
    private javax.swing.JComboBox drawMain;
    private javax.swing.JSpinner globalDateSpinner;
    private javax.swing.JButton goBtn;
    private javax.swing.JLabel headerLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JButton jDialog1_remove;
    private javax.swing.JButton jDialog1_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton jdialog1_addBtn;
    private javax.swing.JLabel jdialog1_balance;
    private javax.swing.JLabel mainTotalLbl;
    private javax.swing.JTable mainTransactionTable;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JPasswordField oldPass;
    private javax.swing.JComboBox raffleCombo;
    private javax.swing.JTextField raffleNum;
    private javax.swing.JMenu stat1;
    private javax.swing.JMenu stat3;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JLabel timeLbl;
    private javax.swing.JTextField transactionSearchField;
    // End of variables declaration//GEN-END:variables

    private void jDialog1_updateTable() {

        jDialog_updateRemainingBalanceLabel();

        double total = 0d;
        for (ProntonTransactionDetail detail : details) {
            total += detail.getBet();
        }

        jLabel2.setText("Total: " + total);

        DefaultTableModel model;
        model = new DefaultTableModel() {

            @Override
            public int getColumnCount() {
                return 5;
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "DetailId";
                    case 1:
                        return "Draw";
                    case 2:
                        return "Raffle";
                    case 3:
                        return "Raffle No.";
                    case 4:
                        return "Bet";
                }
                return "";
            }

            @Override
            public int getRowCount() {
                return details.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                ProntonTransactionDetail r = details.get(row);
                switch (column) {
                    case 0:
                        return Integer.toString(r.getId(), 36).toUpperCase();
                    case 1:
                        return r.getDraw();
                    case 2:
                        return r.getRaffle();
                    case 3:
                        return r.getRaffleNumber();
                    case 4:
                        return r.getBet();
                }
                return "";
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        detailTable.setModel(model);

        detailTable.scrollRectToVisible(detailTable.getCellRect(details.size() - 1, 0, false));
        detailTable.changeSelection(details.size() - 1, 0, true, true);

    }

    private void jDialog_updateRemainingBalanceLabel() {

        if (limitLabelUpdater != null) {
            limitLabelUpdater.cancel(true);
        }

        limitLabelUpdater = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    Thread.sleep(256);
                } catch (InterruptedException e) {
                }
                updateBalanceLbl();
                return null;
            }

            @Override
            protected void done() {
                super.done(); //To change body of generated methods, choose Tools | Templates.
                limitLabelUpdater = null;
            }

        };

        limitLabelUpdater.execute();

    }

    private void updateBalanceLbl() {
        int draw = drawMain.getSelectedIndex() + 1;
        String drawStr = drawMain.getSelectedItem().toString();
        String raffle = raffleCombo.getSelectedItem().toString();
        String raffleNumb = raffleNum.getText().trim();
        if (raffleNumb.isEmpty()) {
            jdialog1_balance.setText("N/A");
            return;
        }

        double totalForRaffle = GlobalLimitService.getInstance().getTodaysTotal(drawStr, raffle, raffleNumb);
        GlobalLimit check = GlobalLimitService.getInstance().check(draw, raffle, raffleNumb, null);

        if (check == null) {
            jdialog1_balance.setText("N/A");
            return;
        }

        double globalLimit = check.getLimitValue();
        if (globalLimit == Double.MAX_VALUE) {
            jdialog1_balance.setText("REM: No LIMIT");
        } else {
            jdialog1_balance.setText("REM: " + (globalLimit - totalForRaffle));
        }
    }

    public void updateMainTable() {

        Date d = (Date) globalDateSpinner.getValue();
        final List<ProntonTransaction> trans = ProntonTransactionService.getInstance().search(transactionSearchField.getText(), d);

        double total = 0d;
        List<ProntonTransaction> toRemove = new ArrayList();
        for (ProntonTransaction tran : trans) {
            if (tran.getTotal() < 1) { //remove invalid transactions that were saved.

                List<ProntonTransactionDetail> details1 = ProntonTransactionService.getInstance().getDetails(tran.getId());
                for (ProntonTransactionDetail detail : details1) {
                    ProntonTransactionDetailService.getInstance().remove(detail);
                }

                toRemove.add(tran);

                ProntonTransactionService.getInstance().remove(tran);
                continue;
            }
            total += tran.getTotal();
        }

        trans.removeAll(toRemove);

        mainTotalLbl.setText("Total: " + total);

        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public int getRowCount() {
                return trans.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                ProntonTransaction transaction = trans.get(row);
                switch (column) {
                    case 0:

                        return formatter.format(transaction.getId());
                    case 1:
                        return dformatter.format(transaction.getTransactionTimeStamp());
                    case 2:
                        return transaction.getDraw();
                    case 3:
                        return transaction.getTotal();
                    case 4:
                        if (transaction.getServerResponse() != null) {
                            return transaction.getServerResponse().getMessage();
                        } else {
                            return "N/A";
                        }
                }
                return "";
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Trans Id";
                    case 1:
                        return "Timestamp";
                    case 2:
                        return "Draw";
                    case 3:
                        return "Total Bet";
                    case 4:
                        return "Server Response";
                }
                return "";
            }

        };

        mainTransactionTable.setModel(model);

    }

    private void openTransaction() {
        int selectedRow = mainTransactionTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        Object valueAt = mainTransactionTable.getModel().getValueAt(selectedRow, 0);
        if (valueAt != null) {
            int id = Integer.parseInt(valueAt.toString());
            loadTransaction(id);
        }
    }

    private void loadTransaction(int id) {
        ProntonTransaction found = ProntonTransactionService.getInstance().find(id);
        details = ProntonTransactionService.getInstance().getDetails(id);

        jDialog1.pack();
        jDialog1.setLocationRelativeTo(null);
        jDialog1.setTitle("Transaction " + id + " Draw: " + found.getDraw());

        jDialog1_updateTable();

        raffleCombo.setEnabled(false);
        raffleNum.setEnabled(false);
        betFieldSp.setEnabled(false);
        jdialog1_addBtn.setEnabled(false);

        jDialog1_remove.setEnabled(false);
        jDialog1_save.setEnabled(false);

        jDialog1.setVisible(true);
    }

    List<ProntonTransactionDetail> result;

    private void updateDetailSummTable() {

        if (tabPane.getSelectedIndex() != 1) {
            return;
        }
        String trim = detailSearchField.getText().trim();
        Date d = (Date) globalDateSpinner.getValue();
        result
                = ProntonTransactionDetailService.getInstance()
                .search(detailSearch_draw.getSelectedItem().toString(),
                        detailSearch_raffle.getSelectedItem().toString(),
                        detailSearch_type.getSelectedIndex(),
                        trim,
                        d);

        double total = 0d;
        for (ProntonTransactionDetail dd : result) {
            total += dd.getBet();
        }

        DefaultTableModel m = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Detail ID";
                    case 1:
                        return "Transaction ID";
                    case 2:
                        return "Draw";
                    case 3:
                        return "Raffle";
                    case 4:
                        return "Raffle No.";
                    case 5:
                        return "Bet Amount";
                }
                return "";
            }

            @Override
            public int getRowCount() {
                return result.size();
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public Object getValueAt(int row, int column) {
                ProntonTransactionDetail get = result.get(row);
                switch (column) {
                    case 0:
                        return Integer.toString(get.getId(), 36).toUpperCase();
                    //return formatter.format(get.getId());
                    case 1:
                        return formatter.format(get.getTransaction().getId());
                    case 2:
                        return get.getDraw();
                    case 3:
                        return get.getRaffle();
                    case 4:
                        return get.getRaffleNumber();
                    case 5:
                        return get.getBet();
                }
                return "";
            }

        };
        detailSummTable.setModel(m);
        detailsTotal.setText("Total: " + total);

    }

    private void printTransaction(ProntonTransaction transaction) {
        if (transaction == null) {
            return;
        }

        int paper = SettingsService.getInstance().getSettingsObject().getPrinterPaperSize();

        String reportFile = null;

        if (paper == 1) {
            reportFile = "/com/jps/l2app/reports/ProntonReceipt76.jasper";
        } else if (paper == 2) {
            reportFile = "/com/jps/l2app/reports/ProntonReceipt58.jasper";
        } else {
            UIUtilities.addWarningMessage("Paper size not set. Please go to Setup > Printer.");
            return;
        }

        JasperPrint p = null;
        try {
            ProntonReceiptSource s = new ProntonReceiptSource(transaction);
            p = JasperFillManager.fillReport(ProntonMain_BN.class.getResourceAsStream(reportFile), new HashMap(), s);
            if (showPrintPreview) {
                JasperViewer.viewReport(p);
            } else {
                JasperPrintManager.printReport(p, false);
            }
        } catch (JRException jRException) {
            //  jRException.printStackTrace();
            UIUtilities.addWarningMessage("There was an error in printing. Please check if your printer is connected.\nYou can print this transaction again in the main window.");
        }

    }

    private final DecimalFormat nFormatter = new DecimalFormat("##");

    private void summarySubmit(String draw) {

        boolean dfound = false;
        for (DeviceSpecifications d : Main.DEV_SPECS) {
            dfound = d.getModem() != null;
            if (dfound) {
                break;
            }
        }

        if (!dfound) {
            UIUtilities.addWarningMessage("Device missing or not yet initialized.");
            System.exit(1);
            return;
        }

        String mobile = SettingsService.getInstance().getSettingsObject().getServerMobileNumber();
        if (mobile == null) {
            UIUtilities.addWarningMessage("Server mobile number not sent.");
            return;
        }

        QueuedResponseService.getInstance().clearUnsent(draw);

        List<QueuedResponse> rList = new ArrayList();

        Date date = new Date();

        int index = 0;

        double total = 0d;

        for (String raffle : raffles) {

            List<String> raffleNumbers = ProntonTransactionDetailService.getInstance().getRaffleNumbers(date, draw, raffle);

            StringBuilder message = null;

            for (int i = 0; i < raffleNumbers.size(); i++) {
                String raffleNumber = raffleNumbers.get(i);

                if (message == null) {
                    message = new StringBuilder();
                    message.append(draw).append(".T").append(++index).append(".").append(raffle).append(".");
                }

                double rafNTotal = ProntonTransactionDetailService.getInstance().getRaffleNumberTotal(date, draw, raffle, raffleNumber);
                total += rafNTotal;

                String toAppend = raffleNumber + nFormatter.format(rafNTotal) + ".";
                if (message.length() + toAppend.length() > 158) {
                    message.append("E");
                    rList.add(send(message.toString(), mobile));
                    message = null;
                    i = i - 1;//reset
                } else {
                    message.append(toAppend);
                }

            }

            if (message != null) {
                message.append("E");
                rList.add(send(message.toString(), mobile));
            }
        }

        Main.log(total);
        rList.add(0, send("GROSS_" + draw + "=" + total, mobile));

        SendingProgress p = new SendingProgress(rList);
        p.pack();
        p.setLocationRelativeTo(null);
        p.setVisible(true);

    }

    private QueuedResponse send(String text, String mobile) {

        QueuedResponse re = new QueuedResponse();
        re.setAutoResponse(false);
        re.setEmployee(null);
        re.setMessage(text);
        re.setQueuedDate(new Date());
        re.setSendStatus(QueuedResponse.UNPROCESSED);
        re.setSendTo(RawSMSService.extractEssentialMobileNum(mobile));

        re = QueuedResponseService.getInstance().update(re);
        return re;

    }

    private void printDrawSummary(Date d, String draw) {

        int paper = SettingsService.getInstance().getSettingsObject().getPrinterPaperSize();

        String reportFile = null;

        if (paper == 1) {
            reportFile = "/com/jps/l2app/reports/ProntonSummary76.jasper";
        } else if (paper == 2) {
            reportFile = "/com/jps/l2app/reports/ProntonSummary58.jasper";
        } else {
            UIUtilities.addWarningMessage("Paper size not set. Please go to Setup > Printer.");
            return;
        }

        JasperPrint p = null;
        try {

            ProntonSummarySource src = new ProntonSummarySource(d, draw);
            p = JasperFillManager.fillReport(ProntonMain_BN.class.getResourceAsStream(reportFile), new HashMap(), src);
            JasperViewer.viewReport(p, false);

        } catch (JRException jRException) {
            jRException.printStackTrace();
            UIUtilities.addWarningMessage("There was an error in printing. Please check if your printer is connected.\nYou can print this transaction again in the main window.");
        }

    }

    private void printSearchResults(List<ProntonTransactionDetail> details) {
        String reportFile = "/com/jps/l2app/reports/ProntonSearchResult.jasper";

        int paper = SettingsService.getInstance().getSettingsObject().getPrinterPaperSize();

        if (paper == 1) {
            reportFile = "/com/jps/l2app/reports/ProntonSearchResult76.jasper";
        } else if (paper == 2) {
            reportFile = "/com/jps/l2app/reports/ProntonSearchResult58.jasper";
        } else {
            UIUtilities.addWarningMessage("Paper size not set. Please go to Setup > Printer.");
            return;
        }

        JasperPrint p = null;
        try {

            ProntonSearchResultSource src = new ProntonSearchResultSource(details);

            p = JasperFillManager.fillReport(ProntonMain_BN.class.getResourceAsStream(reportFile), new HashMap(), src);
            if (showPrintPreview) {
                JasperViewer.viewReport(p);
            } else {
                JasperPrintManager.printReport(p, false);
            }

        } catch (JRException jRException) {
            jRException.printStackTrace();
            UIUtilities.addWarningMessage("There was an error in printing. Please check if your printer is connected.\nYou can print this transaction again in the main window.");
        }

    }
}
