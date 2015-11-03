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

import java.util.Iterator;
import java.util.List;
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
    
    ////////////////////////////////////////////////////////////////
  //Filtrado
  ///////////////////////////////////////////////////////////////
   public List<Proveedor> filtrar (FiltersObject filtros){
     String queryString;
     
     // Hacemos el select de la entidad cliente
     queryString = "SELECT o FROM Proveedor o " ;
     
     // Si el filtro es distinto de null ampliamos la consulta, con el filtro
     if (filtros.getFilters() != null && !filtros.getFilters().isEmpty() ){
         queryString = queryString + "WHERE";
         for ( Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext(); ){ //iteramos con el filtro
             try{
                 String atributo = it.next();
                 String valor = filtros.getFilters().get(atributo).toString();
                 if (  atributo.equals("id")){
                     int cantidad = Integer.parseInt(valor);
                     queryString = queryString + " " + atributo + " = " + cantidad; //si es un entero traemos lo menos o igual a ese numero
                 } else {
                     valor = "'%" + valor + "%'";
                     queryString = queryString + " " + atributo + " LIKE " + valor; //si el valor a filtrar es una cadena , traemos la misma cadena o la que le contiene
                 }
                 
             } catch ( Exception e ){
                 
             }
         }
     }
     //Para el ordenamiento, se agrega el order by a la consulta con el campo que se ordena y la direccion de ordenamiento
     queryString = queryString + "ORDER BY o." + filtros.getSortField() + " " + filtros.getSortOrder();
     
     return em.createQuery(queryString, Proveedor.class)
             .setFirstResult(filtros.getFirst())
             .setMaxResults(filtros.getPageSize())
             .getResultList();
 }
    
    public int filtrarCantidadRegistros (FiltersObject filtros){
        String queryString;
        
        // Establece el nombre de la entidad que se esta buscando
        queryString = "SELECT COUNT( o ) FROM Proveedor o ";
        
        // Si existen filtros los agrega a la consulta
        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty() ){
            queryString = queryString + "WHERE";
            for ( Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext(); ){
                try{
                    String atributo = it.next();
                    String valor = filtros.getFilters().get(atributo).toString();
                    
                    if (atributo.equals("id") ){
                        int cantidad = Integer.parseInt(valor);
                        queryString = queryString + " " + atributo + " = " + cantidad;
                    } else {
                        valor = "'%" + valor + "%'";
                        queryString = queryString + " " + atributo + " LIKE " + valor;
                    }
                
                } catch ( Exception e ){
                    
                }
            }
        }
        Long result = em.createQuery(queryString, Long.class).getSingleResult();
       // System.out.println("cantidad"+result.intValue());
        return result.intValue();
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
