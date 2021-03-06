/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.server.ui;

import com.jps.l2app.entities.DigitTotal;
import com.jps.l2app.entities.services.DigitTotalService;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author admin
 */
public class RaffleDigitTotalTable extends javax.swing.JPanel {

    /**
     * Creates new form DigitTotalTable
     */
    public RaffleDigitTotalTable() {
        initComponents();
    }
    
    /**
     *
     * @param date
     * @param draw - if null then all
     * @param raffle - if null then all
     * @param orderByBet -
     * @param key
     */
    public void displayrafDigitTotal(Date date, String draw, String raffle, boolean orderByBet, String key) {
        final List<DigitTotal> totals = DigitTotalService.getInstance().getAll(date, draw, raffle, orderByBet, key);
        DefaultTableModel m = new DefaultTableModel() {

            private String[] cols = new String[]{
                "#", "Draw", "Raffle", "Digit", "Detail", "Total", "Forwarded", "Available"
            };

            @Override
            public int getColumnCount() {
                return cols.length;
            }

            @Override
            public String getColumnName(int column) {
                return cols[column];
            }

            @Override
            public int getRowCount() {
                return totals.size();
            }

            @Override
            public Object getValueAt(int row, int column) {
                DigitTotal t = totals.get(row);
                switch (column) {
                    case 0:
                        return row + 1;
                    case 1:
                        return t.getDraw();
                    case 2:
                        return t.getRaffleType();
                    case 3:
                        return t.getRaffleDigit();
                    case 4:
                        return t.getDetail();
                    case 5:
                        return t.getTotal();
                    case 6:
                        return t.getForwarded();
                    case 7:
                        return t.getTotal() - t.getForwarded();

                }
                return "n/a";
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        mainTable.setModel(m);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mainTable = new javax.swing.JTable();

        mainTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Draw", "Raffle", "Digit", "Detail", "Total", "Forwarded", "Available"
            }
        ));
        jScrollPane1.setViewportView(mainTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable mainTable;
    // End of variables declaration//GEN-END:variables
}
