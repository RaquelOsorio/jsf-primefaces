package py.pol.una.ii.pw.rest;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import py.pol.una.ii.pw.data.Venta_DetRepository;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.model.Venta_Det;
import py.pol.una.ii.pw.service.FiltersObject;
import py.pol.una.ii.pw.service.Venta_DetRegistration;

//@ManagedBean(name="beanventadet")
//@ViewScoped
@Path("/venta_Det")
@RequestScoped
public class Venta_DetResourceRESTService {
	@PersistenceContext(unitName="PersistenceApp")
	private EntityManager em;

	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private Venta_DetRepository repository;

    @Inject
    Venta_DetRegistration registration;

 //   static List<Venta_Cab> ventas;

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Venta_Det> listAllVenta_Det() {
        return repository.findAllOrderedByVenta_Det();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Venta_Det lookupVenta_DetById(@PathParam("id") long id) {
    	Venta_Det venta_Det = repository.findById(id);
        if (venta_Det == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta_Det;
    }


    /****************************Listar por cabecera******************************/
    @GET
    @Path("/listar/{cab:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Venta_Det> listAllProductosByCabecera(@PathParam("cab") long idcab) {
        return repository.findAllByCabecera(idcab);
    }

    /****************************Crear*********************************************
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear/{producto}/{cantidad}")
    
    public Response createVenta(@PathParam("producto") long producto,@PathParam("cantidad") int cantidad) {

        Response.ResponseBuilder builder = null;
        DetalleVenta venta_det= new DetalleVenta();
     //   Compra_Cab cab= em.find(Compra_Cab.class, cabecera);
//        compra_det.setCabecera(cab);
        venta_det.setCantidad(cantidad);
        Producto prod= em.find(Producto.class, producto);
        venta_det.setProducto(prod);

        try {
            // Validates member using bean validation
           // validateCompra_Det(compra_det);
            registrationaux.register(venta_det);

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
        	Venta_Det det= buscar(id);
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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Venta_Det buscar(Long id) {
        return em.find(Venta_Det.class, id);
       
    }

  

  /*
    /////////////MODIFICAR UN CLIENTE, INGRESANDO SU ID////////////////////////
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String modificarCliente(Cliente cliente) {
        return registration.editarCliente(cliente);
    }
    
    ////////////////////////////////////////////////
    //ELIMINAR UN CLIENTE, INGRESANDO SU ID
    ////////////////////////////////////////////////
    @DELETE
    @Path("{id}")
   public String eliminarCliente(@PathParam("id") Long id) {
        String respuesta = "";
        try {
            respuesta = registration.borrarCliente(id);
        } catch (EJBTransactionRolledbackException e) {
            System.out.println("LLEGO A LA EXCEPCION");
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                return "Advertencia: No se puede borrar el cliente, ya que el mismo se utiliza en otra tabla";
            }

        }
        return respuesta;
    }
   */
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
    private void validateVenta_Det(Venta_Det venta_Det) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Venta_Det>> violations = validator.validate(venta_Det);

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
//    //////////////////////////////////////////////////////////
//    //Filtrado
//    //////////////////////////////////////////////////////////
//    @GET
//    @Path("/filtrar/{param}")
//    public Response filtrar(@PathParam("param") String content){
//        Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
//        //Gson gson = new Gson();
//        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//        FiltersObject filtros = gson.fromJson(content, tipoFiltros);
//        ventas = registration.filtrar(filtros);
//        Gson objetoGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd").create();
//        if (this.ventas.isEmpty()) {
//            return Response
//                    .status(200)
//                    .entity("[]").build();
//        } else {
//            return Response
//                    .status(200)
//                    .entity(objetoGson.toJson(ventas)).build();
//        }
//    }
//    
//    @GET
//    @Path("/filtrarCantidad/{param}")
//    public int filtrarCantidadRegistros(@PathParam("param") String content){
//        Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
//        //Gson gson = new Gson();
//            Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//
//        FiltersObject filtros = gson.fromJson(content, tipoFiltros);
//        int cantidad = registration.filtrarCantidadRegistros(filtros);
//        return cantidad;
//    }
    
}
