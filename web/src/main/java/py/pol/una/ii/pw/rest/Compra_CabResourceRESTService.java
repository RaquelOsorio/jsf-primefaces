
package py.pol.una.ii.pw.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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

import py.pol.una.ii.pw.data.Compra_CabRepository;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.service.Compra_CabRegistration;
import py.pol.una.ii.pw.service.Compra_DetRegistration;
import py.pol.una.ii.pw.service.ProductoRegistration;


@Path("/cabeceras")
//@RequestScoped

@ManagedBean(name="beancompra")
@ViewScoped
public class Compra_CabResourceRESTService {
	
/*	private List<Compra_Cab> compraFilteringList;
	
	public List<Compra_Cab> getClientesFilteringList() {
	return compraFilteringList;
	}
	public void setClientesFilteringList(List<Compra_Cab> clientesFilteringList) {
	this.compraFilteringList = clientesFilteringList;
	}*/
	
	@Inject
    ProductoRegistration pr;
	private String mensaje;
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	private Producto producto;
	private int cantidad;
	private Integer [] cantidades = new Integer[100];

	//private List<Integer> cantidades = new ArrayList(100);
	private long identificador;
	private List<Producto> listaProductos;
	
	
	@Inject
	ProductoResourceRESTService productoManager;
	

	public long getIdentificador() {
		return identificador;
	}

	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}

	private Proveedor proveedor;
	
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	private List<Compra_Det> detalle;
	@PersistenceContext(unitName="PersistenceApp") 
	private EntityManager em;
	
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private Compra_CabRepository repository;

    @Inject
    Compra_CabRegistration registration;
    
    @Inject
    Compra_DetRegistration registrationdetalle;


    private List <Compra_Det> listadetalle;
    private List<Compra_Det>[] vector;
   
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Compra_Cab> listAllCabeceras() {
        return repository.findAllOrderedByFecha();
    }

    /*@GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)*/
    public Compra_Cab lookupProductoById(@PathParam("id") long id) {
        Compra_Cab cabecera = repository.findById(id);
        if (cabecera == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return cabecera;
    }
    
    /****************************Cargar Productos******************************************/
    
    public void cargarProductos(ActionEvent event){
    	
    	listaProductos = productoManager.listaPorProveedores(identificador);
    	
    }

    public Integer[] getCantidades() {
		return cantidades;
	}

	public void setCantidades(Integer[] cantidades) {
		this.cantidades = cantidades;
	}

	public List<Producto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	/****************************Crear Compras********************************************/
    
    /*@POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{proveedor}")*/
   // public void create(Compra_Cab compra) {
       
        
      
     //}
    public void agregarCabecera(long id){
    	Proveedor prov= em.find(Proveedor.class, id);
    	Compra_Cab newCabecera= new Compra_Cab();
    	newCabecera.setProveedor(prov);
    	newCabecera.setDetalleCompraList(listadetalle);
    	
    	 try {
         	
 			registration.registrarCompra(newCabecera);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	 
     }
    
    public void agregarDetalle(long id) throws Exception{
    	
    	Producto produ;
    	produ= em.find(Producto.class, id);
		Compra_Det newDetalle= new Compra_Det();
		newDetalle.setProducto(produ);	
    	newDetalle.setCantidad(cantidad);
    	listadetalle.add(newDetalle);
    //	produ.setStock(produ.getStock()+cantidad);
    //	pr.modificar(produ);
    //	System.out.println(algo);
    	System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    	System.out.println(cantidad);
    	cantidad=0;
    	
    	 	
     }
    @PostConstruct
    public void init(){
    	listadetalle = new ArrayList<Compra_Det>();
    	listaProductos = new ArrayList<Producto>();
    }
    
    
    public List<Compra_Det> getListadetalle() {
		return listadetalle;
	}

	public void setListadetalle(List<Compra_Det> listadetalle) {
		this.listadetalle = listadetalle;
	}

	
    


    /*****************************Eliminar***********************************************/
    @DELETE
    @Path("eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Compra_Cab det= buscar(id);
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
    public Compra_Cab buscar(Long id) {
        return em.find(Compra_Cab.class, id);
       
    }

  
    @POST
    @Path("/cargamasiva")
  //@Consumes(MediaType.APPLICATION_JSON)
  //  @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public void cargaMasiva() throws IOException {
        mensaje= registration.cargaMasiva("/home/shaka/pfff/jsf-primefaces/compras.txt");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, "PrimeFaces Rocks."));
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
    private void validateCompra_Cab(Compra_Cab cabecera) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Compra_Cab>> violations = validator.validate(cabecera);

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