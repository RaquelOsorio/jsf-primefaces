package py.pol.una.ii.pw.model;
//import com.google.gson.annotations.Expose;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
//import org.codehaus.jackson.annotate.JsonIgnore;
@Entity
@Table(name = "Venta_Det")
@XmlRootElement
public class Venta_Det implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name = "id")
    //@Expose
    private Long id;
    
    @JoinColumn(name = "producto", referencedColumnName = "id")
    @OneToOne
  //  @Expose
    private Producto producto;

  //  @Basic(optional = false)
  //  @NotNull
    @Column(name = "cantidad")
    //@Expose
    private int cantidad;

    @JoinColumn(name= "cabecera",referencedColumnName="id")
    @ManyToOne
    private Venta_Cab cabecera;
    
    
    
	public Venta_Cab getCabecera() {
		return cabecera;
	}

	public void setCabecera(Venta_Cab cabecera) {
		this.cabecera = cabecera;
	}

	public Venta_Det() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Venta_Det(Long id, Producto producto, int cantidad) {
		this.id = id;
		this.producto = producto;
		this.cantidad = cantidad;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
    
    
}
