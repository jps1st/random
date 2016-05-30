/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.DigitTotal;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.utils.FormatUtils;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class DigitTotalService extends AbstractEntityService<DigitTotal> {

    private static DigitTotalService instance;

    public static DigitTotalService getInstance() {
        if (instance == null) {
            instance = new DigitTotalService();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return DigitTotal.class;
    }

    public DigitTotal getDigitTotals(Submission submission) {

        Date date = FormatUtils.getDayStart(submission.getReceivedDate());

        EntityManager m = getNewEntityManager();

        List<DigitTotal> rs
                = m.createQuery("SELECT c FROM DigitTotal c WHERE c.raffleDate = ?1 AND c.draw LIKE ?2 AND c.raffleType LIKE ?3 AND c.raffleDigit LIKE ?4 ",
                        DigitTotal.class)
                .setParameter(1, date)
                .setParameter(2, submission.getDraw())
                .setParameter(3, submission.getRaffleType())
                .setParameter(4, submission.getRaffleNumber())
                .getResultList();

        DigitTotal dt;
        
        if (rs.isEmpty()) {

            m.getTransaction().begin();
            dt = new DigitTotal();
            dt.setDraw(submission.getDraw());
            dt.setRaffleDate(date);
            dt.setRaffleType(submission.getRaffleType());
            dt.setRaffleDigit(submission.getRaffleNumber());
            dt = m.merge(dt);
            m.getTransaction().commit();
            

        } else {
            dt = rs.get(0);
        }
        
        m.close();

        return dt;
    }

    public void updateDetails(Integer id) {
        List<Submission> subs = getSubmissions(id);
        double total = 0d;

        StringBuilder detailBuilder = new StringBuilder();
        String esn = null;
        double esnTotal = 0d;
        for (Submission sub : subs) {

            total += sub.getBetValue();

            String sn;
            if (sub.getSender() == null) {
                sn = "n/a";
            } else {
                sn = sub.getSender().getShortName();
            }

            if (esn == null || !esn.equals(sn)) {

                if (esn != null) {
                    detailBuilder.append(esn).append("(").append(esnTotal).append("), ");
                }

                esn = sn;
                esnTotal = sub.getBetValue();

            } else if (esn.equals(sn)) {
                esnTotal += sub.getBetValue();
            }
        }

        if (esn != null) {
            detailBuilder.append(esn).append("(").append(esnTotal).append("), ");
        }

        DigitTotal found = find(id);
        found.setTotal(total);
        found.setDetail(detailBuilder.toString());
        update(found);
    }

    private List<Submission> getSubmissions(Integer id) {
        EntityManager m = getNewEntityManager();
        List<Submission> subs = m.createQuery("SELECT c FROM Submission c WHERE c.raffleDigitTotal.id = ?1 ORDER BY c.sender", Submission.class).setParameter(1, id).getResultList();
        for (Submission sub : subs) {
            m.refresh(sub);
        }
        m.close();
        return subs;
    }

    public List<DigitTotal> getAll(Date date, String draw, String raffle, boolean orderByBet, String key) {

        if (draw == null) {
            draw = "%%";
        } else if (raffle == null) {
            raffle = "%%";
        }

        EntityManager m = getNewEntityManager();
        date = FormatUtils.getDayStart(date);
        String oc = orderByBet ? "ORDER BY c.raffleType, c.total DESC" : "ORDER BY c.raffleType, c.raffleDigit ASC";
        List<DigitTotal> rs = m.createQuery("SELECT c FROM DigitTotal c WHERE c.raffleDate = ?1 AND c.draw LIKE ?2 AND c.raffleType LIKE ?3 AND c.raffleDigit LIKE ?4 " + oc, DigitTotal.class)
                .setParameter(1, date)
                .setParameter(2, draw)
                .setParameter(3, raffle)
                .setParameter(4, "%" + key + "%")
                .getResultList();
        m.close();
        return rs;

    }

}
