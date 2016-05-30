/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.EmployeeTotals;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import com.jps.l2app.utils.FormatUtils;
import javax.persistence.TypedQuery;

/**
 *
 * @author org org
 */
public class EmployeeTotalsService extends AbstractEntityService<EmployeeTotals> {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private static EmployeeTotalsService instance;

    public static EmployeeTotalsService getInstance() {
        if (instance == null) {
            instance = new EmployeeTotalsService();
        }
        return instance;
    }

    public EmployeeTotals getEmployeeTotals(Employee employee, String draw) {
        return getEmployeeTotals(employee, draw, new Date());
    }

    public EmployeeTotals getEmployeeTotals(Employee employee, String draw, Date date) {

        date = FormatUtils.getDayStart(date);

        EntityManager m = getNewEntityManager();

        List<EmployeeTotals> rs
                = m.createQuery("SELECT c FROM EmployeeTotals c WHERE c.raffleDate = ?1 AND c.employee.id = ?2 AND c.draw LIKE ?3 ",
                        EmployeeTotals.class)
                .setParameter(1, date)
                .setParameter(2, employee.getId())
                .setParameter(3, draw)
                .getResultList();

        EmployeeTotals totals;

        if (rs.isEmpty()) {

            m.getTransaction().begin();

            totals = new EmployeeTotals();
            totals.setEmployee(m.merge(employee));
            totals.setRaffleDate(date);
            totals.setDraw(draw);
            totals.setL2Total(0d);
            totals.setL3Total(0d);
            totals.setP42Total(0d);

            totals.setP45Total(0d);
            totals.setP49Total(0d);
            totals.setP55Total(0d);
            totals.setS3Total(0d);
            totals.setS2Total(0d);
            totals.setS3rTotal(0d);
            totals = insert(totals);

            m.merge(totals);
            m.getTransaction().commit();

        } else {
            totals = rs.get(0);
        }

        m.close();

        return totals;
    }

    @Override
    public Class getEntityClass() {
        return EmployeeTotals.class;
    }

    public EmployeeTotals getEmpTotals(Submission sub) {
        return getEmployeeTotals(sub.getSender(), sub.getDraw());
    }

    public EmployeeTotals addToEmpTotals(Submission sub) {
        double amount = sub.getBetValue();
        String raffle = sub.getRaffleType();

        EmployeeTotals empTotals = getEmployeeTotals(sub.getSender(), sub.getDraw());
        EntityManager m = getNewEntityManager();
        empTotals = m.merge(empTotals);
        m.refresh(empTotals);
        m.close();

        switch (raffle.trim().toUpperCase()) {
            case "L2":
                empTotals.setL2Total(empTotals.getL2Total() + amount);
                break;
            case "L3":
                empTotals.setL3Total(empTotals.getL3Total() + amount);
                break;
            case "S3":
                empTotals.setS3Total(empTotals.getS3Total() + amount);
                break;
            case "S3R":
                empTotals.setS3rTotal(empTotals.getS3rTotal() + amount);
                break;
            case "S2":
                empTotals.setS2Total(empTotals.getS2Total() + amount);
                break;
            case "P3":
                empTotals.setP3Total(empTotals.getP42Total() + amount);
                break;
            case "P42":
                empTotals.setP42Total(empTotals.getP42Total() + amount);
                break;
            case "P45":
                empTotals.setP45Total(empTotals.getP45Total() + amount);
                break;
            case "P49":
                empTotals.setP49Total(empTotals.getP49Total() + amount);
                break;
            case "P55":
                empTotals.setP55Total(empTotals.getP55Total() + amount);
                break;
            case "4D":
                empTotals.setD4Total(empTotals.getD4Total() + amount);
        }

        return update(empTotals);
    }

