package py.pol.una.ii.pw.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import py.pol.una.ii.pw.model.Factura;
import py.pol.una.ii.pw.service.FacturaRegistration;

@Model
public class FacturaController {

	@Inject
    private FacesContext facesContext;

    @Inject
    private FacturaRegistration facturaRegistration;

    private Factura newFactura;

    @Produces
    @Named
    public Factura getNewFactura() {
        return newFactura;
    }

    public void register() throws Exception {
        try {
            facturaRegistration.register(newFactura);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registrado!", "Registro exitoso"));
            initNewFactura();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro fallido");
            facesContext.addMessage(null, m);
        }
    }

    @PostConstruct
    public void initNewFactura() {
        newFactura = new Factura();
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
