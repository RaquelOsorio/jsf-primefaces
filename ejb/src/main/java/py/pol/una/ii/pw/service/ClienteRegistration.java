package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Clientes;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
    	
    /*	 String cadena;
         FileReader f = new FileReader(direccion);
         BufferedReader b = new BufferedReader(f);
         while((cadena = b.readLine())!=null) {
             System.out.println(cadena);
         }
         b.close();
         return "";*/
       String errores = new String();
        int cantidadErrores = 0;
        String direccion = direc;
        FileReader fr;
        BufferedReader br;
        File archivo;
        String linea;
        Integer cantidadTotal = 0;
        archivo = new File(direccion);
        fr = new FileReader(archivo);
        br = new BufferedReader(fr);
        Gson gson = new Gson();
        boolean error = false;
        while (br.ready()) {
            linea = br.readLine();
            cantidadTotal++;
            Clientes clienteNuevo = new Clientes();
            Clientes cliente;
            try {
                cliente = gson.fromJson(linea, Clientes.class);
                if (cliente.getNombre() == null) {
                    errores = errores + "Cliente sin nombre, linea: " + cantidadTotal.toString() + "\n";
                    error = true;
                    cantidadErrores++;
                }
                
                if (cliente.getApellido() == null) {
                    errores = errores + "Cliente sin apellido, linea: " + cantidadTotal.toString() + "\n";
                    error = true;
                    cantidadErrores++;
                }

                clienteNuevo.setNombre(cliente.getNombre());
                clienteNuevo.setApellido(cliente.getApellido());
                if (!error) {
                    try {
                        register(clienteNuevo);
                    } catch (Exception e) {
                        errores = errores + " Error en la linea " + cantidadTotal.toString();
                        cantidadErrores++;

                    }
                }
            } catch (Exception e) {
                errores = errores + "Formato de archivo incorrecto \n";
                cantidadErrores++;

            }

            error = false;
        }

        System.out.println("TOTAL: " + cantidadTotal);
        System.out.println("Errores: " + cantidadErrores);
        System.out.println("ERRORES: " + errores);

        if (cantidadErrores > 0) {
            //context.setRollbackOnly();
            return errores;
        }
        return "Carga Exitosa," + cantidadTotal + " clientes cargados";
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
        // System.out.println("cantidad"+result.intValue());
        return result.intValue();
    }
    

    
    
}
