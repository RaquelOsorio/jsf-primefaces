package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.model.Proveedor;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ClienteRegistration {

	@Inject
    private Logger log;

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    @Inject
    private Event<Clientes> clienteEventSrc;
///////////////CREAR UN CLIENTE//////////////////////////////
    public void register(Clientes cliente) throws Exception {
        log.info("Registering " + cliente.getNombre());
        em.persist(cliente);
        clienteEventSrc.fire(cliente);
    }
    
    ////////////MODIFICAR CLIENTE/////////////////////////////
    
    public void modificar(Clientes cliente) throws Exception {
        log.info("Registering " + cliente.getNombre());
        em.merge(cliente);
        clienteEventSrc.fire(cliente);
    }
    
    ///////////ELIMINAR CLIENTE////////////////////////////////
    
    public void remover(Clientes cliente) throws Exception {
        em.remove(em.contains(cliente) ? cliente : em.merge(cliente));
        
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
                Clientes clienteNuevo = new Clientes();
                Clientes cliente;
                try {
                    cliente = gson.fromJson(line, Clientes.class);
                    if (cliente.getNombre() == null || cliente.getNombre().length() < 1 ) {
                        errores = errores + "Cliente sin nombre, linea: " + cantidadTotal.toString() + "\n";
                        error = true;
                        System.out.println(errores);
                        cantidadErrores++;
                    }
                    //if ((cliente.getNombre().equals(cliente.getNombre().toString())) ) {
                    //	errores = errores + "Tipo de dato Incorrecto, linea: " + cantidadTotal.toString() + "\n";
                    //   error = true;
                    //  cantidadErrores++;
                    //}
                    if (cliente.getApellido() == null || cliente.getApellido()=="") {
                        errores = errores + "Cliente sin apellido, linea: " + cantidadTotal.toString() + "\n";
                        error = true;
                        cantidadErrores++;
                    }
                } catch (Exception e) {
                    errores = errores + "Formato de archivo incorrecto \n";
                    error= true;
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
            		Clientes clienteNuevo = new Clientes();
            		Clientes cliente;
            		String linea = sccarga.nextLine();
            		cliente = gson.fromJson(linea, Clientes.class);
            		em.persist(cliente);
            		// System.out.println(line);
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
        	return "Carga Exitosa," + cantidadTotal + " clientes cargados";
        
        }	
        if (cantidadErrores > 0) {
            return errores;
        }
       return "";
        
        
        
        
    }

    public List<Clientes> filtrar(FiltersObject filtros) {
        String queryString;

        // Hacemos el select de la entidad cliente
        queryString = "SELECT o FROM Clientes o ";

        // Si el filtro es distinto de null ampliamos la consulta, con el filtro
        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty()) {
            queryString = queryString + "WHERE";
            for (Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext();) { //iteramos con el filtro
                try {
                    String atributo = it.next();
                    String valor = filtros.getFilters().get(atributo).toString();
                    if (atributo.equals("id")) {
                        int cantidad = Integer.parseInt(valor);
                        queryString = queryString + " " + atributo + " = " + cantidad; //si es un entero traemos lo menos o igual a ese numero
                    } else {
                        valor = "'%" + valor + "%'";
                        queryString = queryString + " " + atributo + " LIKE " + valor; //si el valor a filtrar es una cadena , traemos la misma cadena o la que le contiene
                    }

                } catch (Exception e) {

                }
            }
        }
        //Para el ordenamiento, se agrega el order by a la consulta con el campo que se ordena y la direccion de ordenamiento
        queryString = queryString + "ORDER BY o." + filtros.getSortField() + " " + filtros.getSortOrder();

        return em.createQuery(queryString, Clientes.class)
                .setFirstResult(filtros.getFirst())
                .setMaxResults(filtros.getPageSize())
                .getResultList();
    }

    public int filtrarCantidadRegistros(FiltersObject filtros) {
        String queryString;

        // Establece el nombre de la entidad que se esta buscando
        queryString = "SELECT COUNT( o ) FROM Clientes o ";

        // Si existen filtros los agrega a la consulta
        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty()) {
            queryString = queryString + "WHERE";
            for (Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext();) {
                try {
                    String atributo = it.next();
                    String valor = filtros.getFilters().get(atributo).toString();

                    if (atributo.equals("id")) {
                        int cantidad = Integer.parseInt(valor);
                        queryString = queryString + " " + atributo + " = " + cantidad;
                    } else {
                        valor = "'%" + valor + "%'";
                        queryString = queryString + " " + atributo + " LIKE " + valor;
                    }

                } catch (Exception e) {

                }
            }
        }
        Long result = em.createQuery(queryString, Long.class).getSingleResult();
        return result.intValue();
    }
    

    
    
}
