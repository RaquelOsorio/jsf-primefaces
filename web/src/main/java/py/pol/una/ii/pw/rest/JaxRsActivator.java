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
package py.pol.una.ii.pw.rest;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6 "no XML" approach to activating
 * JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 */
@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
    /* class body intentionally left blank */
	 @Override
	    public Set<Class<?>> getClasses() {
	        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
	        try {
	            Class jsonProvider = Class.forName("org.jboss.resteasy.resteasy-jackson-provider");
	           
	            resources.add(jsonProvider);
	        } catch (ClassNotFoundException ex) {
	            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
	        addRestResourceClasses(resources);
	        return resources;
	    }
	    
	    private void addRestResourceClasses(Set<Class<?>> resources) {
	        resources.add(Compra_CabResourceRESTService.class);
	        resources.add(ClienteResourceRESTService.class);
	        resources.add(ProveedorResourceRESTService.class);
	        resources.add(ProductoResourceRESTService.class);
	        resources.add(Venta_CabResourceRESTService.class);
	        resources.add(Venta_DetResourceRESTService.class);
	        resources.add(FacturaResourceRESTService.class);
	        resources.add(Compra_DetResourceRESTService.class);	 
	        resources.add(SolicitudesCompraBean.class);	 
	        
	        
	          
	    }
}
