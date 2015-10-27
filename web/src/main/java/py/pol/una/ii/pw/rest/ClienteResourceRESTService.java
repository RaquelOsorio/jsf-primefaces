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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.pol.una.ii.pw.data.ClienteRepository;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.service.ClienteRegistration;


/*@Path("/clientes")
@RequestScoped*/
@ManagedBean(name="beanclientes")
@ViewScoped
public class ClienteResourceRESTService {
    
	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;
    
	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private ClienteRepository repository;

    @Inject
    ClienteRegistration registration;

   /* @GET
    @Produces(MediaType.APPLICATION_JSON)*/
    public List<Clientes> listAllProveedores() {
        return repository.findAllOrderedByNombre();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Clientes lookupProductoById(@PathParam("id") long id) {
        Clientes cliente = repository.findById(id);
        if (cliente == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return cliente;
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    /*****************************Crear*****************************************************/
    @POST
   // @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{nombre}/{apellido}")
    
    ////////////////funciona cuando no hay registros de proveedores 
    public Response createCliente(@PathParam("nombre")String nombre, @PathParam("apellido")String apellido) {
    	Clientes cliente;
    	cliente= new Clientes();
    	cliente.setNombre(nombre);
    	cliente.setApellido(apellido);
    	//cliente.setCedula(ci);
    	//proveedor.setId(n);
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateCliente(cliente);

            registration.register(cliente);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("emaill", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void create(Clientes cliente) {
    	System.out.println("crear!");
        em.persist(cliente);
    }
    
    
    /*****************************Modificar**********************************************/
    @PUT
     //@Consumes(MediaType.APPLICATION_JSON)
     @Produces(MediaType.APPLICATION_JSON)
     @Path("/modificar/{id}/{nombre}/{apellido}")
     
    public Response modificarProveedor(@PathParam("id")Long id,@PathParam("nombre")String nombre, @PathParam("apellido")String apellido) {
    	Clientes cliente= buscar(id);
    	cliente.setNombre(nombre);
    	cliente.setApellido(apellido);
    	//cliente.setCedula(ci);
    	//proveedor.setId(n);
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
           // validateCliente(cliente);

            registration.modificar(cliente);

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
    @Path("/eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Clientes cliente= buscar(id);
            registration.remover(cliente);
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
    public Clientes buscar(Long id) {
        return em.find(Clientes.class, id);
       
    }
    /*************************Carga Masiva***************************************************/
    @POST
    @Path("/cargamasiva")
  //@Consumes(MediaType.APPLICATION_JSON)
  //  @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String cargaMasiva() throws IOException {
        return registration.cargaMasiva("/home/viviana/IIN2015/2doSemestre/web/EJB/tpWeb/cliente.txt");
    }
    
    
  
    private void validateCliente(Clientes cliente) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Clientes>> violations = validator.validate(cliente);

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
