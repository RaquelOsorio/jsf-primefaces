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
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    
    public String cargaMasiva(String direc) throws FileNotFoundException, IOException {
        String errores = new String();
        int cantidadErrores = 0;
        String direccion = direc;
        FileReader fr;
        BufferedReader br;
        File archivo;
        String linea;
        Integer cantidadTotal = 0;
        archivo = new File(direccion);
        fr = new FileReader(archivo);
        br = new BufferedReader(fr);
        Gson gson = new Gson();
        boolean error = false;
        while (br.ready()) {
            linea = br.readLine();
            cantidadTotal++;
            //Clientes clienteNuevo = new Clientes();
            Compra_Cab auxiliar;
            try {
   
                auxiliar = gson.fromJson(linea, Compra_Cab.class);
                if (auxiliar.getDetalleCompraList() == null) {
                    errores = errores + "Sin detalles, linea: " + cantidadTotal.toString() + "\n";
                    error = true;
                    cantidadErrores++;
                }
                
                if (auxiliar.getProveedor() == null) {
                    errores = errores + "Sin proveedor, linea: " + cantidadTotal.toString() + "\n";
                    error = true;
                    cantidadErrores++;
                }
                
                /*
                Iterator<DetalleCompra> it=null;
                it=auxiliar.getDetalle().iterator();
                //System.out.println("ITERATOR"+it.next().getCantidad());
                
                while (it.hasNext())
                {	
                	DetalleCompra aux = it.next();
                	if(aux.getProducto().getProveedor() != auxiliar.getProveedor())
                	{
                		errores = errores + "El proveedor no coincide, linea: " + cantidadTotal.toString() + "\n";
                        error = true;
                        cantidadErrores++;
                	}
                	
                }*/
                             
                
                if (!error) {
                    try {
                    	registrarCompra(auxiliar);
                    } catch (Exception e) {
                        errores = errores + " Error en la linea " + cantidadTotal.toString();
                        cantidadErrores++;

                    }
                }
            } catch (Exception e) {
                errores = errores + "Formato de archivo incorrecto \n";
                cantidadErrores++;

            }

            error = false;
        }

        System.out.println("TOTAL: " + cantidadTotal);
        System.out.println("Errores: " + cantidadErrores);
        System.out.println("ERRORES: " + errores);

        if (cantidadErrores > 0) {
            //context.setRollbackOnly();
            return errores;
        }
        return "Carga Exitosa," + cantidadTotal + " compras cargadas";
    }

    
    
    
    
    
    
    
    
}
