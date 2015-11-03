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

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Iterator;
import java.util.List;
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
    
    ////////////////////////////////////////////////////////////////
  //Filtrado
  ///////////////////////////////////////////////////////////////
   public List<Producto> filtrar (FiltersObject filtros){
     String queryString;
     
     // Hacemos el select de la entidad cliente
     queryString = "SELECT o FROM Producto o " ;
     
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
     
     return em.createQuery(queryString, Producto.class)
             .setFirstResult(filtros.getFirst())
             .setMaxResults(filtros.getPageSize())
             .getResultList();
 }
    
    public int filtrarCantidadRegistros (FiltersObject filtros){
        String queryString;
        
        // Establece el nombre de la entidad que se esta buscando
        queryString = "SELECT COUNT( o ) FROM Producto o ";
        
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
  
}
