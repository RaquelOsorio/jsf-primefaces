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
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.Clientes;

@ApplicationScoped
public class ClienteRepository {

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    public Clientes findById(Long id) {
        return em.find(Clientes.class, id);
    }

    public Clientes findByNombre(String nombre) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clientes> criteria = cb.createQuery(Clientes.class);
        Root<Clientes> cliente = criteria.from(Clientes.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(cliente).where(cb.equal(cliente.get("nombre"), nombre));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Clientes> findAllOrderedByNombre() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clientes> criteria = cb.createQuery(Clientes.class);
        Root<Clientes> cliente = criteria.from(Clientes.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(cliente).orderBy(cb.asc(cliente.get("nombre")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    
    public List<Clientes> findAll() {
        Query query = em.createNamedQuery("Clientes.findAll");
        return query.getResultList();
    }
    
    
    
    
    
}
