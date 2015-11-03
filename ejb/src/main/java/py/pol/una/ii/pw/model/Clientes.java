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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;



import com.google.gson.annotations.Expose;

@Entity
@XmlRootElement
@Table(name = "clientes")
@NamedQueries({
	   @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c"),
	   })
public class Clientes implements Serializable {
    
	/** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="my_seq")
    @SequenceGenerator(name="my_seq",sequenceName="cliente_id_seq", allocationSize=1)
    @Expose
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "No es una descripcion valida")
    private String nombre;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "No es una descripcion valida")
    private String apellido;
    
    
    public Clientes(Long id, String nombre, String apellido) {
//		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;

	}
	public Clientes() {
	//	super();
		// TODO Auto-generated constructor stub
	}

//    boolean editable;
//    
//	public boolean isEditable() {
//		return editable;
//	}
//	public void setEditable(boolean editable) {
//		this.editable = editable;
//	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
     @Override
    public boolean equals(Object obj){
        System.out.println("Llego al equals de cliente");
        if ( obj == null )
            return false;
        if ( !( obj instanceof Clientes) )
            return false;
        Clientes cli = (Clientes) obj;
        
        
        return new EqualsBuilder()
                        .append(this.nombre, cli.nombre)
                        .append(this.id, cli.id)
                        .append(this.apellido, cli.apellido)
                        .isEquals();
                      
                        
    }

    /*@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }*/

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }

}
