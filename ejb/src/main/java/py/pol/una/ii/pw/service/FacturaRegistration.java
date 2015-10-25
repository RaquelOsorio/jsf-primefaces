package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Factura;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FacturaRegistration {

	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Factura> facturaEventSrc;
///////////////CREAR UN CLIENTE//////////////////////////////
    public void register(Factura factura) throws Exception {
        log.info("Registering " + factura.getId());
        em.persist(factura);
        facturaEventSrc.fire(factura);
    }
    public void modificar(Factura factura) throws Exception {
        log.info("Registering " + factura.getId());
        em.merge(factura);
        facturaEventSrc.fire(factura);
    }
    
    public void remover(Factura factura) throws Exception {
        em.remove(em.contains(factura) ? factura : em.merge(factura));
        
    }
}
