package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class Compra_DetRegistration {

	@EJB
	private Compra_CabRegistration regiscab;
	
    @Inject
    private Logger log;

    @PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Compra_Det> compra_detEventSrc;


    public void register(Compra_Det producto) throws Exception {
        log.info("Registering " + producto.getProducto());
        em.persist(producto);
        compra_detEventSrc.fire(producto);
    }
    public void modificar(Compra_Det detalle) throws Exception {
        log.info("Registering " + detalle.getId());
        em.merge(detalle);
        compra_detEventSrc.fire(detalle);
    }
    public void remover(Compra_Det detalle) throws Exception {
        em.remove(em.contains(detalle) ? detalle : em.merge(detalle));
        
    }
}
