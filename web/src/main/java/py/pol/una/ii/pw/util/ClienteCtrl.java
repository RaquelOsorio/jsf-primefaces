package py.pol.una.ii.pw.util;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.faces.event.ActionEvent;

import py.pol.una.ii.pw.data.ClienteRepository;
import py.pol.una.ii.pw.model.Clientes;
import py.pol.una.ii.pw.rest.ClienteResourceRESTService;
import py.pol.una.ii.pw.service.ClienteRegistration;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
 

@ManagedBean

public class ClienteCtrl {
	@Inject
    ClienteRegistration registration;
	
	private String nombre="";
	private String apellido="";
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	public void crear(ActionEvent actionEvent) throws Exception{
		Clientes cliente;
		cliente= new Clientes();
    	cliente.setNombre(nombre);
    	cliente.setApellido(apellido);
    	registration.register(cliente);
    	System.out.println("Se creo con EXITOOOOOOOOOOOOOOOOOOOOOOO");
    	
    	
	}
	
}
