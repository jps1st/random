/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import com.jps.l2app.utils.UIUtilities;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class EmployeeService extends AbstractEntityService<Employee> {

    private static EmployeeService instance;

    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
            // instance.init();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return Employee.class;
    }

    public List<Employee> getAllowedEmployees() {
        Main.log("Allowed: " + Setup.users);
        EntityManager m = getNewEntityManager();
        List<Employee> rs = m.createQuery("SELECT c FROM Employee c ORDER BY c.shortName", Employee.class).setMaxResults(Setup.users).getResultList();

        for (Employee r : rs) {
            m.refresh(r);
        }
        m.close();
        return rs;
    }

    public List<Employee> getAllEmployees() {
        EntityManager m = getNewEntityManager();
        List<Employee> rs = m.createQuery("SELECT c FROM Employee c ORDER BY c.shortName", Employee.class).getResultList();
        
        if (rs.size() > Setup.users) {
            UIUtilities.addWarningMessage("The population of employees(" + rs.size() + ") exceeds the expected number(" + Setup.users + ").\nPlease remove excess employees to avoid performance issues.");
        }

        for (Employee r : rs) {
            m.refresh(r);
        }
        
        m.close();
        return rs;
    }

    public long getEmployeeCount() {
        EntityManager m = getNewEntityManager();
        Long rs = m.createQuery("SELECT COUNT(c) FROM Employee c ORDER BY c.shortName", Long.class).getSingleResult();
        m.close();
        return rs;
    }

    public boolean checkShortName(String shortName) {
        EntityManager m = getNewEntityManager();
        Long r = m.createQuery("SELECT COUNT(c) FROM Employee c WHERE c.shortName LIKE ?1", Long.class).setParameter(1, shortName).getSingleResult();
        m.close();
        return r > 0;

    }

    public Employee getEmployee(String mobileNumber) {
        mobileNumber = RawSMSService.extractEssentialMobileNum(mobileNumber);
        EntityManager m = getNewEntityManager();

        List<Employee> rs = m.createQuery("SELECT c FROM Employee c WHERE c.mobileNumber LIKE ?1", Employee.class)
                .setParameter(1, "%" + mobileNumber)
                .getResultList();

        m.close();
        if (!rs.isEmpty()) {
            return rs.get(0);
        }
        return null;

    }

    public boolean checkMobileNumber(String mobileNumber) {
        mobileNumber = RawSMSService.extractEssentialMobileNum(mobileNumber);
        EntityManager m = getNewEntityManager();
        Long r
                = m.createQuery("SELECT COUNT(c) FROM Employee c WHERE c.mobileNumber LIKE ?1 OR c.mobileNumber1 LIKE ?1", Long.class)
                .setParameter(1, "%" + mobileNumber)
                .getSingleResult();

        m.close();
        return r > 0;

    }

    public Employee getEmployeeWithMobileNumber(String mobileNumber) {
        mobileNumber = RawSMSService.extractEssentialMobileNum(mobileNumber);
        EntityManager m = getNewEntityManager();
        List<Employee> r = m.createQuery("SELECT c FROM Employee c WHERE c.mobileNumber LIKE ?1 OR c.mobileNumber LIKE ?1", Employee.class).setParameter(1, "%" + mobileNumber).getResultList();

        if (r.isEmpty()) {
            m.close();
            return null;
        }

        Employee e = r.get(0);
        m.refresh(e);
        m.close();

        return e;
    }

    public List<Employee> search(String trim) {
        trim = trim.trim();
        EntityManager m = getNewEntityManager();
        List<Employee> rs = m.createQuery("SELECT c FROM Employee c WHERE c.stationArea LIKE ?1 OR c.shortName LIKE ?1 OR c.mobileNumber LIKE ?1 ORDER BY c.stationArea", Employee.class).setParameter(1, "%" + trim.trim() + "%").getResultList();
        m.close();
        return rs;
    }

    @Override
    public void remove(Employee toRemove) {

        SubmissionRawService.getInstance().removeSubmissionRawByEmp(toRemove);
        EmployeeTotalsService.getInstance().removeEmpTotals(toRemove);
        QueuedResponseService.getInstance().removeResponses(toRemove);

        super.remove(toRemove);
    }

}
