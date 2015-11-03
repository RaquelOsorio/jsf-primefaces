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

import java.util.List;

import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

@ApplicationScoped
public class ProductoRepository {

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    public Producto findById(Long id) {
        return em.find(Producto.class, id);
    }

    public Producto findByDetalle(String detalle) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> producto = criteria.from(Producto.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(producto).where(cb.equal(producto.get("detalle"), detalle));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Producto> findAllOrderedByDetalle() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> producto = criteria.from(Producto.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(producto).orderBy(cb.asc(producto.get("detalle")));
        return em.createQuery(criteria).getResultList();
    }
    /*******************Listado por stock ascendente********************************************/
    public List<Producto> findAllOrderedByStock() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> producto = criteria.from(Producto.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(producto).orderBy(cb.asc(producto.get("stock")));
        return em.createQuery(criteria).getResultList();
    }
    
    
    //Listar por proveedores
    public List<Producto> findAllByProveedores(Long prov) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Producto> criteria = cb.createQuery(Producto.class);
        Root<Producto> proveedores = criteria.from(Producto.class);
        criteria.select(proveedores).where(cb.equal(proveedores.get("proveedor"), prov));
        return em.createQuery(criteria).getResultList();
         //  criteria.select(detalle).orderBy(cb.asc(detalle.get("producto")));
        //return em.createQuery(criteria).getResultList();
    }
    
    
    
    
}
