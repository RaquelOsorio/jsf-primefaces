package py.pol.una.ii.pw.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.pol.una.ii.pw.data.ProductoRepository;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.SolicitudCompra;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.service.ProductoRegistration;

@Stateless
public class SolicitudesCompraBean {
      @PersistenceContext(unitName="PersistenceApp")
      EntityManager em;
      @Inject
      ProductoRepository pm;
//      ProductoResourceRESTService pm;
      int limite = 10;

     
      public void generarNuevaSolicitud()
      {
          List<Producto> productos = pm.findAllOrderedByStock();
          System.out.println("GENERANDO NUEVAS SOLICITUDES..................");
          Iterator<Producto> it=null;
          it=productos.iterator();
          //Producto aux = it.next();
          while (it.hasNext() )
          {		Producto aux = it.next();
          		if (aux.getStock()>= limite){ 
          			break;
          		}
          		if(!(existeSolicitud(aux)))
          		{	
          			SolicitudCompra nuevaSolicitud = new SolicitudCompra();
          			nuevaSolicitud.setFecha(new Date());
          			nuevaSolicitud.setProducto(aux);
          			System.out.println(aux.getDetalle() + aux.getStock());
          			em.persist(nuevaSolicitud);
          		}
          }

      }
      /******************Listar todas las solicitudes********************************************/
      public List<SolicitudCompra> findAllSolicitudes() {
          CriteriaBuilder cb = em.getCriteriaBuilder();
          CriteriaQuery<SolicitudCompra> criteria = cb.createQuery(SolicitudCompra.class);
          Root<SolicitudCompra> solicitud = criteria.from(SolicitudCompra.class);
          criteria.select(solicitud).orderBy(cb.asc(solicitud.get("producto")));
          return em.createQuery(criteria).getResultList();
      }
      
      /******************Verificar solicitudes existentes*****************************************/
      public boolean existeSolicitud(Producto p){
    	  List<SolicitudCompra> solicitudes=findAllSolicitudes();
    	  Iterator<SolicitudCompra> it=null;
          it=solicitudes.iterator();
          while (it.hasNext())
          {		SolicitudCompra aux = it.next();
          System.out.println("verificar"+ aux.getProducto().getDetalle()+aux.getProducto().getStock());
          		if (aux.getProducto().getId()==p.getId()){
          			System.out.println("if"+aux.getProducto().getDetalle());
          			return true;
          		}
          		
          }
          System.out.println("false"+p.getDetalle());
    	  return false;
      }
      public void remover(SolicitudCompra solicitud) throws Exception {
          em.remove(em.contains(solicitud) ? solicitud : em.merge(solicitud));
          
      }
      
      
}
