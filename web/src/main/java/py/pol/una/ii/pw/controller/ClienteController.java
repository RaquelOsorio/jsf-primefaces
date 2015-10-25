package py.pol.una.ii.pw.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.service.ClienteRegistration;

@Model
public class ClienteController {

	@Inject
    private FacesContext facesContext;

    @Inject
    private ClienteRegistration clienteRegistration;

    private Clientes newCliente;

    @Produces
    @Named
    public Clientes getNewCliente() {
        return newCliente;
    }

    public void register() throws Exception {
        try {
            clienteRegistration.register(newCliente);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registrado!", "Registro exitoso"));
            initNewCliente();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registro fallido");
            facesContext.addMessage(null, m);
        }
    }

    @PostConstruct
    public void initNewCliente() {
        newCliente = new Clientes();
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
