package py.pol.una.ii.pw.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;

import py.pol.una.ii.pw.data.Compra_CabRepository;
import py.pol.una.ii.pw.data.Venta_CabRepository;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.service.Venta_CabRegistration;

//@Model
@ManagedBean(name="beanventas")
@ViewScoped
public class Venta_CabController extends LazyDataModel<Venta_Cab>{

	@Inject
    private FacesContext facesContext;

    @Inject
    private Venta_CabRegistration venta_CabRegistration;

    private Venta_Cab newVenta_Cab;
    
    @Inject
    private Venta_CabRepository repository;

	private List<Venta_Cab> venta_CabFilteringList;
	
    private static Map<String,Object> filtrado = new HashMap();

	private Venta_Cab venta;


    public Venta_Cab getVenta() {
		return venta;
	}

	public void setVenta(Venta_Cab venta) {
		this.venta = venta;
	}

	public List<Venta_Cab> getVenta_CabFilteringList() {
		return venta_CabFilteringList;
	}

	public void setVenta_CabFilteringList(List<Venta_Cab> venta_CabFilteringList) {
		this.venta_CabFilteringList = venta_CabFilteringList;
	}
	
    public Map<String,Object> getFiltrado()
    {
        return Venta_CabController.filtrado;
    }
    
    public void setFiltrado(Map<String,Object> fil)
    {
    	Venta_CabController.filtrado = fil;
    }

	@Produces
    @Named
    public Venta_Cab getNewVenta_Cab() {
        return newVenta_Cab;
    }

    public List<Venta_Cab> listAllVenta_Cab() {
        return repository.findAllOrderedByVenta_Cab();
    }
    
	
    public void exportarCSV() throws MalformedURLException, IOException
    {

    	System.out.println("Entro en exportar???");
        
    	
    	Map<String,Object> filtros = getFiltrado();
    	String requestString = "http://localhost:8080/EjbJaxRS-web/rest/venta_Cab"; 
                String fecha=(String) filtros.get("fecha");
		System.out.println(fecha);
        String cliente=(String) filtros.get("cliente");
		System.out.println(cliente);
               
		
		
		if(fecha !=null || cliente!=null )
			requestString+="?";
                if (fecha!= null){
                    requestString+="fecha="+fecha;
                    if(cliente !=null)
                            requestString+="&";

                }
		
		if (cliente !=null) {
			requestString+="cliente="+cliente;
			
		}
 
    System.out.println(getFiltrado());
     
    FacesContext context = FacesContext.getCurrentInstance();  
    try {  
       context.getExternalContext().redirect(requestString);  
    }catch (Exception e) {  
       e.printStackTrace();  
    }  

        
    }

    
    
    public void register() throws Exception {
        try {
        	venta_CabRegistration.register(newVenta_Cab);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registrado!", "Registro exitoso"));
            initNewVenta_Cab();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro fallido");
            facesContext.addMessage(null, m);
        }
    }

    @PostConstruct
    public void initNewVenta_Cab() {
        newVenta_Cab = new Venta_Cab();
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "El registro ha fallado";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }


}
