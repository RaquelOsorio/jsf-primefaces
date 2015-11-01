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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.filter.*;
import py.pol.una.ii.pw.data.ProveedorRepository;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Page;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.service.ProveedorRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the productos table.
 */
@ManagedBean(name="beanproveedores")
@ViewScoped
public class ProveedorResourceRESTService extends LazyDataModel{
    
	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;
    
	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private ProveedorRepository repository;

    private Page pagina;
    @Inject
    ProveedorRegistration registration;
    
    private List<Proveedor> filtroProveedor;
    private List<Proveedor> proveedores;
    
    
    public List<Proveedor> getProveedores() {
		return proveedores;
	}



	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}



//	@PostConstruct
//    public void init() {
//    	proveedores = listAllProveedores();
//    }
    
    
    
    /*********************Listado ascendente*****************************************************/
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
    public List<Proveedor> listAllProveedores() {
        return repository.findAllOrderedByDescripcion();
    }
    /*********************Listado descendente*****************************************************/
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/desc")
    public List<Proveedor> listAllProveedoresDescendente() {
        return repository.findAllOrderedDescByDescripcion();
    }
    
    
    
    public List<Proveedor> getFiltroProveedor() {
		return filtroProveedor;
	}
	public void setFiltroProveedor(List<Proveedor> filtroProveedor) {
		this.filtroProveedor = filtroProveedor;
	}
//	@GET
//    @Path("/{id:[0-9][0-9]*}")
//    @Produces(MediaType.APPLICATION_JSON)
    public Proveedor lookupProductoById(/*@PathParam("id")*/ long id) {
        Proveedor proveedor = repository.findById(id);
        if (proveedor == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return proveedor;
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    /*****************************Crear*****************************************************/
    @POST
   // @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{descripcion}")
    
    ////////////////funciona cuando no hay registros de proveedores 
    public Response createProveedor(@PathParam("descripcion")String des) {
    	
    	Proveedor proveedor;
    	proveedor= new Proveedor();
    	proveedor.setDescripcion(des);
    	//proveedor.setId(n);
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateProveedor(proveedor);

            registration.register(proveedor);

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
    public void create(Proveedor proveedor) {
    	System.out.println("crear!");
        em.persist(proveedor);
    }
    
    
    /*****************************Modificar**********************************************/
    @PUT
     //@Consumes(MediaType.APPLICATION_JSON)
     @Produces(MediaType.APPLICATION_JSON)
     @Path("/modificar/{id}/{des}")
     
    public Response modificarProveedor(@PathParam("id")Long id,@PathParam("des")String des) {
    	Proveedor proveedor= buscar(id);
    	proveedor.setDescripcion(des);
    	//proveedor.setId(n);
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            validateProveedor(proveedor);

            registration.modificar(proveedor);

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
//    @DELETE
//    @Path("/eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(/*@PathParam("id")*/Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Proveedor det= buscar(id);
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
    public Proveedor buscar(Long id) {
        return em.find(Proveedor.class, id);
       
    }

  
    private void validateProveedor(Proveedor proveedor) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Proveedor>> violations = validator.validate(proveedor);

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/page/{pag}/{tam}")
    public Page getPage(@PathParam("pag")int page,@PathParam("tam")int pageSize) throws SQLException {
    	int first = pagina.getStartItemByPage(page, pageSize);
    	int lastElement = first + pageSize - 1;
    	Connection c = null;
    	PreparedStatement statement = null;
    	ResultSet resultSet = null;
    	String query = "SELECT * FROM Proveedores";
    	String queryCount = "SELECT COUNT(*) FROM Proveedores";
    	Collection results = new ArrayList();
    	try {
    		// c = myDataSource.getConnection();  // Sustituir al gusto
    		String realQuery = "select * from ( select a.*, rownum rnum from (" +
                            query +
                            ") a where rownum <= "+lastElement+" ) where rnum >= "+first;
    		statement = c.prepareStatement(realQuery);
    		resultSet = statement.executeQuery();
    		while (resultSet.next()) {
    			 // Sustituir por nuestra creacion de Beans
                 Proveedor m = new Proveedor();
                m.setId(resultSet.getLong(1));
                 results.add(m);
    		}
        
    		try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {}
    		try { if (statement != null) statement.close(); } catch (SQLException e) {}
    		// c = myDataSource.getConnection();  // Sustituir al gusto
    		statement = c.prepareStatement(queryCount);
    		resultSet = statement.executeQuery();
    		resultSet.next();
    		int totalElements = resultSet.getInt(1); 

    		return new Page(results,  page, pageSize, totalElements);

    	} finally {
    		try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {}
    		try { if (statement != null) statement.close(); } catch (SQLException e) {}
    		try { if (c != null) c.close(); } catch (SQLException e) {}
    	}
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
