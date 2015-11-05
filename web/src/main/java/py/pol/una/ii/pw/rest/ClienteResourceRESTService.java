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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.primefaces.model.LazyDataModel;

import com.csvreader.CsvWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import py.pol.una.ii.pw.controller.ClienteLazyList;
import py.pol.una.ii.pw.data.ClienteRepository;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.service.ClienteRegistration;
import py.pol.una.ii.pw.service.FiltersObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;

@ManagedBean(name="beanclientes")
@Path("/clientes")
@ApplicationScoped
//@ViewScoped
//@RequestScoped
public class ClienteResourceRESTService {
	
	private String mensaje;
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}



	private String nombre;
	private String apellido;
	private long identificador;
    
	public long getIdentificador() {
		return identificador;
	}

	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}


    
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

    static List<Clientes> clientes;
    
    LazyDataModel<Clientes> lazyModel ;

    public LazyDataModel<Clientes> getLazyModel() {
		return lazyModel;
	}


	public void setLazyModel(LazyDataModel<Clientes> lazyModel) {
		this.lazyModel = lazyModel;
	}

	@GET

    @Produces(MediaType.APPLICATION_JSON)
    public List<Clientes> listAllClientes() {
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
    public Response createCliente(ActionEvent event) {
    	Clientes cliente;
    	cliente= new Clientes();
    	cliente.setNombre(nombre);
    	cliente.setApellido(apellido);
    	nombre="";
    	apellido="";
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
       public void obtenerCliente(Long id){
       	Clientes c = buscar(id);
       	identificador=id;
       	nombre = c.getNombre();
       	apellido = c.getApellido();
       	
       	
       }
       public Response modificarCliente(ActionEvent event) {
       	
       		Clientes cliente = buscar(identificador);
       		cliente.setNombre(nombre);
       		cliente.setApellido(apellido);
       		nombre="";
           	apellido="";
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
    	
    	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
    public void cargaMasiva() throws IOException {
        mensaje = registration.cargaMasiva("/home/shaka/pfff/jsf-primefaces/cliente.txt");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, "PrimeFaces Rocks."));
    }
    
    /*************************Exportar Cliente***************************************************/
    
    public void exportarCSV() throws MalformedURLException, IOException
    {
    	List<Clientes> cli;
        cli= this.clientes;

        String outputFile = "/home/sonia/Desktop/ArchivoClientes.csv";
        boolean alreadyExists = new File(outputFile).exists();
         
        if(alreadyExists){
            File ArchivoClientes = new File(outputFile);
            ArchivoClientes.delete();
        }        
         
        try {
 
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
             
            
            csvOutput.write("Nombre");
            csvOutput.write("Apellido");
            csvOutput.endRecord();
 
            System.out.println("\nENTRO EN EXPORTAR???");
            
            Iterator<Clientes> ite=null;
            ite=clientes.iterator();
            while(ite.hasNext()){
              Clientes emp=ite.next();
                csvOutput.write(emp.getNombre());
                csvOutput.write(emp.getApellido());
                csvOutput.endRecord();                   
            }
             
            csvOutput.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
//    	ClienteRegistration cr;
//    	List<Clientes> lista;
//    	String stringFiltros="";
//    	Map<String,Object> filtros = getFiltrado();
//    	String requestString = "http://localhost:8080/EjbJaxRS-web/rest/clientes";
//    	String nombre=(String) filtros.get("nombre");
//		System.out.println(nombre);
//		String apellido=(String) filtros.get("apellido");
//		System.out.println(apellido);
//		
//		if(nombre!=null || apellido!=null)
//			requestString+="?";
//		if (nombre !=null) {
//			requestString+="nombre="+nombre;
//			if(apellido!=null)
//				requestString+="&";
//		}
//		if (apellido !=null) {
//			requestString+="apellidos="+apellido;
//		}
//		System.out.println("esto imprime" + getFiltrado());
//		System.out.println("esto es requeststring" + requestString);
//		FacesContext context = FacesContext.getCurrentInstance();  
//		try {  
//			context.getExternalContext().redirect(requestString);  
//		}catch (Exception e) {  
//			e.printStackTrace();  
//		}  
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

//	
//    @GET
//    @Path("/events.csv")
//    public Response getEventsAsCsv(@Context HttpServletResponse response) throws Throwable {
//        DataReader reader = getEvents();
// 
//        response.setContentType("text/csv");
//        response.setHeader("Content-Disposition", "attachment; filename=\"events.csv\"");
//        PrintWriter printWriter = response.getWriter();
//        DataWriter writer = new CSVWriter(printWriter).setFieldNamesInFirstRow(true);
// 
//        JobTemplate.DEFAULT.transfer(reader, writer);
// 
//        return Response.ok().build();
//    }
    

    //////////////////////////////////////////////////////////
    //Filtrado
    //////////////////////////////////////////////////////////
    @GET
    @Path("/filtrar/{param}")
    public Response filtrar(@PathParam("param") String content){
        Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
        Gson gson = new Gson();
        FiltersObject filtros = gson.fromJson(content, tipoFiltros);
        clientes = registration.filtrar(filtros);
        Gson objetoGson = new Gson();
        if (this.clientes.isEmpty()) {
            return Response
                    .status(200)
                    .entity("[]").build();
        } else {
            return Response
                    .status(200)
                    .entity(objetoGson.toJson(clientes)).build();
        }
    }
    
    @GET
    @Path("/filtrarCantidad/{param}")
    public int filtrarCantidadRegistros(@PathParam("param") String content){
        Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
        Gson gson = new Gson();
        FiltersObject filtros = gson.fromJson(content, tipoFiltros);
        int cantidad = registration.filtrarCantidadRegistros(filtros);
        return cantidad;
    }
    
    @PostConstruct
    public void init(){
        lazyModel = new ClienteLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount());   
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