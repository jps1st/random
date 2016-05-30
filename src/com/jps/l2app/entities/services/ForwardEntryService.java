/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Forward;
import com.jps.l2app.entities.ForwardEntry;
import com.jps.l2app.entities.services.util.AbstractEntityService;

/**
 *
 * @author org org
 */
public class ForwardEntryService extends AbstractEntityService<ForwardEntry> {

    @Override
    public Class getEntityClass() {
        return ForwardEntry.class;
    }

    private static ForwardEntryService instance;

    public static ForwardEntryService getInstance() {
        if (instance == null) {
            instance = new ForwardEntryService();
        }
        return instance;
    }

}