    public List<EmployeeTotals> queryEmpTotals(String draw, String e, Date start, Date end) {
        EntityManager m = getNewEntityManager();

        if (draw.toLowerCase().matches("all")) {
            draw = "";
        }

        List<EmployeeTotals> rs = new ArrayList<>();

        try {
            rs = m.createQuery("SELECT c FROM EmployeeTotals c WHERE (c.employee.shortName LIKE ?1 OR c.employee.stationArea LIKE ?1) AND (c.raffleDate >= ?3 AND c.raffleDate <= ?4)  AND c.draw LIKE ?2 ORDER BY c.raffleDate, c.employee.shortName, c.draw", EmployeeTotals.class)
                    .setParameter(1, "%" + e + "%")
                    .setParameter(2, "%" + draw + "%")
                    .setParameter(3, start)
                    .setParameter(4, end)
                    .getResultList();

            m.getTransaction().begin();
            for (EmployeeTotals empTotals : rs) {

                if (empTotals.isNeedsRefresh()) {
                    empTotals.setD4Total(0);
                    empTotals.setGross(0);
                    empTotals.setL2Total(0);
                    empTotals.setL3Total(0);
                    empTotals.setP3Total(0);
                    empTotals.setP42Total(0);
                    empTotals.setP45Total(0);
                    empTotals.setP49Total(0);
                    empTotals.setP55Total(0);
                    empTotals.setS2Total(0);
                    empTotals.setS3Total(0);
                    empTotals.setS3rTotal(0);

                    List<Submission> submissions = getSubmissions(empTotals);
                    for (Submission sub : submissions) {
                        double amount = sub.getBetValue();
                        String raffle = sub.getRaffleType();
                        switch (raffle.trim().toUpperCase()) {
                            //<editor-fold defaultstate="collapsed" desc="det">
                            case "L2":
                                empTotals.setL2Total(empTotals.getL2Total() + amount);
                                break;
                            case "L3":
                                empTotals.setL3Total(empTotals.getL3Total() + amount);
                                break;
                            case "S3":
                                empTotals.setS3Total(empTotals.getS3Total() + amount);
                                break;
                            case "S3R":
                                empTotals.setS3rTotal(empTotals.getS3rTotal() + amount);
                                break;
                            case "S2":
                                empTotals.setS2Total(empTotals.getS2Total() + amount);
                                break;
                            case "P3":
                                empTotals.setP3Total(empTotals.getP3Total() + amount);
                                break;
                            case "P42":
                                empTotals.setP42Total(empTotals.getP42Total() + amount);
                                break;
                            case "P45":
                                empTotals.setP45Total(empTotals.getP45Total() + amount);
                                break;
                            case "P49":
                                empTotals.setP49Total(empTotals.getP49Total() + amount);
                                break;
                            case "P55":
                                empTotals.setP55Total(empTotals.getP55Total() + amount);
                                break;
                            case "4D":
                                empTotals.setD4Total(empTotals.getD4Total() + amount);
                            //</editor-fold>
                        }
                    }
                    empTotals.updateGross();
                    
                    empTotals.setNeedsRefresh(false);
                }

            }
            m.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        m.close();
        return rs;
    }

    private List<Submission> getSubmissions(EmployeeTotals e) {
        EntityManager m = getNewEntityManager();
        List<Submission> rs = m.createQuery("SELECT c FROM Submission c WHERE c.employeeTotals.id = ?1", Submission.class).setParameter(1, e.getId()).getResultList();
        m.close();
        return rs;
    }

    public void removeEmpTotals(Employee toRemove) {
        EntityManager m = getNewEntityManager();
        List<EmployeeTotals> rs = m.createQuery("SELECT c FROM EmployeeTotals c WHERE c.employee.id = ?1", EmployeeTotals.class).setParameter(1, toRemove.getId()).getResultList();
        if (!rs.isEmpty()) {
            m.getTransaction().begin();
        }
        for (EmployeeTotals r : rs) {
            m.remove(r);
        }
        m.getTransaction().commit();
        m.close();
    }

}
