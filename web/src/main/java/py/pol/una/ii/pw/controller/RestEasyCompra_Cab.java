package py.pol.una.ii.pw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import py.pol.una.ii.pw.model.Compra_Cab;
import py.pol.una.ii.pw.model.Compra_Det;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

@ManagedBean
@ViewScoped
public class RestEasyCompra_Cab implements Serializable {

    private final Date fechaDate = new Date();
    SimpleDateFormat fechaFormato = new SimpleDateFormat("dd/mm/yyyy");
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private String fecha = df.format(fechaDate);
    private Proveedor idProveedor;
    private List<Compra_Det> detalles;
   // private DetalleCompra detalleCompra;
    private Integer total=0;
    private Compra_Det detalle;
    private List<Producto> productos;

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Compra_Det getDetalle() {
        return detalle;
    }
/*
    public void setDetalle(DetalleCompra detalle) {
        this.detalle = detalle;
    }*/

    public Proveedor getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }

    

    public List<Compra_Det> getDetalles() {
        return detalles;
    }
/*
    public void setProducto(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }*/

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    @PostConstruct
    public void init() {
        detalle = new Compra_Det();
        detalles = new ArrayList<Compra_Det>();
         
    }
    
     public void createNew() {
        
         System.out.println("Llego a crear" + detalle.getProducto().getDetalle() + " " + detalle.getCantidad());
         //   detalles.add(detalle);
            detalle = new Compra_Det();
            Integer tot = detalle.getProducto().getPrecio()* detalle.getCantidad();
            this.setTotal(total + tot);
            
    }
     public String reinit() {
          System.out.println("Llego a crear" + detalle.getProducto().getDetalle() + " " + detalle.getCantidad());
         
       // detalles.add(detalle);
         Integer tot = detalle.getProducto().getPrecio()* detalle.getCantidad();
        setTotal(total + tot);
         detalle = new Compra_Det();
        detalle.setCantidad(0);
        detalle.setProducto(null);
        return null;
    }
     
     public void borrar()
     {
         Integer tot =0;
         System.out.println("llego a borrar");
         for (Compra_Det detalleActual: detalles)
         {
             tot = tot + detalleActual.getProducto().getPrecio()*detalleActual.getCantidad();
         }
         setTotal(tot);
     }
    
     @Override
    public boolean equals(Object o) 
    { 
        if (this == o) 
            return true; 
        if (o == null || getClass() != o.getClass()) 
            return false; 
         if (detalle!= null ? !detalle.equals(this.detalle) : this.detalle != null) 
             return false;

         return true; 
     }  

     @Override 
     public int hashCode() 
     { 
         int result = detalle.hashCode(); 

         return result; 
     } 
    
//    public List<OrdenCompra> getOrdenCompra() throws IOException {
//
//        try {
//
//            ClientRequest request = new ClientRequest(
//                    "http://localhost:8080/SistemaCompraVenta/rest/compra/ordencompra");
//            request.accept("application/json");
//            ClientResponse<String> response = request.get(String.class);
//
//            /*if (response.getStatus() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + response.getStatus());
//            }*/
//
//            System.out.println(response);
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    new ByteArrayInputStream(response.getEntity().getBytes())));
//
//            String output;
//            //System.out.println("Output from Server .... \n");
//            Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//            if ((output = br.readLine()) != null) {
//                System.out.println(output);
//                java.lang.reflect.Type tipoListaOrdenCompra = new TypeToken<List<OrdenCompra>>() {}.getType();
//                List<OrdenCompra> ordencompra = gson.fromJson(output, tipoListaOrdenCompra);
//                return ordencompra;
//
//            }
//
//        } catch (MalformedURLException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//        return null;
//
//    }
//    
//    public void /*List<Producto>*/ onProveedorChange() throws IOException {
//		try {
//                        /*String idSeleccionado = "";
//                        while (idProveedor.getId() == null){
//                                idSeleccionado = Integer.toString(idProveedor.getId());
//                        }*/
//                    
//                    if( idProveedor !=null ){
//                        String  idSeleccionado = Integer.toString(idProveedor.getId());
//			ClientRequest request = new ClientRequest(
//					"http://localhost:8080/SistemaCompraVenta/rest/compra/findByProveedor/" + idSeleccionado );
//			request.accept("application/json");
//			ClientResponse<String> response = request.get(String.class);
//
//			/*if (response.getStatus() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ response.getStatus());
//			}*/
//                        
//                        //System.out.println(response);
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					new ByteArrayInputStream(response.getEntity().getBytes())));
//
//			String output;
//			//System.out.println("Output from Server .... \n");
//			if ((output = br.readLine()) != null) {
//                                System.out.println(output);
//                                Gson gson = new Gson();
//                                java.lang.reflect.Type tipoListaProducto = new TypeToken<List<Producto>>(){}.getType();
//                                productos = gson.fromJson(output, tipoListaProducto);
//                                //assertNotNull(clientes);
//                                //List<Cliente> prov = gson.fromJson(output, List<Cliente.class>);
//				//System.out.println(prov.getNombre());
//                                //return productos;
//			}
//                    }
//		} catch (ClientProtocolException e) {
//
//			e.printStackTrace();
//
//		} catch (IOException e) {
//
//			e.printStackTrace();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}
//                //return null;
//	}
//    
    
//    public void comprar() {
//                 System.out.println("Llamo a crear compra");
//		try {
//
//                       
//			ClientRequest request = new ClientRequest(
//					"http://localhost:8080/SistemaCompraVenta/rest/compra");
//			request.accept("application/json");
//                        
//                        Compra_Cab comp = new Compra_Cab();
//                        comp.setFecha(fechaDate);
//                        comp.setDetalleCompraList(detalles);
//                        comp.setProveedor(idProveedor);
//                       // comp.setTotal(total);
//			/*String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
//			request.body("application/json", input);*/
//			
//                        Gson p = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//                        request.body("application/json", p.toJson(comp));
//                                
//			ClientResponse<String> response = request.post(String.class);
//                        FacesMessage msg = new FacesMessage("Compra_Cab", response.getEntity());  
//                        FacesContext.getCurrentInstance().addMessage(null, msg);
//			
//
//		} catch (MalformedURLException e) {
//
//			e.printStackTrace();
//			
//		} catch (IOException e) {
//
//			e.printStackTrace();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}
//
//	}
    
}
