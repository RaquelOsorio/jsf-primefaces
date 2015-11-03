package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Factura;

@RequestScoped
public class FacturaListProducer {
	@Inject
    private FacturaRepository facturaRepository;

    private List<Factura> factura;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Factura> getFactura() {
        return factura;
    }

    public void onFacturaListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Factura factura) {
        retrieveAllFacturasOrderedByDetalle();
    }

    @PostConstruct
    public void retrieveAllFacturasOrderedByDetalle() {
        factura = facturaRepository.findAllOrderedByFactura();
    }

}
