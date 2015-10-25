package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.Venta_Det;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless

public class Venta_DetRegistration {

	@Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Venta_Det> venta_DetEventSrc;
///////////////CREAR UN CLIENTE//////////////////////////////
    public void register(Venta_Det venta_Det) throws Exception {
        log.info("Registering " + venta_Det.getId());
        em.persist(venta_Det);
        venta_DetEventSrc.fire(venta_Det);
    }
    public void modificar(Venta_Det detalle) throws Exception {
        log.info("Registering " + detalle.getId());
        em.merge(detalle);
        venta_DetEventSrc.fire(detalle);
    }
    
    public void remover(Venta_Det detalle) throws Exception {
        em.remove(em.contains(detalle) ? detalle : em.merge(detalle));
        
    }
/*    
    ////////////MODIFICAR CLIENTE/////////////////////////////
    
    public String editarCliente(Cliente cliente) {
        //entityManager.merge(customer);
        em.merge(cliente);
        return ("Modificado Exitosamente");
    }
    
    ///////////ELIMINAR CLIENTE////////////////////////////////
    
    public String borrarCliente(Long id) throws EJBTransactionRolledbackException {
        
        Cliente cliente = em.find(Cliente.class, id);
        if (null != cliente) {
            //System.out.println("LLEGO ACA");
            em.remove(cliente);
        }

        return "Eliminado Exitosamente";
    }
    
*/
}
