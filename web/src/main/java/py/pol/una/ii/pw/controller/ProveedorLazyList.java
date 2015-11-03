package py.pol.una.ii.pw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.BadRequestException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import py.pol.una.ii.pw.model.Proveedor;

@ManagedBean
public class ProveedorLazyList extends LazyDataModel<Proveedor>{  
     private static final long serialVersionUID = 1L;  
     private List<Proveedor> proveedores;
     private Gson gson = new Gson();
     private static Map<String,Object> filtrado = new HashMap();
     @Override
     public Proveedor getRowData(String rowKey) {
         String output="";
         try {
             ClientRequest request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/proveedores/" + rowKey);
             request.accept("application/json");
             ClientResponse<String> response = request.get(String.class);
      	     BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
             output = br.readLine();            
         }catch (ClientProtocolException e) {
 			e.printStackTrace();
 	}catch (IOException e) {
 			e.printStackTrace();
 	}catch (Exception e) {
 			e.printStackTrace();
 	}
         return gson.fromJson(output, Proveedor.class);
     }
     
     @Override
     public Object getRowKey(Proveedor prov) {
         return prov.getId();
     }
     
         public Map<String,Object> getFiltrado()
     {
         return ProveedorLazyList.filtrado;
     }
     
     public void setFiltrado(Map<String,Object> fil)
     {
    	 ProveedorLazyList.filtrado = fil;
     }
     
         public void exportarCSV() throws MalformedURLException, IOException
     {

         Map<String,Object> filtros = getFiltrado();
         String requestString = "http://localhost:8080/EjbJaxRS-web/rest/proveedores/filtrarCantidad/"; 
         String nombre=(String) filtros.get("descripcion");
 		System.out.println(nombre);
 		
 		
 		if (nombre !=null) {
 			requestString+="nombre="+nombre;
 			
 		}

  
         System.out.println(getFiltrado());
      
         FacesContext context = FacesContext.getCurrentInstance();  
         try {  
             context.getExternalContext().redirect(requestString);  
         }catch (Exception e) {  
             e.printStackTrace();  
         }          
     }
     
     @Override
     public List<Proveedor> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters){
         ClientRequest request;
         setFiltrado(filters);
         FiltersObject filtros = new FiltersObject(first, pageSize, sortField, sortOrder, filters);
         Type tipoFiltros = new TypeToken<FiltersObject>(){}.getType();
         String stringFiltros;
         String filtrosJson = gson.toJson(filtros, tipoFiltros);
         String output = "";
         ClientResponse<String> response;
         BufferedReader br;
         // Codificar el JSON para usar como parte del URL, de otra manera da error
         try {
             stringFiltros = URLEncoder.encode(filtrosJson, "UTF-8");
         } catch ( UnsupportedEncodingException e){
             System.err.println(e.getLocalizedMessage());
             stringFiltros = "";
         }
         //Recuperar la cantidad de registros de la tabla aplicando los filtros pero sin paginar.
         try {
        	 request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/proveedores/filtrarCantidad/" + stringFiltros);
             request.accept("application/json");
            response = request.get(String.class);
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
            output = br.readLine();
         } catch ( BadRequestException e ){
            e.printStackTrace();
            output = "";
         } catch (Exception ex) {
            Logger.getLogger(ProveedorLazyList.class.getName()).log(Level.SEVERE, null, ex);
         }
         System.out.println(output);
         this.setRowCount(gson.fromJson(output, int.class));
         // Recuperar la lista de registros luego de filtrar, ordenar y paginar
         try {
             request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/proveedores/filtrar/" + stringFiltros);
             request.accept("application/json");
             response = request.get(String.class);
             br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
             output = br.readLine();
         } catch ( BadRequestException e ){
             e.printStackTrace();
         } catch (Exception ex) {
             Logger.getLogger(ProveedorLazyList.class.getName()).log(Level.SEVERE, null, ex);
         }
             proveedores = gson.fromJson(output, new TypeToken<List<Proveedor>>(){}.getType());
             return proveedores;
         }  
}  