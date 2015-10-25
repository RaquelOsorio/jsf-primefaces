package py.pol.una.ii.pw.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.service.Venta_CabRegistration;

@Model
public class Venta_CabController {

	@Inject
    private FacesContext facesContext;

    @Inject
    private Venta_CabRegistration venta_CabRegistration;

    private Venta_Cab newVenta_Cab;

    @Produces
    @Named
    public Venta_Cab getNewVenta_Cab() {
        return newVenta_Cab;
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
