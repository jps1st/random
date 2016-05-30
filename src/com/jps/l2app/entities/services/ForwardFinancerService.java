/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.ForwardFinancer;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class ForwardFinancerService extends AbstractEntityService<ForwardFinancer> {

    @Override
    public Class getEntityClass() {
        return ForwardFinancer.class;
    }

    private static ForwardFinancerService instance;

    public static ForwardFinancerService getInstance() {
        if (instance == null) {
            instance = new ForwardFinancerService();
        }
        return instance;
    }

    public List<ForwardFinancer> getFinancerList() {
        EntityManager m = getNewEntityManager();
        List<ForwardFinancer> rs = m.createQuery("SELECT c FROM ForwardFinancer c WHERE c.active = TRUE ORDER BY c.priority, c.name", ForwardFinancer.class).getResultList();
        for (ForwardFinancer r : rs) {
            m.refresh(r);
        }
        m.close();
        return rs;
    }

}
