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
package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductoRegistration {

    @Inject
    private Logger log;

    @PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Producto> productoEventSrc;

    public void register(Producto producto) throws Exception {
        log.info("Registering " + producto.getDetalle());
        em.persist(producto);
        productoEventSrc.fire(producto);
    }
    public void modificar(Producto producto) throws Exception {
        log.info("Registering " + producto.getDetalle());
        em.merge(producto);
        productoEventSrc.fire(producto);
    }
    ///////////////////////////eliminar
    public void remover(Producto producto) throws Exception {
        em.remove(em.contains(producto) ? producto : em.merge(producto));
        
    }
}
