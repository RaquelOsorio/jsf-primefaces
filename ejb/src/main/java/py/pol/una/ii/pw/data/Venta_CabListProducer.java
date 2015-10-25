package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Venta_Cab;


@RequestScoped
public class Venta_CabListProducer {

	@Inject
    private Venta_CabRepository venta_CabRepository;

    private List<Venta_Cab> venta_Cab;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Venta_Cab> getVenta_Cab() {
        return venta_Cab;
    }

    public void onVenta_CabListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Venta_Cab venta_Cab) {
        retrieveAllVenta_CabOrderedByDetalle();
    }

    @PostConstruct
    public void retrieveAllVenta_CabOrderedByDetalle() {
    	venta_Cab = venta_CabRepository.findAllOrderedByVenta_Cab();
    }
}
