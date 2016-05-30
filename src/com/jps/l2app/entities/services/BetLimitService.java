/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.BetLimit;
import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.util.List;

/**
 *
 * @author org org
 */
public class BetLimitService extends AbstractEntityService<BetLimit> {

    private static BetLimitService instance;

    public static BetLimitService getInstance() {
        if (instance == null) {
            instance = new BetLimitService();
           // instance.init();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return BetLimit.class;
    }

    public List<BetLimit> search(String key, int draw, Object raffle, int limitType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void removeBetLimits(Employee toRemove) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
