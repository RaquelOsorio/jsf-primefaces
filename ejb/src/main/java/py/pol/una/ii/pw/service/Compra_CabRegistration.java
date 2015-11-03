package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.Compra_DetRepository;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import com.google.gson.Gson;

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
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class Compra_CabRegistration {

    @Inject
    private Logger log;

    @PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Compra_Cab> cabeceraEventSrc;
    
    @Inject
    private Compra_DetRepository repository;
    
    @Inject
    private Compra_DetRegistration detalleRegistration;
    
    public void registrarCabecera(Compra_Cab cabecera) throws Exception {
        log.info("Registering " + cabecera.getFecha());
        em.persist(cabecera);
        cabeceraEventSrc.fire(cabecera);
    }
    
    
    public void modificar(Compra_Cab cabecera) throws Exception {
        log.info("Registering " + cabecera.getFecha());
        em.merge(cabecera);
        cabeceraEventSrc.fire(cabecera);
    }
    public void remover(Compra_Cab cabecera) throws Exception {
        em.remove(em.contains(cabecera) ? cabecera : em.merge(cabecera));
        
    }
    /**********************Registrar una compra**********************************************/
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String registrarCompra(Compra_Cab nuevaCabecera) throws Exception, EJBTransactionRolledbackException  {
     	Compra_Cab cabecera= new Compra_Cab();
    	Date fecha = new Date();
    	cabecera.setFecha(fecha);
    	cabecera.setProveedor(nuevaCabecera.getProveedor());
    	em.persist(cabecera);
        List<Compra_Det>  detalle= nuevaCabecera.getDetalleCompraList();
        Iterator<Compra_Det> it=null;
        it=detalle.iterator();
        while (it.hasNext())
        {	
        		Compra_Det aux = it.next();
        		Compra_Det cdetalle= new Compra_Det();
        		cdetalle.setCabecera(cabecera);
        		cdetalle.setCantidad(aux.getCantidad());
        		cdetalle.setProducto(aux.getProducto());
        		/***Actualiza el stock***/
        		Long pid= cdetalle.getProducto().getId();
        		Producto p= em.find(Producto.class, pid);
        		p.setStock(p.getStock()+aux.getCantidad());
        		em.merge(p);
        		detalleRegistration.register(cdetalle);
        }
        return "exito";
        
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
                Compra_Cab auxiliar;
                try {
                	auxiliar = gson.fromJson(line, Compra_Cab.class);
                	if (auxiliar.getDetalleCompraList() == null) {
                		errores = errores + "Sin detalles, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                	}
                	if (auxiliar.getDetalleCompraList().size()==0)
                	{
                		errores = errores + "Sin detalles, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                		
                	}	
                	if (auxiliar.getProveedor().getDescripcion().length()==0)
                	{
                		errores = errores + "datos incompletos, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                		
                	}
                	
                	if (auxiliar.getProveedor() == null) {
                		errores = errores + "Sin proveedor, linea: " + cantidadTotal.toString() + "\n";
                		error = true;
                		cantidadErrores++;
                	}
                	
                	Iterator<Compra_Det> it=null;
                	it=auxiliar.getDetalleCompraList().iterator();
                	//System.out.println("ITERATOR"+it.next().getCantidad());
                
                	while (it.hasNext())
                	{	
                		Compra_Det aux = it.next();
                		if(aux.getProducto().getProveedor().getId() != auxiliar.getProveedor().getId())
                		{
                			errores = errores + "El proveedor no coincide, linea: " + cantidadTotal.toString() + "\n";
                        	error = true;
                        	cantidadErrores++;
                		}
                		if(aux.getCantidad() <= 0)
                		{
                			errores = errores + "Cantidad menor o igual a 0, linea: " + cantidadTotal.toString() + "\n";
                        	error = true;
                        	cantidadErrores++;
                		}
                	
                	}
                 
                	
                }catch (Exception e) {
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
        		
        		Compra_Cab cabecera;
        		String linea = sccarga.nextLine();
        		cabecera = gson.fromJson(linea, Compra_Cab.class);
        		try {
                	registrarCompra(cabecera);
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
    	return "Carga Exitosa," + cantidadTotal + " compras cargadas";
    
    }	
    if (cantidadErrores > 0) {
        return errores;
    }
   return "";
        }

    public List<Compra_Cab> filtrar (FiltersObject filtros){
        String queryString;
        
        // Hacemos el select de la entidad cliente
        queryString = "SELECT o FROM Compra_Cab o " ;
        
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
        
        return em.createQuery(queryString, Compra_Cab.class)
                .setFirstResult(filtros.getFirst())
                .setMaxResults(filtros.getPageSize())
                .getResultList();
    }
    
    public int filtrarCantidadRegistros (FiltersObject filtros){
        String queryString;
        
        // Establece el nombre de la entidad que se esta buscando
        queryString = "SELECT COUNT( o ) FROM Compra_Cab o ";
        
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
