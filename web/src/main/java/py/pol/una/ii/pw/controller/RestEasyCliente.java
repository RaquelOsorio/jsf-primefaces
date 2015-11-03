package py.pol.una.ii.pw.controller;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.primefaces.model.LazyDataModel;

import py.pol.una.ii.pw.model.Clientes;
/**
 *
 * @author usuario
 */
@ManagedBean(name="clientBean")
public class RestEasyCliente {
    
    private String nombre;
    private String apellido;
    
    private Long idM;
    private String nombreM;
    private String apellidosM;
    
    LazyDataModel<Clientes> lazyModel ;
    
    public LazyDataModel<Clientes> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Clientes> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }


    public String getNombreM() {
        return nombreM;
    }

    public void setNombreM(String nombreM) {
        this.nombreM = nombreM;
    }

    public String getApellidosM() {
        return apellidosM;
    }

    public void setApellidosM(String apellidosM) {
        this.apellidosM = apellidosM;
    }

    public void seleccionarUnCliente(Integer Id) {
		try {
                        String idModificar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/clientes/" + idModificar);
			request.accept("application/json");
			ClientResponse<String> response = request.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
                        
                        System.out.println(response);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			if ((output = br.readLine()) != null) {
                                Gson gson = new Gson();
                                Clientes prov = gson.fromJson(output, Clientes.class);
				System.out.println(prov.getNombre());
                                idM= prov.getId();
                                nombreM = prov.getNombre();
                                apellidosM = prov.getApellido();
                               
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
                
	}
    
        public List<Clientes> getClientes() throws IOException {
		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/clientes");
			request.accept("application/json");
			ClientResponse<String> response = request.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
                        
                        System.out.println(response);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			
			if ((output = br.readLine()) != null) {
                                Gson gson = new Gson();
                                java.lang.reflect.Type tipoListaClientes = new TypeToken<List<Clientes>>(){}.getType();
                                List<Clientes> clientes = gson.fromJson(output, tipoListaClientes);
                                
                                return clientes;
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
                return null;
	}
        
        public void setCliente() {

		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/clientes");
			request.accept("application/json");
                        
                        Clientes pr = new Clientes();
                        pr.setNombre(nombre);
                        pr.setApellido(apellido);
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(pr));
                                
			ClientResponse<String> response = request.post(String.class);
                        FacesMessage msg = new FacesMessage("Cliente", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        nombre= null;
                        apellido=null;
   
			

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
    public void eliminarCliente(Long id)
    {
    	
    }
        
        public void deleteCliente(Integer Id) {

		try {
                        String idEliminar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/clientes/eliminar/" + idEliminar );
			
                        
			ClientResponse<String> response = request.delete(String.class);
                        FacesMessage msg = new FacesMessage("Cliente", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);

					

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
        public void modificarCliente() {

               try {

                        
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/clientes");
			request.accept("application/json");
                        
                        /*Cliente pr = new Cliente();*/
                        Clientes clienteModificado = new Clientes();
                        clienteModificado.setId(idM);
                        clienteModificado.setNombre(nombreM);
                        clienteModificado.setApellido(apellidosM);
                        
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(clienteModificado));
                                
			ClientResponse<String> response = request.put(String.class);
                        FacesMessage msg = new FacesMessage("Clientes", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);

                        idM = null;
                        nombreM = null;
                        apellidosM = null;
                        
                               


		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
           }
        
    @PostConstruct
    public void init(){
        lazyModel = new ClienteLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount());   
    }
}