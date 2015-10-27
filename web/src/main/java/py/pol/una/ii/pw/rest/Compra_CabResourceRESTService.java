
package py.pol.una.ii.pw.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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


/*@Path("/cabeceras")
@RequestScoped
*/
@ManagedBean(name="beancabeceras")
@ViewScoped
public class Compra_CabResourceRESTService {
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


    List <Compra_Det> listadetalle;
    /*@GET
    @Produces(MediaType.APPLICATION_JSON)*/
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

    /****************************Crear Compras********************************************/
    
    /*@POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{proveedor}")*/
    public void create(Compra_Cab compra) {
        try {
        	
			registration.registrarCompra(compra);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
      
     }
    public Compra_Cab agregarCabecera(Proveedor proveedor){
    	Compra_Cab newCabecera= new Compra_Cab();
    	newCabecera.setProveedor(proveedor);
    	newCabecera.setDetalleCompraList(listadetalle);
    	return newCabecera;
    	 
     }
    
    public void agregarDetalle(Producto producto, int cantidad){
    	Compra_Det newDetalle= new Compra_Det();
    	newDetalle.setProducto(producto);
    	newDetalle.setCantidad(cantidad);
    	listadetalle.add(newDetalle);
    	 
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