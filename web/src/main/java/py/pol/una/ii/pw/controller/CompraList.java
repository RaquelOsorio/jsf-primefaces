package py.pol.una.ii.pw.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.LazyDataModel;

import py.pol.una.ii.pw.model.Compra_Cab;

/**
 *
 * @author usuario
 */
@ManagedBean(name="compra")
@ViewScoped
public class CompraList implements Serializable {

    Compra_Cab compra;

    public Compra_Cab getCompra() {
        return compra;
    }

    public void setCompra(Compra_Cab compra) {
        this.compra = compra;
    }
    LazyDataModel<Compra_Cab> lazyModel ;
    
    public LazyDataModel<Compra_Cab> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Compra_Cab> lazyModel) {
        this.lazyModel = lazyModel;
    }
   
    @PostConstruct
    public void init() {

        lazyModel = new Compra_CabLazyList();
        lazyModel.setRowCount(lazyModel.getRowCount()); 
         
    }
    
}
