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
import javax.faces.context.FacesContext;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.primefaces.model.LazyDataModel;

import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;

public class RestEasyProducto {

    private String nombre;
    private int cantidad;
    private int precioUnitario;
    private String descripcion;
    private Proveedor idProveedor;
    
    private Integer idM;
    private String nombreM;
    private int cantidadM;
    private int precioUnitarioM;
    private String descripcionM;
    private Proveedor idProveedorM;

    LazyDataModel<Producto> lazyModel ;
    
    public LazyDataModel<Producto> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Producto> lazyModel) {
        this.lazyModel = lazyModel;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(int precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getNombreM() {
        return nombreM;
    }

    public void setNombreM(String nombreM) {
        this.nombreM = nombreM;
    }

    public Integer getCantidadM() {
        return cantidadM;
    }

    public void setCantidadM(Integer cantidadM) {
        this.cantidadM = cantidadM;
    }

    public int getPrecioUnitarioM() {
        return precioUnitarioM;
    }

    public void setPrecioUnitarioM(int precioUnitarioM) {
        this.precioUnitarioM = precioUnitarioM;
    }

    public String getDescripcionM() {
        return descripcionM;
    }

    public void setDescripcionM(String descripcionM) {
        this.descripcionM = descripcionM;
    }

    public Proveedor getIdProveedorM() {
        return idProveedorM;
    }

    public void setIdProveedorM(Proveedor idProveedorM) {
        this.idProveedorM = idProveedorM;
    }
    
    
    
    public void seleccionarUnProducto(Integer Id) {
		try {
                        String idModificar = Integer.toString(Id);
			ClientRequest request = new ClientRequest(
					"http://localhost:8080/SistemaCompraVenta/rest/productos/" + idModificar);
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
                                
                                nombreM = prod.getDetalle();
                                cantidadM = prod.getStock();
                                precioUnitarioM = prod.getPrecio();
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
                                //assertNotNull(productos);
                                //List<Producto> prod = gson.fromJson(output, List<Producto.class>);
				//System.out.println(prod.getNombre());
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
                        pr.setDetalle(descripcion);
                        pr.setStock(cantidad);
                       
                        pr.setProveedor(idProveedor);
                        pr.setPrecio(precioUnitario);
			/*String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
			request.body("application/json", input);*/
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(pr));
                                
			ClientResponse<String> response = request.post(String.class);
                        FacesMessage msg = new FacesMessage("Producto", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        nombre = null;
                        cantidad = 0;
                        idProveedor = null;
                        precioUnitario = 0;

			

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
                        productoModificado.setDetalle(nombreM);
                        productoModificado.setStock(cantidadM);
                        
                        productoModificado.setPrecio(precioUnitarioM);
                        productoModificado.setProveedor(idProveedorM);
			
                        Gson p= new Gson();
                        request.body("application/json", p.toJson(productoModificado));
                                
			ClientResponse<String> response = request.put(String.class);
                        FacesMessage msg = new FacesMessage("Producto", response.getEntity());  
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        idM = null;
                        nombreM = null;
                        cantidadM = 0;
                        
                        precioUnitarioM = 0;
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
        lazyModel = new LazyProductoModel();
        lazyModel.setRowCount(lazyModel.getRowCount());   
    }

}
