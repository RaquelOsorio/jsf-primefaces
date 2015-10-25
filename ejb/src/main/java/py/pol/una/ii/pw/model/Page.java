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
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import py.pol.una.ii.pw.model.Producto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;



import java.util.*;

public class Page {
    int pageSize, page, lastPage, totalElements;
    Collection results;
    public Page() {}

    public Page(Collection results, int page, int pageSize, int totalElements) {
        this.results = results;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        lastPage = getLastPage(totalElements, pageSize);
    }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public Collection getResults() { return results; }

    public void setResults(Collection results) { this.results = results; }

    public int getLastPage() { return lastPage; }

    public void setLastPage(int lastPage) { this.lastPage = lastPage; }

    public int getPageSize() { return pageSize; }

    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalElements() { return totalElements; }

    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }

    /**
     * Devuelve el numero (posicion) del elementos inicial de una pagina dada.
     * A partir del elementos devuelto hay que pintar nPageSize elementos más.
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static int getStartItemByPage(int currentPage, int pageSize) {
        return Math.max((pageSize * (currentPage - 1)) + 1, 1);
        // El Math.max sirve para evitar los numeros negativos, siempre hay
        // algun malvado que puede pedirnos la pagina -10. En esos casos,
        // la pagina devuelta sera 1
    }

    public static int getLastPage(int totalElements, int pageSize) {
        int base = totalElements / pageSize;
        int mod = totalElements % pageSize;
        return base + (mod > 0?1:0);
    }
}
