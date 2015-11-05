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
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.csvreader.CsvWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.faces.event.ActionEvent;
import org.primefaces.model.LazyDataModel;
import py.pol.una.ii.pw.controller.ProductoLazyList;
import py.pol.una.ii.pw.data.ProductoRepository;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.service.FiltersObject;
import py.pol.una.ii.pw.service.ProductoRegistration;

import py.pol.una.ii.pw.data.ProveedorRepository;

import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="beanproductos")
@Path("/productos")
@ApplicationScoped
public class ProductoResourceRESTService {
	// @PersistenceContext(unitName="ProductosService", 
     //        type=PersistenceContextType.TRANSACTION)
	@PersistenceContext(unitName="PersistenceApp") 
	private EntityManager em; 
	
    LazyDataModel<Producto> lazyModel ;
    
    public LazyDataModel<Producto> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Producto> lazyModel) {
        this.lazyModel = lazyModel;
    }

	
	private List<Proveedor> proveedores;
	private long identi;
	public long getIdenti() {
		return identi;
	}
	public void setIdenti(long identi) {
		this.identi = identi;
	}

	private String detalle;
	private int precio;
	
	@Inject
    private ProveedorRepository repo;
	
	public List<Proveedor> getProveedores() {
		return proveedores;
	}
	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}

	private Proveedor proveedor;
	private int stock;
	private long identificador;
	
    public long getIdentificador() {
		return identificador;
	}
	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public int getPrecio() {
		return precio;
	}
	public void setPrecio(int precio) {
		this.precio = precio;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	
	
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private ProductoRepository repository;

    @Inject
    ProductoRegistration registration;

    private List<Producto> productos;
    /***************Listado Ascendente***************************************************/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Producto> listAllProductos() {
        return repository.findAllOrderedByDetalle();
    }
    /***************Listado Ascendente POR PRODUCTO***************************************************/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Producto> listAllProductosByStockAscendente() {
        return repository.findAllOrderedByStock();
    }
    
    /***************Listado por Proveedores***********************************************/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/proveedores/{idprov}")
    public List<Producto> listaPorProveedores(@PathParam("idprov") long id) {
        return repository.findAllByProveedores(id);
    }
    
    /****************Buscar por Id********************************************************/
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Producto lookupProductoById(@PathParam("id") long id) {
        Producto producto = repository.findById(id);
        if (producto == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return producto;
    }

    /*****************************Crear*****************************************************/
    @POST
   // @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{descripcion}/{precio}/{stock}/{proveedor}")
    
    ////////////////funciona cuando no hay registros de proveedores 
    public Response createProducto(ActionEvent event) {
    	Producto producto;
    	producto= new Producto();
    	producto.setDetalle(detalle);
    	producto.setPrecio(precio);
    	producto.setStock(stock);
    	Proveedor prov= em.find(Proveedor.class, identi);
    	producto.setProveedor(prov);
        Response.ResponseBuilder builder = null;
        detalle="";
        precio=0;
        stock=0;

        try {
            // Validates member using bean validation
            validateProducto(producto);

            registration.register(producto);

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

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear")
    public void create(Producto producto) {
        em.persist(producto);
    }
    
    /*****************************Modificar**********************************************/
    @PUT
     //@Consumes(MediaType.APPLICATION_JSON)
     @Produces(MediaType.APPLICATION_JSON)
     @Path("/modificar/{id}/{detalle}/{precio}/{proveedor}")
    
    public void obtenerProducto(Long id){
    	Producto producto= buscar(id);
    	identificador = id;
    	detalle = producto.getDetalle();
    	precio = producto.getPrecio();
    	proveedor = producto.getProveedor();
    
    	System.out.println(proveedor.getDescripcion());
    	
    }
    
    public void prueba(ActionEvent event){
    	System.out.print(identi);
    	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    	
    }
     
    public Response modificarProducto(ActionEvent event) {
    	Producto producto= buscar(identificador);
    	producto.setDetalle(detalle);
    	Proveedor prov= em.find(Proveedor.class, identi);
    	producto.setProveedor(prov);
    	producto.setPrecio(precio);
    	//proveedor.setId(n);
    	detalle="";
    	precio=0;
    	stock=0;
    	
    	
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            validateProducto(producto);

            registration.modificar(producto);

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
    public Response removeProducto(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Producto det= buscar(id);
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
    public Producto buscar(Long id) {
        return em.find(Producto.class, id);
       
    }
  


    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     * 
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateProducto(Producto producto) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Producto>> violations = validator.validate(producto);

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
    
   /*************************Exportar Cliente***************************************************/
    
    public void exportarCSV() throws MalformedURLException, IOException
    {
    	

        String outputFile = "/home/sonia/Desktop/ArchivoProductos.csv";
        boolean alreadyExists = new File(outputFile).exists();
         
        if(alreadyExists){
            File ArchivoClientes = new File(outputFile);
            ArchivoClientes.delete();
        }        
         
        try {
 
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
             
            
            csvOutput.write("Detalle");
            csvOutput.write("Precio");
            csvOutput.write("Stock");
            csvOutput.write("Proveedor");
            csvOutput.endRecord();
 
            System.out.println("\nENTRO EN EXPORTAR???");
            
            Iterator<Producto> ite=null;
            ite=productos.iterator();
            while(ite.hasNext()){
              Producto emp=ite.next();
              	String p= Integer.toString(emp.getPrecio());
              	String s= Integer.toString(emp.getStock());
              	
                csvOutput.write(emp.getDetalle());
                csvOutput.write(p);
                csvOutput.write(s);
                csvOutput.write(emp.getProveedor().getDescripcion());
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


    //////////////////////////////////////////////////////////
    //Filtrado
    //////////////////////////////////////////////////////////
    @GET
    @Path("/filtrar/{param}")
    public Response filtrar(@PathParam("param") String content){
        Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
        Gson gson = new Gson();
        FiltersObject filtros = gson.fromJson(content, tipoFiltros);
        productos = registration.filtrar(filtros);
        Gson objetoGson = new Gson();
        if (this.productos.isEmpty()) {
            return Response
                    .status(200)
                    .entity("[]").build();
        } else {
            return Response
                    .status(200)
                    .entity(objetoGson.toJson( productos)).build();
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
    	proveedores = repo.findAllOrderedByDescripcion();
        lazyModel = new ProductoLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount());   
    }

}