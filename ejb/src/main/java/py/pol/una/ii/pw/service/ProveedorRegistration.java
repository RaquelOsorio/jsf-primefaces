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

import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

///import javax.ws.rs.POST;
///import javax.ws.rs.PUT;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProveedorRegistration {

    @Inject
    private Logger log;

    @PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Proveedor> proveedorEventSrc;


    public void register(Proveedor proveedor) throws Exception {
        log.info("Registering " + proveedor.getDescripcion());
        em.persist(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    public void modificar(Proveedor proveedor) throws Exception {
        log.info("Registering " + proveedor.getDescripcion());
        em.merge(proveedor);
        proveedorEventSrc.fire(proveedor);
    }

    
    public void remover(Proveedor detalle) throws Exception {
        em.remove(em.contains(detalle) ? detalle : em.merge(detalle));
        
    }
    /*
    //modificar
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(Proveedor proveedor) {
        em.merge(proveedor);
    }
 
    //eliminar
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") Integer id) {
        Proveedor proveedor = read(id);
        if(null != proveedor) {
            em.remove(proveedor);
        }
    }*/
}
