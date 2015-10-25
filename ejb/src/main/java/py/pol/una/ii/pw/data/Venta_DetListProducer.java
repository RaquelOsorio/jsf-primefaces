package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Venta_Det;


@RequestScoped
public class Venta_DetListProducer {

	@Inject
    private Venta_DetRepository venta_DetRepository;

    private List<Venta_Det> venta_Det;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Venta_Det> getVenta_Det() {
        return venta_Det;
    }

    public void onVenta_DetListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Venta_Det venta_Det) {
        retrieveAllVenta_DetOrderedByDetalle();
    }

    @PostConstruct
    public void retrieveAllVenta_DetOrderedByDetalle() {
    	venta_Det = venta_DetRepository.findAllOrderedByVenta_Det();
    }
}
