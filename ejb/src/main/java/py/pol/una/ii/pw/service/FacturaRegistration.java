package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.data.FacturaRepository;
import py.pol.una.ii.pw.data.Venta_CabRepository;
import py.pol.una.ii.pw.data.Venta_DetRepository;
import py.pol.una.ii.pw.model.Factura;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.SolicitudCompra;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.model.Venta_Det;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class FacturaRegistration {

	@Inject
    private Logger log;
	
	@Inject
    private Venta_CabRepository repository;
	
	@Inject
    private Venta_DetRepository detallerepository;

	@Inject 
	private FacturaRepository facturarepository;
	
	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Factura> facturaEventSrc;
   

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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Asynchronous
    public Future generarFactura(){
    	File carpeta= new File("/home/viviana/disenhoweb/jsf-primefaces/reportes");
    	carpeta.mkdirs();
    	try {
			Thread.sleep(60000);
			List<Venta_Cab> ventas= repository.findAllOrderedByVenta_Cab();
	   	  	Iterator<Venta_Cab> it=null;
	         it=ventas.iterator();
	         while (it.hasNext())
	         {		Venta_Cab aux = it.next();
	         		if(aux.getFactura()==null){
	         			Factura nuevafactura= new Factura();
	         			nuevafactura.setFecha(new Date());
	         			int monto_total=0;
	         			List <Venta_Det> detalles= detallerepository.findAllByCabecera(aux.getId());
	         			Iterator<Venta_Det> ite=null;
	         			ite=detalles.iterator();
	         			while(ite.hasNext()){
	         				Venta_Det detaux=ite.next();
	         				monto_total=monto_total+(detaux.getCantidad()*detaux.getProducto().getPrecio());
	         				
	         			}
	         			nuevafactura.setMonto_total(monto_total);
	         			try {
							register(nuevafactura);
							generarPDF(carpeta,nuevafactura,detalles,aux);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	         			aux.setFactura(nuevafactura);
	         			em.merge(aux);
	         		}
	         }
	         return new AsyncResult("Proceso Terminado"); 

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			 throw new RuntimeException(e1);
		}
    	    	
    	
    }
    public void generarPDF(File carpeta,Factura factura, List<Venta_Det> detalles, Venta_Cab cabecera) throws JRException{
    	//Connection conn = dsweb.getConnection();
    	//List <Venta_Det> facturas= facturarepository.findAllOrderedByFactura();
    	
    	Map<String,Object> parameters= new HashMap<String,Object>();
    	parameters.put("id", factura.getId());
    	parameters.put("fecha", factura.getFecha());
    	parameters.put("monto_total", factura.getMonto_total());
    	parameters.put("cliente", cabecera.getCliente().getNombre());
    	File jasper= new File("/home/viviana/disenhoweb/jsf-primefaces/reporteFactura.jasper");
    	JasperPrint jasperPrint= JasperFillManager.fillReport(jasper.getPath(), parameters,new JRBeanCollectionDataSource(detalles));
    	JasperExportManager.exportReportToPdfFile( jasperPrint, carpeta.getAbsolutePath()+"/factura"+factura.getId()+".pdf"); 
    	  
    }
}
