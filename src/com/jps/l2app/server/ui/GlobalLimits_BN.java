/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.server.ui;

import com.jps.l2app.entities.ForwardFinancer;
import com.jps.l2app.utils.ListCellRendererImp;
import com.jps.l2app.entities.GlobalLimit;
import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.services.ForwardFinancerService;
import com.jps.l2app.entities.services.GlobalLimitService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.main.Setup;
import com.jps.l2app.main.SystemType;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import com.jps.l2app.utils.UIUtilities;
import java.awt.event.ItemEvent;

/**
 *
 * @author admin
 */
public class GlobalLimits_BN extends javax.swing.JDialog {

    /**
     * Creates new form GlobalLimits
     */
    public GlobalLimits_BN(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        if (Setup.systemType == SystemType.SERVER) {
            financerCombo.removeAllItems();
            ForwardFinancer f1 = new ForwardFinancer();
            f1.setId(-1);
            f1.setName("NONE - Station limits.");
            financerCombo.addItem(f1);

            List<ForwardFinancer> l = ForwardFinancerService.getInstance().getFinancerList();
            for (ForwardFinancer f : l) {
                financerCombo.addItem(f);
            }
        } else {
            financerCombo.getParent().remove(financerCombo);
            jLabel5.getParent().remove(jLabel5);
        }

        refresh();
    }

