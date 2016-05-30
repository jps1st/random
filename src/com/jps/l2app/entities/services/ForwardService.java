/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Administrator;
import com.jps.l2app.entities.Forward;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.utils.UIUtilities;

/**
 *
 * @author org org
 */
public class ForwardService extends AbstractEntityService<Forward> {

    @Override
    public Class getEntityClass() {
        return Forward.class;
    }

    private static ForwardService instance;

    public static ForwardService getInstance() {
        if (instance == null) {
            instance = new ForwardService();
        }
        return instance;
    }

}
