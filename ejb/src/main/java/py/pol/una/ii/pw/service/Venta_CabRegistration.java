package py.pol.una.ii.pw.service;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.gson.Gson;

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.data.Venta_DetRepository;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Venta_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Venta_Det;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;

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

    public void registrarVenta(Venta_Cab nuevacabecera) throws Exception, EJBTransactionRolledbackException  {
    	Venta_Cab cabecera= new Venta_Cab();
    	Date fecha = new Date();
    	cabecera.setFecha(fecha);
    	//cabecera.setFactura(null);
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
        	//detalleRegistration.register(cdetalle);
        	/***Actualiza el stock***/
    		Long pid= cdetalle.getProducto().getId();
    		Producto p= em.find(Producto.class, pid);
    		p.setStock(p.getStock()-aux.getCantidad());
    		em.merge(p);
        	em.persist(cdetalle);
        	
        }
       
        
    }
    public String cargaMasiva(String direc) throws FileNotFoundException, IOException {
    	String errores = new String();
        int cantidadErrores = 0;
        Integer cantidadTotal = 0;
        Gson gson = new Gson();
        boolean error = false;
        FileInputStream inputStream = null;
        Scanner sc = null;
        FileInputStream carga = null;
        Scanner sccarga = null;
        try {
            inputStream = new FileInputStream(direc);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                cantidadTotal++;
                Venta_Cab auxiliar;
                try {
   
                	auxiliar = gson.fromJson(line, Venta_Cab.class);
                	if (auxiliar.getDetalleVenta() == null) {
                		errores = errores + "Sin detalles, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                	}
                	if (auxiliar.getDetalleVenta().size()==0)
                	{
                		errores = errores + "Sin detalles, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                		
                	}	
                	
                	if (auxiliar.getCliente().getNombre().length()==0 || auxiliar.getCliente().getApellido().length()==0)
                	{
                		errores = errores + "datos incompletos, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                		
                	}
                	if (auxiliar.getCliente() == null) {
                		errores = errores + "No se cargó el cliente, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                	}
                
                	
                	Iterator<Venta_Det> it=null;
                	it=auxiliar.getDetalleVenta().iterator();
                	//System.out.println("ITERATOR"+it.next().getCantidad());
                
                	while (it.hasNext())
                	{	
                		Venta_Det aux = it.next();
                		if(aux.getCantidad() <= 0)
                		{
                			errores = errores + "Cantidad menor o igual a 0, linea: " + cantidadTotal.toString() + "\n";
                        	error = true;
                        	cantidadErrores++;
                		}
                		Long pid= aux.getProducto().getId();
                		Producto p= em.find(Producto.class, pid);
                		
                		if((p.getStock()-aux.getCantidad()) < 0)
                		{
                			errores = errores + "No hay stock suficiente, linea: " + cantidadTotal.toString() + "\n";
                        	error = true;
                        	cantidadErrores++;
                		}
                	
                	}
                             
                
                
            } catch (Exception e) {
                errores = errores + "Formato de archivo incorrecto \n";
                error=true;
                cantidadErrores++;

            }

        }

            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        if (!error)
        {	try {
            	carga = new FileInputStream(direc);
            	sccarga = new Scanner(carga, "UTF-8");
            	while (sccarga.hasNextLine()) {
            		
            		Venta_Cab cabecera;
            		String linea = sccarga.nextLine();
            		cabecera = gson.fromJson(linea, Venta_Cab.class);
            		try {
                    	registrarVenta(cabecera);
                    } 
                    catch (Exception e) {
                        errores = errores + " Error en la linea " + cantidadTotal.toString();
                        cantidadErrores++;

                    }
            	}
            	// note that Scanner suppresses exceptions
            	if (sccarga.ioException() != null) {
            		throw sccarga.ioException();
            	}
        	} finally {
        		if (carga != null) {
        			carga.close();
        		}
        		if (sccarga != null) {
        			sccarga.close();
        		}
        	}
        	return "Carga Exitosa," + cantidadTotal + " ventas cargadas";
        
        }	
        if (cantidadErrores > 0) {
            return errores;
        }
       return "";
     }

    
    public List<Venta_Cab> filtrar (FiltersObject filtros){
        String queryString;
        
        // Hacemos el select de la entidad cliente
        queryString = "SELECT o FROM Venta_Cab o " ;
        
        // Si el filtro es distinto de null ampliamos la consulta, con el filtro
        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty() ){
            queryString = queryString + "WHERE";
            for ( Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext(); ){ //iteramos con el filtro
                try{
                    String atributo = it.next();
                    String valor = filtros.getFilters().get(atributo).toString();
                    if (  atributo.equals("id") || atributo.equals("total") ){
                        int cantidad = Integer.parseInt(valor);
                        queryString = queryString + " " + atributo + " = " + cantidad; //si es un entero traemos lo menos o igual a ese numero
                    } else {
                        valor = "'%" + valor + "%'";
                        queryString = queryString + " " + atributo + " LIKE " + valor; //si el valor a filtrar es una cadena , traemos la misma cadena o la que le contiene
                    }
                    
                } catch ( Exception e ){
                    
                }
            }
        }
        //Para el ordenamiento, se agrega el order by a la consulta con el campo que se ordena y la direccion de ordenamiento
        queryString = queryString + "ORDER BY o." + filtros.getSortField() + " " + filtros.getSortOrder();
        
        return em.createQuery(queryString, Venta_Cab.class)
                .setFirstResult(filtros.getFirst())
                .setMaxResults(filtros.getPageSize())
                .getResultList();
    }
    
    public int filtrarCantidadRegistros (FiltersObject filtros){
        String queryString;
        
        // Establece el nombre de la entidad que se esta buscando
        queryString = "SELECT COUNT( o ) FROM Venta_Cab o ";
        
        // Si existen filtros los agrega a la consulta
        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty() ){
            queryString = queryString + "WHERE";
            for ( Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext(); ){
                try{
                    String atributo = it.next();
                    String valor = filtros.getFilters().get(atributo).toString();
                    
                    if (atributo.equals("id") || atributo.equals("total") ){
                        int cantidad = Integer.parseInt(valor);
                        queryString = queryString + " " + atributo + " = " + cantidad;
                    } else {
                        valor = "'%" + valor + "%'";
                        queryString = queryString + " " + atributo + " LIKE " + valor;
                    }
                
                } catch ( Exception e ){
                    
                }
            }
        }
        Long result = em.createQuery(queryString, Long.class).getSingleResult();
       // System.out.println("cantidad"+result.intValue());
        return result.intValue();
    }    

    
    
    
    
    
}
