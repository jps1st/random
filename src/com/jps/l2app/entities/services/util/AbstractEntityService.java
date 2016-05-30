package com.jps.l2app.entities.services.util;

import javax.persistence.EntityManager;

public abstract class AbstractEntityService<G> {

    public abstract Class getEntityClass();

    public G refresh(G found) {
        EntityManager m = getNewEntityManager();
        found = m.merge(found);
        m.refresh(found);
        m.close();
        return found;
    }

    public G find(Object id) {
        EntityManager m = getNewEntityManager();
        G found = (G) m.find(getEntityClass(), id);
        m.close();
        return found;
    }

    public void remove(G toRemove) {
        EntityManager m = getNewEntityManager();
        m.getTransaction().begin();
        m.remove(m.merge(toRemove));
        m.getTransaction().commit();
        m.close();
    }

    public G update(G updatedInstance) {
        EntityManager m = getNewEntityManager();
        m.getTransaction().begin();
        G merge = m.merge(updatedInstance);
        m.getTransaction().commit();
        m.close();
        return merge;
    }

    public G insert(G toInsert) {
        return update(toInsert);
    }

    public EntityManager getNewEntityManager() {
        return DbaseManager.getNewEntityManager();
    }

}
