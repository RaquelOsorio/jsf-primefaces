package py.pol.una.ii.pw.controller;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import py.pol.una.ii.pw.model.Venta_Cab;

@ManagedBean(name="ventaList")
@ViewScoped
public class VentaList implements Serializable {

    LazyDataModel<Venta_Cab> lazyModel ;
    Venta_Cab venta;

    
    public LazyDataModel<Venta_Cab> getLazyModel() {
		return lazyModel;
	}
	public void setLazyModel(LazyDataModel<Venta_Cab> lazyModel) {
		this.lazyModel = lazyModel;
	}
	public Venta_Cab getVenta() {
		return venta;
	}
	public void setVenta(Venta_Cab venta) {
		this.venta = venta;
	}
	@PostConstruct
    public void init() {
        lazyModel = new VentaLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount()); 
         
    }
     public void selectVentaFromDialog(String cliente) {
        RequestContext.getCurrentInstance().closeDialog(cliente);
    
     }
     
       public void onVentaChosen(SelectEvent event) {
        String venta = (String) event.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta", "Id:" + venta);
         
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
}
