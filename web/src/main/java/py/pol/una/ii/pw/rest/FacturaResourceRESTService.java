package py.pol.una.ii.pw.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Logger;


import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

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

import net.sf.jasperreports.engine.JRException;

import py.pol.una.ii.pw.data.FacturaRepository;
import py.pol.una.ii.pw.model.Factura;
import py.pol.una.ii.pw.model.Proveedor;
//import javax.ejb.EJBTransactionRolledbackException;
//import javax.ejb.EJB;
//import javax.ejb.EJBTransactionRolledbackException;
import py.pol.una.ii.pw.service.FacturaRegistration;


@Path("/facturas")
@ViewScoped
@ManagedBean(name="beanfacturas")
//@ViewScoped


public class FacturaResourceRESTService {

	@PersistenceContext(unitName="PersistenceApp")
	private EntityManager em;
	
	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private FacturaRepository repository;

    @Inject
    FacturaRegistration registration;
    
    private static Future estado=null;
    
    private static boolean detener=false;
    //estado de la facturacion true terminado false corriendo
    private boolean est= false;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/listar")
    public List<Factura> listAllFactura() {
        return repository.findAllOrderedByFactura();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Factura lookupProductoById(@PathParam("id") long id) {
    	Factura factura = repository.findById(id);
        if (factura == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return factura;
    }


    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
  //  @Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/crear")
    public void createFactura() {
    	estado=registration.generarFactura();
  //  	estado.cancel(true);
       
    }
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/estado")
  public void estadoFacturacion(Factura factura) {
    	
    	while(true) { 
    		if(estado==null)
    		{	est=true;	
    			break;
    		}else{
    			if(!estado.isDone()) { 
    				try {
    					System.out.println("corriendo");
    					est=false;
    					Thread.sleep(2000);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} 
    			} else { 
    				System.out.println("terminado");
    				est=true;
    				break; 
    			} 
    		}
    	}	
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/detener") 
  public String detener()
  {
      if(estado!=null){

          estado.cancel(true);
          return "Detenido";
      }

      return "No se puede detener";
  }
  


    public void mensaje() {
    	if (est){
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "No existe proceso de facturación corriendo."));
    	}else{
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "El proceso de facturación aún está corriendo."));
    		
    	}
    }

    /*****************************Eliminar***********************************************/
    @DELETE
    @Path("eliminar/{id:[0-9][0-9]*}")
    public Response removeProvider(@PathParam("id")Long id) {
        Response.ResponseBuilder builder = null;

        try {
        	Factura det= buscar(id);
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
    @Path("{id}")
    public Factura buscar(Long id) {
        return em.find(Factura.class, id);
       
    }
    private void validateFactura(Factura factura) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Factura>> violations = validator.validate(factura);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

     }

    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
   /* @PostConstruct
    public void init(){
    	estado=null;
    }
    */

}
