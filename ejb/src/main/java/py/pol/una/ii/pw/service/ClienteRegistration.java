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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    
    
    
    public String cargaMasiva(String direccion) throws FileNotFoundException, IOException {
    	
    	 String cadena;
    	 System.out.println("llegaaaaaaaaaa????????");
         FileReader f = new FileReader(direccion);
         System.out.println("No llegaaaaaaaaaa????????");
         BufferedReader b = new BufferedReader(f);
         while((cadena = b.readLine())!=null) {
             System.out.println(cadena);
         }
         b.close();
         return "";
/*        String errores = new String();
        int cantidadErrores = 0;
        String direccion = "/home/viviana/IIN2015/2doSemestre/web/EJB/tpWeb/ejb/src/main/java/py/pol/una/ii/pw/service/cliente.txt";
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
                        em.persist(clienteNuevo);
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
        return "Carga Exitosa," + cantidadTotal + " clientes cargados";*/
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
