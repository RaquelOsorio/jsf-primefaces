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
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class Compra_DetRegistration {

	@EJB
	private Compra_CabRegistration regiscab;
	
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Compra_Det> compra_detEventSrc;


    public void register(Compra_Det producto) throws Exception {
        log.info("Registering " + producto.getProducto());//poner get producto
        em.persist(producto);
        compra_detEventSrc.fire(producto);
    }
    public void modificar(Compra_Det detalle) throws Exception {
        log.info("Registering " + detalle.getId());
        em.merge(detalle);
        compra_detEventSrc.fire(detalle);
    }
    public void remover(Compra_Det detalle) throws Exception {
        em.remove(em.contains(detalle) ? detalle : em.merge(detalle));
        
    }
}
