package py.pol.una.ii.pw.controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import py.pol.una.ii.pw.model.Clientes;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.BadRequestException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
public class ClienteLazyList extends LazyDataModel<Clientes>{

    private List<Clientes> clientes;
    private Gson gson = new Gson();
    //private FiltersObject filtros = new FiltersObject();
    private static Map<String,Object> filtrado = new HashMap();
    @Override
    public Clientes getRowData(String rowKey) {
        String output="";
        try {
            ClientRequest request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/clientes/" + rowKey);
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
        return gson.fromJson(output, Clientes.class);
    }
    
    @Override
    public Object getRowKey(Clientes cliente) {
        return cliente.getId();
    }
    
//    public void exportarCSV() throws MalformedURLException, IOException
//    {
//
//   Map<String,Object> filtros = getFiltrado();
//   String requestString = "http://localhost:8080/SistemaCompraVenta/csv"; 
//    String nombre=(String) filtros.get("nombre");
//		System.out.println(nombre);
//		String apellidos=(String) filtros.get("apellidos");
//		System.out.println(apellidos);
//		String cedula=(String) filtros.get("cedula");
//		System.out.println(cedula);
//		String email=(String) filtros.get("email");
//		System.out.println(email);
//		
//		if(nombre!=null || apellidos!=null || cedula!=null || email!= null)
//			requestString+="?";
//		if (nombre !=null) {
//			requestString+="nombre="+nombre;
//			if(apellidos!=null || cedula!=null )
//				requestString+="&";
//		}
//		if (apellidos !=null) {
//			requestString+="apellidos="+apellidos;
//			if(cedula!=null || email!=null)
//				requestString+="&";
//		}
//		if (cedula !=null) {
//			requestString+="cedula="+cedula;
//                        if(email!=null)
//				requestString+="&";
//		}
//                if (email !=null) {
//			requestString+="email="+email;
//		
//		}
// 
//    System.out.println(getFiltrado());
//     
//    FacesContext context = FacesContext.getCurrentInstance();  
//    try {  
//       context.getExternalContext().redirect(requestString);  
//    }catch (Exception e) {  
//       e.printStackTrace();  
//    }  
//
//        
//    }
    
    public Map<String,Object> getFiltrado()
    {
        return ClienteLazyList.filtrado;
    }
    
    public void setFiltrado(Map<String,Object> fil)
    {
    	ClienteLazyList.filtrado = fil;
    }
    
    @Override
    public List<Clientes> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters){
    	System.out.println("\nentro en loas????");
        ClientRequest request;
        setFiltrado(filters);
        System.out.println(getFiltrado());
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
           request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/clientes/filtrarCantidad/" + stringFiltros);
           request.accept("application/json");
           response = request.get(String.class);
           br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
           output = br.readLine();
        } catch ( BadRequestException e ){
           e.printStackTrace();
           output = "";
        } catch (Exception ex) {
           Logger.getLogger(ClienteLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(output);
        this.setRowCount(gson.fromJson(output, int.class));
        // Recuperar la lista de registros luego de filtrar, ordenar y paginar
        try {
            request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/clientes/filtrar/" + stringFiltros);
            request.accept("application/json");
            response = request.get(String.class);
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
            output = br.readLine();
        } catch ( BadRequestException e ){
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(ClienteLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
            clientes = gson.fromJson(output, new TypeToken<List<Clientes>>(){}.getType());
            return clientes;
        }
}