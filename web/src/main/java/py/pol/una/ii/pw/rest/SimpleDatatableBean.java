package py.pol.una.ii.pw.rest;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import py.pol.una.ii.pw.model.Clientes;

@ManagedBean(name= "prueba")
@ViewScoped
public class SimpleDatatableBean implements Serializable{

	private List<Clientes> clienteList;
	private List<Clientes> clientesFilteringList;
	
	ClienteResourceRESTService clienteRes;
	
	public List<Clientes> getClienteList() {
		return clienteList;
	}
	public void setClienteList(List<Clientes> clienteList) {
		this.clienteList = clienteList;
	}
	public List<Clientes> getClientesFilteringList() {
		return clientesFilteringList;
	}
	public void setClientesFilteringList(List<Clientes> clientesFilteringList) {
		this.clientesFilteringList = clientesFilteringList;
	}
	
//	@PostConstruct
    public void init() {
    	clienteList = clienteRes.listAllProveedores();
    	System.out.println("llegaaAAAAAAAAA?????????????");
    }
	
	
}
