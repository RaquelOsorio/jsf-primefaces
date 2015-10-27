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
package py.pol.una.ii.pw.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;

import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.service.Compra_DetRegistration;


/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the productos table.
 */

@Path("/compras")
@RequestScoped
public class Compra_DetResourceRESTService {
	// @PersistenceContext(unitName="ProductosService", 
      //       type=PersistenceContextType.TRANSACTION)
    
	
	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;
    
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private Compra_DetRepository repository;

    @Inject
    Compra_DetRegistration registration;
    
   
    /***************************Listar todo****************************************/	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todas")
    public List<Compra_Det> listAllProductos() {
    	System.out.println("entra en listar	");
        return repository.findAllOrderedByProducto();
    }

    
    /***************************Busqueda por id***********************************/
    @GET
    @Path("/id={id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Compra_Det lookupProductoById(@PathParam("id") long id) {
        Compra_Det detalle = repository.findById(id);
        if (detalle == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return detalle;
    }
    /****************************Listar por cabecera******************************/
    @GET
    @Path("/listar/{cab:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Compra_Det> listAllProductosByCabecera(@PathParam("cab") long idcab) {
        return repository.findAllByCabecera(idcab);
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    
    /****************************Crear*********************************************
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{producto}/{cantidad}")
    
    public Response createProducto(@PathParam("producto") long producto,@PathParam("cantidad") int cantidad) {

        Response.ResponseBuilder builder = null;
        DetalleCompra compra_det= new DetalleCompra();
     //   Compra_Cab cab= em.find(Compra_Cab.class, cabecera);
//        compra_det.setCabecera(cab);
        compra_det.setCantidad(cantidad);
        Producto prod= em.find(Producto.class, producto);
        compra_det.setProducto(prod);

        try {
            // Validates member using bean validation
           // validateCompra_Det(compra_det);
            registrationaux.register(compra_det);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    

    /*****************************Eliminar***********************************************/
    @DELETE
    @Path("eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Compra_Det det= buscar(id);
            registration.remover(det);
            builder = Response.ok();
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
  //  @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Path("{id}")
    public Compra_Det buscar(Long id) {
        return em.find(Compra_Det.class, id);
       
    }

  
    
  
    private void validateCompra_Det(Compra_Det detalle) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Compra_Det>> violations = validator.validate(detalle);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        /*if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }*/
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     * 
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     * 
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
   /* public boolean emailAlreadyExists(String detalle) {
        Producto member = null;
        try {
            member = repository.findByDetalle(detalle);
        } catch (NoResultException e) {
            // ignore
        }
        return member != null;
    }*/
}
