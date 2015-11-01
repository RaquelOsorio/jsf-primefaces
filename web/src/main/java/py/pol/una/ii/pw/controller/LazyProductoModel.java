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
import py.pol.una.ii.pw.model.Producto;

@ManagedBean
public class LazyProductoModel extends LazyDataModel<Producto> {

    private List<Producto> productos;
    private Gson gson = new Gson();
    private static Map<String,Object> filtrado = new HashMap();
    
    @Override
    public Producto getRowData(String rowKey) {
        String output="";
        try {
            ClientRequest request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/productos/" + rowKey);
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
        return gson.fromJson(output, Producto.class);
    }
    
    public Map<String,Object> getFiltrado()
    {
        return LazyProductoModel.filtrado;
    }
    
    public void setFiltrado(Map<String,Object> fil)
    {
        LazyProductoModel.filtrado = fil;
    }
    
    @Override
    public Object getRowKey(Producto producto) {
        return producto.getId();
    }
    
    public void exportarCSV() throws MalformedURLException, IOException
    {

        Map<String,Object> filtros = getFiltrado();
        String requestString = ""; 
        String nombre=(String) filtros.get("detalle");
		System.out.println(nombre);
		String cant=(String) filtros.get("stock");
		System.out.println(cant);
		String precioUnitario=(String) filtros.get("precio");
		System.out.println(precioUnitario);
                String proveedor=(String) filtros.get("proveedor");
		System.out.println(proveedor);
		
		if(nombre!=null || cant!=null || precioUnitario!= null 
                        || proveedor!= null)
			requestString+="?";
		if (nombre !=null) {
			requestString+="nombre="+nombre;
			if(cant!=null || precioUnitario!= null 
                        || proveedor!= null)
				requestString+="&";
		}
		if (cant !=null) {
			requestString+="cantidad="+cant;
			if(precioUnitario!= null 
                        || proveedor!= null)
				requestString+="&";
		}
		if (precioUnitario !=null) {
			requestString+="precioUnitario="+precioUnitario;
                       if( proveedor!= null)
				requestString+="&";
                }
                
		
                       if(proveedor!= null)
                    	   	requestString+="&";
                
                
		if (proveedor!= null) {
			requestString+="proveedor="+proveedor;
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
    public List<Producto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters){
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
           request = new ClientRequest("");
           request.accept("application/json");
           response = request.get(String.class);
           br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
           output = br.readLine();
        } catch ( BadRequestException e ){
           e.printStackTrace();
           output = "";
        } catch (Exception ex) {
           Logger.getLogger(LazyProductoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(output);
        this.setRowCount(gson.fromJson(output, int.class));
        
        // Recuperar la lista de registros luego de filtrar, ordenar y paginar
        try {
            request = new ClientRequest("");
            request.accept("application/json");
            response = request.get(String.class);
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
            output = br.readLine();
        } catch ( BadRequestException e ){
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(LazyProductoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            productos = gson.fromJson(output, new TypeToken<List<Producto>>(){}.getType());
            return productos;
        }

}