    public void refresh() {
        String raffle = l1.getSelectedValue();
        if (raffle == null) {
            raffle = "";
        }

        int draw = Integer.parseInt(drawSpinner.getValue().toString());

        ForwardFinancer f = null;
        List<GlobalLimit> all;
        if (financerCombo.getSelectedIndex() != 0) {
            f = (ForwardFinancer) financerCombo.getSelectedItem();
            all = GlobalLimitService.getInstance().getAll(raffle, draw, f);
        } else {
            all = GlobalLimitService.getInstance().getAll(raffle, draw);
        }

        DefaultListModel<GlobalLimit> m = new DefaultListModel();
        for (GlobalLimit globalLimit : all) {
            m.addElement(globalLimit);
        }

        l2.setModel(m);

        jCheckBox1.setSelected(SettingsService.getInstance().getSettingsObject().isS3rDivideBet());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        l2 = new javax.swing.JList<GlobalLimit>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        l1 = new javax.swing.JList<String>();
        jLabel1 = new javax.swing.JLabel();
        drawSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        digitField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        limitField = new javax.swing.JFormattedTextField();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        winPisoSpinner = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        financerCombo = new javax.swing.JComboBox<ForwardFinancer>();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Global Limits");

        l2.setCellRenderer(new ListCellRendererImp()
        );
        l2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                l2ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(l2);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jps/l2app/icons/limit.png"))); // NOI18N
        jLabel11.setText("Limits");

        l1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "S3", "S3R" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        l1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                l1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(l1);

        jLabel1.setText("Raffle Types");

        drawSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 3, 1));
        drawSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                drawSpinnerStateChanged(evt);
            }
        });

        jLabel4.setText("Draw:");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Limit Details"));

        jLabel3.setText("Limit:");

        digitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                digitFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Raffle Digits:");

        jButton5.setMnemonic('s');
        jButton5.setText("Save");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        limitField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        limitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limitFieldActionPerformed(evt);
            }
        });

        jButton3.setMnemonic('d');
        jButton3.setText("Delete");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel6.setText("Win/Piso:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(digitField, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(limitField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(winPisoSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(digitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5)
                    .addComponent(limitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jLabel6)
                    .addComponent(winPisoSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel5.setText("Financer:");

        financerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NONE - Stations Limits" }));
        financerCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                financerComboItemStateChanged(evt);
            }
        });
        financerCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                financerComboActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Divide S3R bet");

        jButton1.setText("Set");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(136, 136, 136))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(drawSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jCheckBox1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(financerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(financerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drawSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void l1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_l1ValueChanged
        refresh();
    }//GEN-LAST:event_l1ValueChanged

    private void l2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_l2ValueChanged

        GlobalLimit v = l2.getSelectedValue();
        if (v == null) {
            digitField.setText("");
            limitField.setValue(0);
            return;
        }
        String rn = v.getRaffleNumber();
        long i;
        try {
            i = Long.parseLong(rn);
        } catch (NumberFormatException n) {
            i = -1;
        }
        digitField.setText(v.getRaffleNumber());
        limitField.setValue(v.getLimitValue());
        winPisoSpinner.setValue(v.getWinPerPiso());

    }//GEN-LAST:event_l2ValueChanged

    private void save() throws NumberFormatException {

        String raffle = l1.getSelectedValue();
        int draw = Integer.parseInt(drawSpinner.getValue().toString());
        if (raffle == null) { //raffle
            return;
        }

        String digit = getEvalDigit(raffle, digitField.getText());
        if (digit == null) {
            return;
        }

        ForwardFinancer f = null;
        try {
            f = (ForwardFinancer) financerCombo.getSelectedItem();
        } catch (Exception e) {
        }

        GlobalLimit limit = l2.getSelectedValue();
        if (limit == null) {
            GlobalLimit check = GlobalLimitService.getInstance().getLimit(draw, raffle, digit, f);
            limit = check == null ? new GlobalLimit() : check;
        }

        limit.setDraw(Integer.parseInt(drawSpinner.getValue().toString()));
        limit.setRaffle(raffle);
        limit.setRaffleNumber(digit);
        limit.setLimitValue(Double.parseDouble(limitField.getValue().toString()));
        limit.setWinPerPiso(Double.parseDouble(winPisoSpinner.getValue().toString()));

        if (Setup.systemType == SystemType.SERVER) {
            if (f.getId() == -1) {
                limit.setFinancer(null);
            } else {
                limit.setFinancer(f);
            }
        } else {
            limit.setFinancer(null);
        }

        GlobalLimitService.getInstance().update(limit);
        UIUtilities.addInfoMessage("Saved");
        refresh();
    }

    private int getExpectedDigits(String raffle) {
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
        return digits;
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        List<GlobalLimit> selection = l2.getSelectedValuesList();
        boolean conf = UIUtilities.getConfirmation("Are you sure you want to delete " + selection.size() + " items?");
        if (!conf) {
            return;
        }

        for (GlobalLimit s : selection) {
            GlobalLimitService.getInstance().remove(s);
        }

        UIUtilities.addInfoMessage("Delete Success!");

        refresh();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void drawSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_drawSpinnerStateChanged
        refresh();
    }//GEN-LAST:event_drawSpinnerStateChanged

    private void digitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_digitFieldActionPerformed
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                limitField.requestFocus();
                limitField.setText(limitField.getText());
                limitField.selectAll();
            }

        });
    }//GEN-LAST:event_digitFieldActionPerformed

    private void limitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limitFieldActionPerformed
        save();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                digitField.requestFocus();
                digitField.setText(digitField.getText());
                digitField.selectAll();
            }

        });
    }//GEN-LAST:event_limitFieldActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        save();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void financerComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_financerComboItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        refresh();

    }//GEN-LAST:event_financerComboItemStateChanged

    private void financerComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_financerComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_financerComboActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Settings o = SettingsService.getInstance().getSettingsObject();
        o.setS3rDivideBet(jCheckBox1.isSelected());
        SettingsService.getInstance().update(o);
        UIUtilities.addInfoMessage("Changes were saved.");
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GlobalLimits_BN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GlobalLimits_BN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GlobalLimits_BN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GlobalLimits_BN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GlobalLimits_BN dialog = new GlobalLimits_BN(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField digitField;
    private javax.swing.JSpinner drawSpinner;
    private javax.swing.JComboBox<ForwardFinancer> financerCombo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> l1;
    private javax.swing.JList<GlobalLimit> l2;
    private javax.swing.JFormattedTextField limitField;
    private javax.swing.JSpinner winPisoSpinner;
    // End of variables declaration//GEN-END:variables

    private String getEvalDigit(String raffle, String digit) {
        digit = digit.toUpperCase();

        int digits = getExpectedDigits(raffle);
        if (digit.equals("ALL")) {
            return digit;
        } else if (digit.startsWith("%") || digit.endsWith("%")) {//starts with/ends with

            try {
                Long.parseLong(digit.replaceAll("%", ""));
            } catch (NumberFormatException e) {
                UIUtilities.addWarningMessage("Invalid raffle number");
                return null;
            }
            return digit;

        } else if (digit.contains("-")) { //range
            String[] split = digit.split("-");
            if (split.length != 2) {
                UIUtilities.addWarningMessage("Invalid raffle number");
                return null;
            }

            try {
                long low = Long.parseLong(split[0]);
                long high = Long.parseLong(split[1]);

                if (low >= high) {
                    UIUtilities.addWarningMessage("Invalid range. Lower bound is greater than upper bound.");
                    return null;
                }

                return digit;
            } catch (NumberFormatException e) {
            }

            UIUtilities.addWarningMessage("Invalid raffle range");

            return null;

        }

        if (digits != digit.length()) {
            UIUtilities.addWarningMessage("Invalid number of digits for raffle number. Expected: " + digits);
            return null;
        }
        try {
            if (Long.parseLong(digit) < 0) {
                return "ALL";
            } else {
                return digit;
            }
        } catch (NumberFormatException e) {
            UIUtilities.addWarningMessage("Invalid raffle number");
        }

        return null;

    }
}
