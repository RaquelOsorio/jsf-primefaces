package py.pol.una.ii.pw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.BadRequestException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import py.pol.una.ii.pw.model.Compra_Cab;

@ManagedBean
public class Compra_CabLazyList extends LazyDataModel<Compra_Cab>{



    private List<Compra_Cab> compras;
    //private GsonBuilder gsonBuilder = new GsonBuilder();
    //gsonBuilder.setDateFormat("dd-MM-yyyy");
    //private Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
   // private Gson gson = gsonBuilder.create();
    //private Gson gson = new Gson();
    private FiltersObject filtros = new FiltersObject();
    private static Map<String,Object> filtrado = new HashMap();
    @Override
    public Compra_Cab getRowData(String rowKey) {
        String output="";
        try {
            ClientRequest request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/cabeceras/" + rowKey);
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
        return gson.fromJson(output, Compra_Cab.class);
    }
    
    @Override
    public Object getRowKey(Compra_Cab compra) {
        return compra.getId();
    }
    
//    public void exportarCSV() throws MalformedURLException, IOException
//    {
//
//   Map<String,Object> filtros = getFiltrado();
//   String requestString = "http://localhost:8080/SistemaCompraVenta/csvCompra"; 
//                String fecha=(String) filtros.get("fecha");
//		System.out.println(fecha);
//                String total=(String) filtros.get("total");
//		System.out.println(total);
//		String proveedor=(String) filtros.get("proveedor");
//		System.out.println(proveedor);
//               
//		
//		
//		if(fecha !=null || total!=null || proveedor!=null )
//			requestString+="?";
//                if (fecha!= null){
//                    requestString+="fecha="+fecha;
//                    if(total !=null || proveedor !=null)
//                            requestString+="&";
//
//                }
//		if (total !=null) {
//			requestString+="total="+total;
//			if(proveedor!=null )
//				requestString+="&";
//		}
//		if (proveedor !=null) {
//			requestString+="proveedor="+proveedor;
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
        return Compra_CabLazyList.filtrado;
    }
    
    public void setFiltrado(Map<String,Object> fil)
    {
    	Compra_CabLazyList.filtrado = fil;
    }
    
    @Override
    public List<Compra_Cab> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters){
        ClientRequest request;
        setFiltrado(filters);
        System.out.println(getFiltrado());
        this.filtros = new FiltersObject(first, pageSize, sortField, sortOrder, filters);
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
           request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/cabeceras/filtrarCantidad/" + stringFiltros);
           request.accept("application/json");
           response = request.get(String.class);
           br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
           output = br.readLine();
        } catch ( BadRequestException e ){
           e.printStackTrace();
           output = "";
        } catch (Exception ex) {
           Logger.getLogger(Compra_CabLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(output);
        this.setRowCount(gson.fromJson(output, int.class));
        // Recuperar la lista de registros luego de filtrar, ordenar y paginar
        try {
            request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/cabeceras/filtrar/" + stringFiltros);
            request.accept("application/json");
            response = request.get(String.class);
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
            output = br.readLine();
        } catch ( BadRequestException e ){
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(Compra_CabLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
            compras = gson.fromJson(output, new TypeToken<List<Compra_Cab>>(){}.getType());
            return compras;
        }

}
