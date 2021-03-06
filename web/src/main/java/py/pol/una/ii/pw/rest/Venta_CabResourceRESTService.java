package py.pol.una.ii.pw.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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

import py.pol.una.ii.pw.data.Venta_CabRepository;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.model.Venta_Det;
import py.pol.una.ii.pw.service.ProductoRegistration;
//import javax.ejb.EJBTransactionRolledbackException;
//import javax.ejb.EJB;
//import javax.ejb.EJBTransactionRolledbackException;
import py.pol.una.ii.pw.service.Venta_CabRegistration;


import javax.swing.JOptionPane;

@Path("ventas")
@ManagedBean(name="beanventa")
@ViewScoped


public class Venta_CabResourceRESTService {
	@Inject
    ProductoRegistration pr;
	
	private String mensaje;
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	@Inject
	ProductoResourceRESTService productoManager;
	private long identificador;
	private int cantidad;
	private List<Producto> listaProductos;
	private List<Venta_Det> listadetalle;

	public List<Venta_Det> getListadetalle() {
		return listadetalle;
	}

	public void setListadetalle(List<Venta_Det> listadetalle) {
		this.listadetalle = listadetalle;
	}

	public long getIdentificador() {
		return identificador;
	}

	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public List<Producto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	@PersistenceContext(unitName="PersistenceApp") 
	private EntityManager em;
	
	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private Venta_CabRepository repository;

    @Inject
    Venta_CabRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Venta_Cab> listAllVenta_Cab() {
        return repository.findAllOrderedByVenta_Cab();
    }
    
    @PostConstruct
    public void init(){
    	listadetalle = new ArrayList<Venta_Det>();
    	
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Venta_Cab lookupVenta_CabById(@PathParam("id") long id) {
    	Venta_Cab venta_Cab = repository.findById(id);
        if (venta_Cab == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta_Cab;
    }

    /****************************Crear Ventas********************************************/
    
    /*@POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{cliente}")
    public void create(Venta_Cab cab) {
        try {
        	
			registration.registrarVenta(cab);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
      
     }*/
    
    public void agregarCabecera(long id){
    	Clientes clie= em.find(Clientes.class, id);
    	Venta_Cab newCabecera= new Venta_Cab();
    	newCabecera.setCliente(clie);
    	newCabecera.setDetalleVenta(listadetalle);
    	
    	 try {
    		 
         	
 			registration.registrarVenta(newCabecera);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	 
     }
    public void agregarDetalle(long id) throws Exception{
    	
    	Producto produ;
    	produ= em.find(Producto.class, id);
		Venta_Det newDetalle= new Venta_Det();
		newDetalle.setProducto(produ);	
    	newDetalle.setCantidad(cantidad);
    	listadetalle.add(newDetalle);
   // 	produ.setStock(produ.getStock()-cantidad);
    //	pr.modificar(produ);
    //	System.out.println(algo);
    	System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    	System.out.println(cantidad);
    	cantidad=0;
    	
     }
    
    /****************************CARGAR PRODUCTOS***************************************************/

    public void cargarProductos(ActionEvent event){
    	
    	listaProductos = productoManager.listAllProductos();			
    }
    
    /*****************************Eliminar***********************************************/
    @DELETE
    @Path("eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Venta_Cab det= buscar(id);
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
    public Venta_Cab buscar(Long id) {
        return em.find(Venta_Cab.class, id);
       
    }

  

    @POST
    @Path("/cargamasiva")
  //@Consumes(MediaType.APPLICATION_JSON)
  //  @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    
    public void cargaMasiva() throws IOException {
    	
    	mensaje=registration.cargaMasiva("/home/shaka/pfff/jsf-primefaces/ventas.txt");
    	System.out.println(mensaje);
    	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, "PrimeFaces Rocks."));
    	
    	//return mensaje;
    	
        
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
    private void validateVenta_Cab(Venta_Cab venta_Cab) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Venta_Cab>> violations = validator.validate(venta_Cab);

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
    

}
