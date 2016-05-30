/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.StationGroup;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class StationGroupService extends AbstractEntityService<StationGroup> {

    @Override
    public Class getEntityClass() {
        return StationGroup.class;
    }

    private static StationGroupService instance;

    public static StationGroupService getInstance() {
        if (instance == null) {
            instance = new StationGroupService();
        }
        return instance;
    }

    public List<StationGroup> findAll() {
        EntityManager m = getNewEntityManager();
        List rs = m.createQuery("SELECT c FROM StationGroup c ORDER BY c.groupName").getResultList();
        m.close();
        return rs;
    }

    public StationGroup findByName(String groupName) {
        EntityManager m = getNewEntityManager();
        List<StationGroup> rs = m.createQuery("SELECT c FROM StationGroup c WHERE c.groupName LIKE ?1 ORDER BY c.groupName", StationGroup.class)
                .setParameter(1, groupName).getResultList();
        m.close();
        if (rs.isEmpty()) {
            return null;
        }
        return rs.get(0);
    }

}
