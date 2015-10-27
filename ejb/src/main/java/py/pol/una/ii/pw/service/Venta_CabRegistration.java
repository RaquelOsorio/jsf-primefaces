package py.pol.una.ii.pw.service;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.data.Venta_DetRepository;
import py.pol.una.ii.pw.model.Clientes;

import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.model.Compra_Det;

import py.pol.una.ii.pw.model.Venta_Det;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless

public class Venta_CabRegistration {

	@Inject
    private Logger log;

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;
    
    @Inject
    private Venta_DetRepository repository;
    
    @Inject
    private Venta_DetRegistration detalleRegistration;

    @Inject
    private Event<Venta_Cab> venta_CabEventSrc;

    public void register(Venta_Cab venta_Cab) throws Exception {
        log.info("Registering " + venta_Cab.getId());
        em.persist(venta_Cab);
        venta_CabEventSrc.fire(venta_Cab);
    }

    public void modificar(Venta_Cab cabecera) throws Exception {
        log.info("Registering " + cabecera.getId());
        em.merge(cabecera);
        venta_CabEventSrc.fire(cabecera);
    }
    public void remover(Venta_Cab cabecera) throws Exception {
        em.remove(em.contains(cabecera) ? cabecera : em.merge(cabecera));
        
    }
    /**********************Registrar una venta**********************************************/
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registrarVenta(Venta_Cab nuevacabecera) throws Exception {
    	Venta_Cab cabecera= new Venta_Cab();
    	Date fecha = new Date();
    	cabecera.setFecha(fecha);
    	cabecera.setFactura(null);
    	cabecera.setCliente(nuevacabecera.getCliente());
    	em.persist(cabecera);
    	 
        List<Venta_Det>  detalle= nuevacabecera.getDetalleVenta();
        System.out.println("ENTRA EN COMPRAAAAAA"+ detalle.size());
        //int cantidad=detalle.size();
        
        Iterator<Venta_Det> it=null;
        it= detalle.iterator();
        //System.out.println("ITERATOR"+it.next().getCantidad());
        
        while (it.hasNext())
        {	
        	Venta_Det aux = it.next();
        	Venta_Det cdetalle= new Venta_Det();
        	cdetalle.setCabecera(cabecera);
        	cdetalle.setCantidad(aux.getCantidad());
        	cdetalle.setProducto(aux.getProducto());
        	detalleRegistration.register(cdetalle);
        	//DetalleVenta auxiliar= em.find(DetalleVenta.class, aux.getId());
        	//em.remove(em.contains(auxiliar) ? auxiliar : em.merge(auxiliar));
        	//System.out.println("DETALLE cantidad"+aux.getCantidad());
        	//detalle.remove(it.next());
        	
        }
       
        
    }
    
    

    
    
    
    
    
}
