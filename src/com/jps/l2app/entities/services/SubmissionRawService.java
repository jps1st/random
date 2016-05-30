/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import java.util.Date;
import javax.persistence.EntityManager;
import com.jps.l2app.utils.FormatUtils;
import java.util.List;

/**
 *
 * @author org org
 */
public class SubmissionRawService extends AbstractEntityService<SubmissionRaw> {

    @Override
    public Class getEntityClass() {
        return SubmissionRaw.class;
    }

    private static SubmissionRawService instance;

    public static SubmissionRawService getInstance() {
        if (instance == null) {
            instance = new SubmissionRawService();
            // instance.init();
        }
        return instance;
    }

    public boolean alreadySubmitted(String draw, String subno, String mobile, Date d) {

        EntityManager m = getNewEntityManager();
        Date today = FormatUtils.getDayStart(d);

        long l = m.createQuery("SELECT COUNT(c) FROM SubmissionRaw c WHERE c.sender.mobileNumber like ?1 AND c.draw LIKE ?2 AND c.submissionId LIKE ?3 AND c.receivedDate > ?4", Long.class)
                .setParameter(1, "%" + mobile + "")
                .setParameter(2, draw)
                .setParameter(3, subno)
                .setParameter(4, today)
                .getSingleResult();

        boolean already = l > 0;
        m.close();
        return already;
    }

    public void removeSubmissionsRaw(Employee emp, String subno, String draw, Date date) {
        EntityManager m = getNewEntityManager();
        m.getTransaction().begin();
        m.createQuery("DELETE FROM SubmissionRaw c WHERE c.sender.id = ?5 AND c.submissionId LIKE ?1 AND c.draw LIKE ?2 AND c.receivedDate > ?3 AND c.receivedDate < ?4")
                .setParameter(1, subno)
                .setParameter(2, draw)
                .setParameter(3, FormatUtils.getDayStart(date))
                .setParameter(4, FormatUtils.getDayEnd(date))
                .setParameter(5, emp.getId())
                .executeUpdate();

        m.getTransaction().commit();
    }

    public void removeSubmissionRaw(SubmissionRaw s) {
        EntityManager m = getNewEntityManager();
        m.getTransaction().begin();
        List<Submission> rs = m.createQuery("SELECT c FROM Submission c WHERE c.submissionRaw.id = ?1", Submission.class).setParameter(1, s.getId()).getResultList();
        for (Submission r : rs) {
            m.remove(r);
        }
        m.remove(m.merge(s));
        m.getTransaction().commit();
        m.close();
    }

    void removeSubmissionRawByEmp(Employee emp) {
        List<SubmissionRaw> raws = getAllSubmissionRaw(emp);
        for (SubmissionRaw raw : raws) {
            removeSubmissionRaw(raw);
        }
    }

    private List<SubmissionRaw> getAllSubmissionRaw(Employee emp) {
        EntityManager m = getNewEntityManager();
        List<SubmissionRaw> rs = m.createQuery("SELECT c FROM SubmissionRaw c WHERE c.sender.id = ?1", SubmissionRaw.class).setParameter(1, emp.getId()).getResultList();
        m.close();
        return rs;
    }

}
