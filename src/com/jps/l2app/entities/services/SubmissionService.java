/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.EmployeeTotals;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import com.jps.l2app.utils.FormatUtils;

/**
 *
 * @author org org
 */
public class SubmissionService extends AbstractEntityService<Submission> {

    private static SubmissionService instance;

    public static SubmissionService getInstance() {
        if (instance == null) {
            instance = new SubmissionService();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return Submission.class;
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
        String format = s.format(new java.util.Date());
        Date today = s.parse(format);
        Main.log(today);
    }

  

    public List<Submission> querySubmissions(String draw, String raffle, Employee e, Date start, Date end, String raffleDigits) {
        raffleDigits = raffleDigits.trim();

        if (draw.toLowerCase().matches("all")) {
            draw = "";
        }

        if (raffle.toLowerCase().matches("all")) {
            raffle = "";
        }

        String query;
        List<Submission> normalResults = new ArrayList<Submission>();

        EntityManager m = getNewEntityManager();

        try {

            //<editor-fold defaultstate="collapsed" desc="normal">
            if (e.toString().toLowerCase().matches("all")) {
                query = "SELECT c FROM Submission c "
                        + "WHERE c.draw LIKE ?1 "
                        + "AND c.raffleType LIKE ?2 "
                        + "AND (c.receivedDate >= ?3 "
                        + "AND c.receivedDate <= ?4) "
                        + "AND c.raffleType NOT LIKE '%r%' "
                        + "AND c.raffleNumber LIKE ?5 "
                        + "ORDER BY c.receivedDate DESC, c.draw, c.raffleType, c.raffleNumber";
                normalResults = m.createQuery(query, Submission.class)
                        .setParameter(1, "%" + draw + "%")
                        .setParameter(2, "%" + raffle + "%")
                        .setParameter(3, start)
                        .setParameter(4, end)
                        .setParameter(5, "%" + raffleDigits + "%")
                        .getResultList();

            } else {
                query = "SELECT c FROM Submission c "
                        + "WHERE c.draw LIKE ?1 "
                        + "AND c.raffleType LIKE ?2 "
                        + "AND c.sender.id = ?3 "
                        + "AND (c.receivedDate >= ?4 "
                        + "AND c.receivedDate <= ?5) "
                        + "AND c.raffleType NOT LIKE '%r%' "
                        + "AND c.raffleNumber LIKE ?6 "
                        + "ORDER BY c.receivedDate DESC, c.draw, c.raffleType, c.raffleNumber";
                normalResults = m.createQuery(query, Submission.class)
                        .setParameter(1, "%" + draw + "%")
                        .setParameter(2, "%" + raffle + "%")
                        .setParameter(3, e.getId())
                        .setParameter(4, start)
                        .setParameter(5, end)
                        .setParameter(6, "%" + raffleDigits + "%")
                        .getResultList();
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="rambolito">
            char[] toCharArray = raffleDigits.toCharArray();

            StringBuilder toApp = new StringBuilder();
            for (int i = 0; i < toCharArray.length; i++) {
                toApp.append("AND c.raffleNumber LIKE ?").append(i + 10).append(" ");
            }

            List<Submission> rnormalResults;
            if (e.toString().toLowerCase().matches("all")) {

                query = "SELECT c FROM Submission c "
                        + "WHERE c.draw LIKE ?1 "
                        + "AND c.raffleType LIKE ?2 "
                        + "AND (c.receivedDate >= ?3 "
                        + "AND c.receivedDate <= ?4) "
                        + "AND c.raffleType LIKE '%r%' "
                        + toApp.toString()
                        + "ORDER BY c.receivedDate DESC, c.draw, c.raffleType, c.raffleNumber";

                TypedQuery<Submission> queryRw = m.createQuery(query, Submission.class)
                        .setParameter(1, "%" + draw + "%")
                        .setParameter(2, "%" + raffle + "%")
                        .setParameter(3, start)
                        .setParameter(4, end);

                for (int i = 0; i < toCharArray.length; i++) {
                    char c = toCharArray[i];
                    queryRw.setParameter(10 + i, "%" + c + "%");
                }

                rnormalResults = queryRw.getResultList();

            } else {

                query = "SELECT c FROM Submission c "
                        + "WHERE c.draw LIKE ?1 "
                        + "AND c.raffleType LIKE ?2 "
                        + "AND c.sender.id = ?3 "
                        + "AND (c.receivedDate >= ?4 "
                        + "AND c.receivedDate <= ?5) "
                        + "AND c.raffleType LIKE '%r%' "
                        + toApp.toString()
                        + "ORDER BY c.receivedDate DESC, c.draw, c.raffleType, c.raffleNumber";

                TypedQuery<Submission> queryRw = m.createQuery(query, Submission.class)
                        .setParameter(1, "%" + draw + "%")
                        .setParameter(2, "%" + raffle + "%")
                        .setParameter(3, e.getId())
                        .setParameter(4, start)
                        .setParameter(5, end);

                for (int i = 0; i < toCharArray.length; i++) {
                    char c = toCharArray[i];
                    queryRw.setParameter(10 + i, "%" + c + "%");
                }

                rnormalResults = queryRw.getResultList();

            }
            //</editor-fold>

            normalResults.addAll(rnormalResults);
        } catch (Exception ex) {
        }

        m.close();
        return normalResults;
    }

    public List<SubmissionRaw> querySubmissionsRaw(String draw, Employee e, Date start, Date end, String key) {
        EntityManager m = getNewEntityManager();

        if (draw.toLowerCase().matches("all")) {
            draw = "";
        }

        List<SubmissionRaw> rs = new ArrayList<>();

        try {
            String query = "SELECT c FROM SubmissionRaw c "
                    + "WHERE c.draw LIKE ?1 "
                    + "AND c.submissionId LIKE ?6 "
                    + "AND c.sender.id = ?3 "
                    + "AND (c.receivedDate >= ?4 "
                    + "AND c.receivedDate <= ?5) "
                    + "ORDER BY c.receivedDate DESC, "
                    + "c.draw";

            rs = m.createQuery(query, SubmissionRaw.class)
                    .setParameter(1, "%" + draw + "%")
                    .setParameter(3, e.getId())
                    .setParameter(4, start)
                    .setParameter(5, end)
                    .setParameter(6, "%" + key.trim() + "%")
                    .getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        m.close();
        return rs;
    }

    public List<SubmissionRaw> querySubmissionsRaw(String draw, int show, Date start, Date end, String key) {
        EntityManager m = getNewEntityManager();

        if (draw.toLowerCase().matches("all")) {
            draw = "";
        }

        List<SubmissionRaw> rs = new ArrayList<>();

        try {
            String query = "SELECT c FROM SubmissionRaw c "
                    + "WHERE c.draw LIKE ?1 "
                    + "AND c.details LIKE ?6 ";

            if (show > 0) {
                query += "AND c.printed = ?3 ";
            }

            query = query
                    + "AND (c.receivedDate >= ?4 "
                    + "AND c.receivedDate <= ?5) "
                    + "ORDER BY c.receivedDate DESC, "
                    + "c.draw";

            TypedQuery<SubmissionRaw> q = m.createQuery(query, SubmissionRaw.class)
                    .setParameter(1, "%" + draw + "%");

            if (show > 0) {
                q.setParameter(3, show == 2);
            }

            rs = q.setParameter(4, start)
                    .setParameter(5, end)
                    .setParameter(6, "%" + key.trim() + "%")
                    .getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        m.close();
        return rs;
    }

    /**
     * Removes from employee totals too
     *
     * @param emp
     * @param subId
     * @param draw
     * @param date
     * @return
     */
    public String removePreviousProntonSubmissions(Employee emp, String subId, String draw, Date date) {//for isPronton only!
        EntityManager m = getNewEntityManager();
        List<Submission> resultList = m.createQuery("SELECT c FROM Submission c WHERE c.sender.id = ?5 AND c.submissionId LIKE ?1 AND c.draw LIKE ?2 AND c.receivedDate > ?3 AND c.receivedDate < ?4", Submission.class)
                .setParameter(1, subId)
                .setParameter(2, draw)
                .setParameter(3, FormatUtils.getDayStart(date))
                .setParameter(4, FormatUtils.getDayEnd(date))
                .setParameter(5, emp.getId())
                .getResultList();
        m.close();

        //assuming that each submission will only have 1 type of raffletype
        if (!resultList.isEmpty()) {
            String raffType = resultList.get(0).getRaffleType();
            for (Submission s : resultList) {
                remove(s);
            }
            return raffType;
        }
        
        return null;

    }

    public List<Submission> querySubmissions(SubmissionRaw s) {

        Date start = FormatUtils.getDayStart(s.getReceivedDate());
        Date end = FormatUtils.getDayEnd(s.getReceivedDate());

        EntityManager m = getNewEntityManager();

        List<Submission> submits = m.createQuery("SELECT c FROM Submission c WHERE c.draw LIKE ?5 AND c.receivedDate > ?1 AND c.receivedDate < ?2 AND c.submissionId LIKE ?3 AND c.sender.id =  ?4 ORDER BY c.raffleNumber", Submission.class)
                .setParameter(1, start)
                .setParameter(2, end)
                .setParameter(3, s.getSubmissionId())
                .setParameter(4, s.getSender().getId())
                .setParameter(5, s.getDraw())
                .getResultList();
        m.close();

        return submits;

    }

    public List<Submission> querySubmissions(EmployeeTotals s) {
        EntityManager m = getNewEntityManager();

        List<Submission> submits = m.createQuery("SELECT c FROM Submission c WHERE c.employeeTotals.id = ?1 ORDER BY c.raffleNumber", Submission.class)
                .setParameter(1, s.getId())
                .getResultList();
        m.close();

        return submits;

    }


}
