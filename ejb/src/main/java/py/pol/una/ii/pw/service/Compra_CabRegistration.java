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

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class Compra_CabRegistration {

    @Inject
    private Logger log;

    @PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Compra_Cab> cabeceraEventSrc;
    
    @Inject
    private Compra_DetRepository repository;
    
    @Inject
    private Compra_DetRegistration detalleRegistration;
    
    public void registrarCabecera(Compra_Cab cabecera) throws Exception {
        log.info("Registering " + cabecera.getFecha());
        em.persist(cabecera);
        cabeceraEventSrc.fire(cabecera);
    }
    
    
    public void modificar(Compra_Cab cabecera) throws Exception {
        log.info("Registering " + cabecera.getFecha());
        em.merge(cabecera);
        cabeceraEventSrc.fire(cabecera);
    }
    public void remover(Compra_Cab cabecera) throws Exception {
        em.remove(em.contains(cabecera) ? cabecera : em.merge(cabecera));
        
    }
    /**********************Registrar una compra**********************************************/
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registrarCompra(Compra_Cab nuevaCabecera) throws Exception {
     	Compra_Cab cabecera= new Compra_Cab();
    	Date fecha = new Date();
    	cabecera.setFecha(fecha);
    	cabecera.setProveedor(nuevaCabecera.getProveedor());
    	em.persist(cabecera);
    	 
        List<Compra_Det>  detalle= nuevaCabecera.getDetalleCompraList();

        //int cantidad=detalle.size();
        
        Iterator<Compra_Det> it=null;
        it=detalle.iterator();
        //System.out.println("ITERATOR"+it.next().getCantidad());
        
        while (it.hasNext())
        {	
        	
        		Compra_Det aux = it.next();
        		Compra_Det cdetalle= new Compra_Det();
        		cdetalle.setCabecera(cabecera);
        		cdetalle.setCantidad(aux.getCantidad());
        		cdetalle.setProducto(aux.getProducto());
        		detalleRegistration.register(cdetalle);
        		//DetalleCompra auxiliar= em.find(DetalleCompra.class, aux.getId());
        		//em.remove(em.contains(auxiliar) ? auxiliar : em.merge(auxiliar));
        		//System.out.println("DETALLE cantidad"+aux.getCantidad());
        		//detalle.remove(it.next());
        	
        	
        }
       
        
    }
    
    
    
    
    
    
    
    
    
    
}
