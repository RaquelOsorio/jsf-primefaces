package py.pol.una.ii.pw.model;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "Factura")
@XmlRootElement

public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="my_seq")
    @SequenceGenerator(name="my_seq",sequenceName="factura_id_seq", allocationSize=1)
    @Expose
    private Long id;	
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Column(name = "monto_total")
   // @Expose
    private Integer monto_total;

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

	public Integer getMonto_total() {
		return monto_total;
	}

	public void setMonto_total(Integer monto_total) {
		this.monto_total = monto_total;
	}

	public Factura(Long id, Date fecha,Integer monto_total) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.monto_total = monto_total;
	}

	public Factura() {
		super();
	}

    
}
