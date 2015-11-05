/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.Date;
import java.util.List;

import py.pol.una.ii.pw.model.Compra_Cab;

@ApplicationScoped
public class Compra_CabRepository {

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    public Compra_Cab findById(Long id) {
        return em.find(Compra_Cab.class, id);
    }

    public Compra_Cab findByFecha(Date fecha) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Compra_Cab> criteria = cb.createQuery(Compra_Cab.class);
        Root<Compra_Cab> cabecera = criteria.from(Compra_Cab.class);
        criteria.select(cabecera).where(cb.equal(cabecera.get("fecha"), fecha));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Compra_Cab> findAllOrderedByFecha() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Compra_Cab> criteria = cb.createQuery(Compra_Cab.class);
        Root<Compra_Cab> cabecera = criteria.from(Compra_Cab.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(cabecera).orderBy(cb.asc(cabecera.get("fecha")));
        return em.createQuery(criteria).getResultList();
    }
}
