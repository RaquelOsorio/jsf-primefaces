package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Clientes;


@RequestScoped
public class ClienteListProducer {

	@Inject
    private ClienteRepository clienteRepository;

    private List<Clientes> cliente;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Clientes> getCliente() {
        return cliente;
    }

    public void onClienteListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Clientes cliente) {
        retrieveAllClientesOrderedByDetalle();
    }

    @PostConstruct
    public void retrieveAllClientesOrderedByDetalle() {
        cliente = clienteRepository.findAllOrderedByNombre();
    }
}
