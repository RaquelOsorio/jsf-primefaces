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

import py.pol.una.ii.pw.model.Venta_Cab;

@ManagedBean
public class VentaLazyList extends LazyDataModel<Venta_Cab>{

    private List<Venta_Cab> ventas;
    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
   
    private FiltersObject filtros = new FiltersObject();
    private static Map<String,Object> filtrado = new HashMap();
    @Override
    public Venta_Cab getRowData(String rowKey) {
        String output="";
        try {
            ClientRequest request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/ventas/" + rowKey);
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
        return gson.fromJson(output, Venta_Cab.class);
    }
    
    @Override
    public Object getRowKey(Venta_Cab venta) {
        return venta.getId();
    }
    
//    public void exportarCSV() throws MalformedURLException, IOException
//    {
//
//   Map<String,Object> filtros = getFiltrado();
//   String requestString = "http://localhost:8080/SistemaCompraVenta/csvVenta"; 
//                String fecha=(String) filtros.get("fecha");
//		System.out.println(fecha);
//                String total=(String) filtros.get("total");
//		System.out.println(total);
//		String cliente=(String) filtros.get("cliente");
//		System.out.println(cliente);
//               
//		
//		
//		if(fecha !=null || total!=null || cliente!=null )
//			requestString+="?";
//                if (fecha!= null){
//                    requestString+="fecha="+fecha;
//                    if(total !=null || cliente !=null)
//                            requestString+="&";
//
//                }
//		if (total !=null) {
//			requestString+="total="+total;
//			if(cliente!=null )
//				requestString+="&";
//		}
//		if (cliente !=null) {
//			requestString+="cliente="+cliente;
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
        return VentaLazyList.filtrado;
    }
    
    public void setFiltrado(Map<String,Object> fil)
    {
    	VentaLazyList.filtrado = fil;
    }
    
    @Override
    public List<Venta_Cab> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters){
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
           request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/ventas/filtrarCantidad/" + stringFiltros);
           request.accept("application/json");
           response = request.get(String.class);
           br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
           output = br.readLine();
        } catch ( BadRequestException e ){
           e.printStackTrace();
           output = "";
        } catch (Exception ex) {
           Logger.getLogger(VentaLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.setRowCount(gson.fromJson(output, int.class));
        // Recuperar la lista de registros luego de filtrar, ordenar y paginar
        try {
            request = new ClientRequest("http://localhost:8080/EjbJaxRS-web/rest/ventas/filtrar/" + stringFiltros);
            request.accept("application/json");
            response = request.get(String.class);
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
            output = br.readLine();
        } catch ( BadRequestException e ){
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(VentaLazyList.class.getName()).log(Level.SEVERE, null, ex);
        }
            ventas = gson.fromJson(output, new TypeToken<List<Venta_Cab>>(){}.getType());
            System.out.println(ventas.get(0).getCliente());
                    
            return ventas;
        }
}
