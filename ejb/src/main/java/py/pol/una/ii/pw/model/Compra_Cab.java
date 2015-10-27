/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@XmlRootElement
@Table(name = "Compra_Cab")
public class Compra_Cab implements Serializable {
    

	/** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="my_seq")
    @SequenceGenerator(name="my_seq",sequenceName="compra_cab_id_seq", allocationSize=1)
    @Expose
    private Long id;

   // @NotNull
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    //@NotNull
    @JoinColumn(name="proveedor",referencedColumnName="id")
    @OneToOne
    private Proveedor proveedor;
    
    @JoinColumn(name="detalleCompraList",referencedColumnName="id")
    @OneToMany
    private List<Compra_Det> detalleCompraList;


	public List<Compra_Det> getDetalleCompraList() {
		return detalleCompraList;
	}

	public void setDetalleCompraList(List<Compra_Det> detalleCompraList) {
		this.detalleCompraList = detalleCompraList;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.util.Date getFecha() {
		return fecha;
	}

	public void setFecha(java.util.Date fecha) {
		this.fecha = fecha;
	}
	public Compra_Cab(Long id, Date fecha, Proveedor proveedor,List<Compra_Det> detalleCompraList) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.proveedor = proveedor;
		this.detalleCompraList = detalleCompraList;
	}

	public Compra_Cab() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/*public List<Compra_Det> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<Compra_Det> detalle) {
		this.detalle = detalle;
	}
    */

}
