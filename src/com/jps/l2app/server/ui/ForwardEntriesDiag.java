/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.server.ui;

import com.jps.l2app.entities.ForwardFinancer;
import com.jps.l2app.entities.GlobalLimit;
import com.jps.l2app.entities.services.ForwardFinancerService;
import com.jps.l2app.entities.services.GlobalLimitService;
import com.jps.l2app.utils.UIUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author admin
 */
public class ForwardEntriesDiag extends javax.swing.JDialog {

    List<ForwardEntriesDiagData> masterData = new ArrayList<>();
    List<ForwardFinancer> financers = ForwardFinancerService.getInstance().getFinancerList();

    public ForwardEntriesDiag(List<ForwardEntriesDiagData> data) {
        super();

        masterData.addAll(data);

        initComponents();
        setModal(true);

        refreshTable();

    }

    private void refreshTable() {
        DefaultTableModel model;
        model = new DefaultTableModel() {

            int lastColIndex = financers.size() + 3;

            @Override
            public boolean isCellEditable(int row, int column) {

                if (column == lastColIndex) {
                    return false;
                } else if (column >= 3) {
                    return true;
                }

                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3) {
                    return Double.class;
                }
                return Object.class;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {

                if (column < 3 || column == lastColIndex) {
                    return;
                }

                double newValue = Double.parseDouble(aValue.toString());

                int fi = column - 3;

                ForwardFinancer f = financers.get(fi);
                ForwardEntriesDiagData d = masterData.get(row);
                double limit = Double.MAX_VALUE;
                GlobalLimit l = GlobalLimitService.getInstance().check(0, d.getRaffle(), d.getRaffleNumber(), f);
                if (l != null) {
                    limit = l.getLimitValue();
                }

                if (newValue > limit) {
                    UIUtilities.addWarningMessage("Limit reached. Max value is " + limit);
                    return;
                }

                switch (fi) {
                    case 0:
                        d.setFinancer1(newValue);
                        break;
                    case 1:
                        d.setFinancer2(newValue);
                        break;
                    case 2:
                        d.setFinancer3(newValue);
                        break;
                    case 3:
                        d.setFinancer4(newValue);
                        break;
                    case 4:
                        d.setFinancer5(newValue);
                        break;
                }

                this.fireTableDataChanged();
            }

            @Override
            public int getColumnCount() {
                return financers.size() + 4;
            }

            @Override
            public int getRowCount() {
                return masterData.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                ForwardEntriesDiagData d = masterData.get(row);

                if (column == lastColIndex) {
                    return d.getAvailable();
                }

                switch (column) {
                    case 0:
                        return d.getRaffle();
                    case 1:
                        return d.getRaffleNumber();

                    case 2:
                        return d.getAmount();
                }

                int fi = column - 3;
                ForwardFinancer f = financers.get(fi);
                //double limit = GlobalLimitService.getInstance().check(d.getRaffle(), d.getRaffleNumber(), f).getLimitValue();
                double fv = 0d;
                switch (fi) {
                    case 0:
                        fv = d.getFinancer1();
                        break;
                    case 1:
                        fv = d.getFinancer2();
                        break;
                    case 2:
                        fv = d.getFinancer3();
                        break;
                    case 3:
                        fv = d.getFinancer4();
                        break;
                    case 4:
                        fv = d.getFinancer5();
                        break;
                }

                return fv;
            }

            @Override
            public String getColumnName(int column) {
                if (column == lastColIndex) {
                    return "Available";
                }

                switch (column) {
                    case 0:
                        return "Raffle";
                    case 1:
                        return "Raffle Number";
                    case 2:
                        return "Amount";

                }

                return financers.get(column - 3).getName();

            }

        };
        mainTable.setModel(model);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mainTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Raffle", "Raffle Number", "Amount", "Financer1", "Financer2", "Financer3", "Available"
            }
        ));
        jScrollPane1.setViewportView(mainTable);

        jLabel2.setText("Forwarding Entries:");

        jButton2.setText("Submit and Finalize");

        jButton3.setText("AutoForward All");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        double avail;
        double value;
        double limit;
        GlobalLimit l;
        ForwardFinancer fin;

        for (ForwardEntriesDiagData d : masterData) {
            for (int i = 0; i < financers.size(); i++) {

                fin = financers.get(i);
                limit = Double.MAX_VALUE;

                l = GlobalLimitService.getInstance().check(0, d.getRaffle(), d.getRaffleNumber(), fin);
                if (l != null) {
                    limit = l.getLimitValue();
                }

                value = 0d;
                avail = d.getAvailable();

                if (avail >= limit) {
                    value = limit;
                } else {
                    value = avail;
                }

                switch (i) {
                    case 0:
                        d.setFinancer1(value);
                        break;
                    case 1:
                        d.setFinancer2(value);
                        break;
                    case 2:
                        d.setFinancer3(value);
                        break;
                    case 3:
                        d.setFinancer4(value);
                        break;
                    case 4:
                        d.setFinancer5(value);
                }

            }
        }

        mainTable.repaint();

    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable mainTable;
    // End of variables declaration//GEN-END:variables

    public static class ForwardEntriesDiagData implements Comparable<ForwardEntriesDiagData> {

        private String raffle;
        private String raffleNumber;
        private double amount = 0;
        private double financer1 = 0;
        private double financer2 = 0;
        private double financer3 = 0;
        private double financer4 = 0;
        private double financer5 = 0;

        public String getRaffle() {
            return raffle;
        }

        public void setRaffle(String raffle) {
            this.raffle = raffle;
        }

        public String getRaffleNumber() {
            return raffleNumber;
        }

        public void setRaffleNumber(String raffleNumber) {
            this.raffleNumber = raffleNumber;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        @Override
        public int compareTo(ForwardEntriesDiagData o) {
            return Integer.parseInt(this.raffleNumber) - Integer.parseInt(o.getRaffleNumber());
        }

        public double getFinancer1() {
            return financer1;
        }

        public void setFinancer1(double financer1) {
            this.financer1 = financer1;
        }

        public double getFinancer2() {
            return financer2;
        }

        public void setFinancer2(double financer2) {
            this.financer2 = financer2;
        }

        public double getFinancer3() {
            return financer3;
        }

        public void setFinancer3(double financer3) {
            this.financer3 = financer3;
        }

        public double getFinancer4() {
            return financer4;
        }

        public void setFinancer4(double financer4) {
            this.financer4 = financer4;
        }

        public double getFinancer5() {
            return financer5;
        }

        public void setFinancer5(double financer5) {
            this.financer5 = financer5;
        }

        public double getAvailable() {
            return amount - financer1 - financer2 - financer3 - financer4 - financer5;
        }

    }

}
