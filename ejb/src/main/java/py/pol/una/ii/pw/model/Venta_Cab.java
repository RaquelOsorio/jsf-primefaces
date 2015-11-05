package py.pol.una.ii.pw.model;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gson.annotations.Expose;

@Entity
@XmlRootElement
@Table(name = "Venta_Cab")
public class Venta_Cab implements Serializable{
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="my_seq")
    @SequenceGenerator(name="my_seq",sequenceName="venta_cab_id_seq", allocationSize=1)
    @Expose
    private Long id;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @JoinColumn(name = "cliente", referencedColumnName = "id")
    @OneToOne
    private Clientes cliente;
    
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @OneToOne
    private Factura factura;

    @JoinColumn(name="detalleVenta",referencedColumnName="id")
    @OneToMany
    private List<Venta_Det> detalleVenta;
    
    
	public List<Venta_Det> getDetalleVenta() {
		return detalleVenta;
	}

	public void setDetalleVenta(List<Venta_Det> detalleVenta) {
		this.detalleVenta = detalleVenta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Clientes getCliente() {
		return cliente;
	}

	public void setCliente(Clientes cliente) {
		this.cliente = cliente;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

/*	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}*/

//	public Venta_Cab() {
	//	super();
		// TODO Auto-generated constructor stub
//	}


}
