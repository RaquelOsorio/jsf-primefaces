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

import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

@ManagedBean(name= "producto")
public class RestEasyProducto {
	
	private String detalle;
    private int stock;
    private int precio;
    private Proveedor idProveedor;
    
    private Integer idM;
    private String detalleM;
    private int stockM;
    private int precioM;
    private Proveedor idProveedorM;

    public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public Proveedor getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Proveedor idProveedor) {
		this.idProveedor = idProveedor;
	}

	public Integer getIdM() {
		return idM;
	}

	public void setIdM(Integer idM) {
		this.idM = idM;
	}

	public String getDetalleM() {
		return detalleM;
	}

	public void setDetalleM(String detalleM) {
		this.detalleM = detalleM;
	}

	public int getStockM() {
		return stockM;
	}

	public void setStockM(int stockM) {
		this.stockM = stockM;
	}

	public int getPrecioM() {
		return precioM;
	}

	public void setPrecioM(int precioM) {
		this.precioM = precioM;
	}

	public Proveedor getIdProveedorM() {
		return idProveedorM;
	}

	public void setIdProveedorM(Proveedor idProveedorM) {
		this.idProveedorM = idProveedorM;
	}


    LazyDataModel<Producto> lazyModel ;
    
    public LazyDataModel<Producto> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Producto> lazyModel) {
        this.lazyModel = lazyModel;
    }

    
    public void seleccionarUnProducto(Integer Id) {
		try {
                        String idModificar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/productos/" + idModificar);
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
                                Producto prod = gson.fromJson(output, Producto.class);
				System.out.println(prod.getDetalle());
                                
                                detalleM = prod.getDetalle();
                                stockM = prod.getStock();
                                precioM = prod.getPrecio();
                                idProveedorM = prod.getProveedor();
                                
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
                
	}
    
        public List<Producto> getProductos() throws IOException {
		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/productos");
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
                                java.lang.reflect.Type tipoListaProductos = new TypeToken<List<Producto>>(){}.getType();
                                List<Producto> productos = gson.fromJson(output, tipoListaProductos);
                               
                                return productos;
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
        
        public void setProducto() {

		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/productos");
			request.accept("application/json");
                        
                        Producto pr = new Producto();
                        pr.setDetalle(detalle);
                        pr.setStock(stock);
                       
                        pr.setProveedor(idProveedor);
                        pr.setPrecio(precio);
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(pr));
                                
			ClientResponse<String> response = request.post(String.class);
                        FacesMessage msg = new FacesMessage("Producto", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        detalle = null;
                        stock = 0;
                        idProveedor = null;
                        precio = 0;

			

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
        public void deleteProducto(Integer Id) {

		try {
                        String idEliminar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/productos/" + idEliminar );
			
                        
			ClientResponse<String> response = request.delete(String.class);
                        FacesMessage msg = new FacesMessage("Producto", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);			

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
        
        public void modificarProducto() {

               try {

                        
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/EjbJaxRS-web/rest/productos");
			request.accept("application/json");
                        
                        /*Producto pr = new Producto();*/
                        Producto productoModificado = new Producto();
                        productoModificado.setDetalle(detalleM);
                        productoModificado.setStock(stockM);
                        
                        productoModificado.setPrecio(precioM);
                        productoModificado.setProveedor(idProveedorM);
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(productoModificado));
                                
			ClientResponse<String> response = request.put(String.class);
                        FacesMessage msg = new FacesMessage("Producto", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        idM = null;
                        detalleM = null;
                        stockM = 0;
                        
                        precioM = 0;
                        idProveedor = null;
                        idProveedor.setDescripcion(null);


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
        lazyModel = new ProductoLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount());   
    }

}
