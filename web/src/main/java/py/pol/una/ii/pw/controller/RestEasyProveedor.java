package py.pol.una.ii.pw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect.Type;
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

import py.pol.una.ii.pw.model.Proveedor;

/**
 *
 * @author usuario
 */
@ManagedBean(name= "proveedor")
public class RestEasyProveedor {
    
    
    private String descripcion;
    

    private String descripcionM;
    
    
    LazyDataModel<Proveedor> lazyModel ;
    
    public LazyDataModel<Proveedor> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Proveedor> lazyModel) {
        this.lazyModel = lazyModel;
    }

   
    
    
    
    public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcionM() {
		return descripcionM;
	}

	public void setDescripcionM(String descripcionM) {
		this.descripcionM = descripcionM;
	}

	public void seleccionarUnProveedor(Integer Id) {
		try {
                        String idModificar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/SistemaCompraVenta/rest/proveedores/" + idModificar);
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
                                Proveedor prov = gson.fromJson(output, Proveedor.class);
				System.out.println(prov.getDescripcion());
                                descripcionM = prov.getDescripcion();
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
                
	}
    
        public List<Proveedor> getProveedores() throws IOException {
		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/proveedores");
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
                                java.lang.reflect.Type tipoListaProveedores = new TypeToken<List<Proveedor>>(){}.getType();
                                List<Proveedor> proveedores = gson.fromJson(output, tipoListaProveedores);
                                //assertNotNull(proveedores);
                                //List<Proveedor> prov = gson.fromJson(output, List<Proveedor.class>);
				//System.out.println(prov.getNombre());
                                return proveedores;
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
        
        public void setProveedor() {

		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/SistemaCompraVenta/rest/proveedores");
			request.accept("application/json");
                        
                        Proveedor pr = new Proveedor();
                        pr.setDescripcion(descripcion);
                       
			/*String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
			request.body("application/json", input);*/
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(pr));
                                
			ClientResponse<String> response = request.post(String.class);
                        FacesMessage msg = new FacesMessage("Proveedor", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        descripcion = null;
                       

			

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
        public void deleteProveedor(Integer Id) {

		try {
                        String idEliminar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/proveedores/" + idEliminar );
			
                        
			ClientResponse<String> response = request.delete(String.class);
                        FacesMessage msg = new FacesMessage("Proveedor", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);

			/*if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}*/			

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
        public void modificarProveedor() {

               try {

                        
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/SistemaCompraVenta/rest/proveedores");
			request.accept("application/json");
                        
                        /*Proveedor pr = new Proveedor();*/
                        Proveedor proveedorModificado = new Proveedor();
                        
                        proveedorModificado.setDescripcion(descripcionM);
                        

			/*String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
			request.body("application/json", input);*/
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(proveedorModificado));
                                
			ClientResponse<String> response = request.put(String.class);
                        FacesMessage msg = new FacesMessage("Proveedor", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        descripcionM = null;
                        

			/*if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}*/


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
        lazyModel = new ProveedorLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount());   
    }

}
