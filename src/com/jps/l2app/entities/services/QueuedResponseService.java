/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class QueuedResponseService extends AbstractEntityService<QueuedResponse> {

    private static QueuedResponseService instance;

    public static QueuedResponseService getInstance() {
        if (instance == null) {
            instance = new QueuedResponseService();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return Submission.class;
    }

    public QueuedResponse sendReadySMS(String senderNum) {
        QueuedResponse qr = new QueuedResponse();
        qr.setAutoResponse(true);
        qr.setMessage("TRP service ready.");
        qr.setSendStatus(QueuedResponse.UNPROCESSED);
        qr.setQueuedDate(new Date());
        qr.setSendTo(RawSMSService.extractEssentialMobileNum(senderNum));
        qr.setEmployee(EmployeeService.getInstance().getEmployeeWithMobileNumber(senderNum));
        return insert(qr);
    }

    public List<QueuedResponse> getUnExpiredQueuedResponses(boolean autoRespond) {
        EntityManager m = getNewEntityManager();

        List<QueuedResponse> rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.sendStatus = 0 AND c.autoResponse = ?1", QueuedResponse.class).setParameter(1, autoRespond).getResultList();
        List<QueuedResponse> toRemove = new ArrayList<>();
        Date now = new Date();

        m.getTransaction().begin();

        for (QueuedResponse qr : rs) {

            m.refresh(qr);

            long dur = now.getTime() - qr.getQueuedDate().getTime();
            if (dur > (1000 * 60 * 60 * 1)) { //expire after 1h

                qr.setSendStatus(QueuedResponse.EXPIRED); //expired
                toRemove.add(qr);

            } else {
                qr.setSendStatus(QueuedResponse.PENDING); //expired
            }

        }
        
        m.getTransaction().commit();

        if (!toRemove.isEmpty()) {
            Main.log("Warning:" + toRemove.size() + " responses expired. Responder module might be offline.");
        }

        rs.removeAll(toRemove);
        m.close();
        return rs;
    }

    public QueuedResponse sendNoEndError(String sender, String submitHeader) {
        QueuedResponse qr = new QueuedResponse();
        qr.setAutoResponse(true);
        qr.setMessage(submitHeader + " Error: end not found.");
        qr.setSendStatus(QueuedResponse.UNPROCESSED);
        qr.setQueuedDate(new Date());
        String senderNo = RawSMSService.extractEssentialMobileNum(sender);
        qr.setSendTo(senderNo);
        qr.setEmployee(EmployeeService.getInstance().getEmployeeWithMobileNumber(sender));
        return insert(qr);
    }

    public List<QueuedResponse> getSentToEmp(Employee e, Date start, Date end, String key) {
        EntityManager m = getNewEntityManager();
        List<QueuedResponse> rs;
        if (e.getShortName().equalsIgnoreCase("unregistered")) {
            rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.queuedDate >= :start AND c.queuedDate <= :end AND c.employee IS NULL AND c.message LIKE :key ORDER BY c.id", QueuedResponse.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .setParameter("key", "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        } else if (e.getShortName().equalsIgnoreCase("all")) {
            rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.queuedDate >= :start AND c.queuedDate <= :end AND c.message LIKE :key ORDER BY c.id", QueuedResponse.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .setParameter("key", "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        } else {
            rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.queuedDate >= :start AND c.queuedDate <= :end AND c.employee.id = :empNo AND c.message LIKE :key ORDER BY c.id", QueuedResponse.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .setParameter("empNo", e.getId())
                    .setParameter("key", "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        }

        for (QueuedResponse r : rs) {
            m.refresh(r);
        }

        m.close();
        return rs;
    }

    public void resendPendingSubmissions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void clearUnsent(String draw) {

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date();
        try {
            d1 = f.parse(f.format(new Date()));
        } catch (ParseException ex) {
            Logger.getLogger(QueuedResponseService.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManager m = getNewEntityManager();
        List<QueuedResponse> rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.queuedDate > ?1 AND c.queuedDate < ?2", QueuedResponse.class)
                .setParameter(1, d1)
                .setParameter(2, new Date()).getResultList();

        m.close();

        int count = 0;

        for (QueuedResponse r : rs) {
            boolean startsWith = r.getMessage().startsWith(draw);
            if (startsWith) {
                remove(r);
                count++;
            }
        }

        Main.log("Cleared: " + count);

    }

    public void removeResponses(Employee toRemove) {
        EntityManager m = getNewEntityManager();
        List<QueuedResponse> rs = m.createQuery("SELECT c FROM QueuedResponse c WHERE c.employee.id = ?1", QueuedResponse.class).setParameter(1, toRemove.getId()).getResultList();
        if (!rs.isEmpty()) {
            m.getTransaction().begin();
        }
        for (QueuedResponse r : rs) {
            m.remove(r);
        }
        m.getTransaction().commit();
        m.close();
    }

}
