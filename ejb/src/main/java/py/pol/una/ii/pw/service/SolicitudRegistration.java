package py.pol.una.ii.pw.service;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import py.pol.una.ii.pw.model.SolicitudCompra;


public class SolicitudRegistration {
	
	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;


	   public List<SolicitudCompra> filtrar(FiltersObject filtros) {
	        String queryString;

	        // Hacemos el select de la entidad cliente
	        queryString = "SELECT o FROM SolicitudCompra o ";

	        // Si el filtro es distinto de null ampliamos la consulta, con el filtro
	        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty()) {
	            queryString = queryString + "WHERE";
	            for (Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext();) { //iteramos con el filtro
	                try {
	                    String atributo = it.next();
	                    String valor = filtros.getFilters().get(atributo).toString();
	                    if (atributo.equals("id")) {
	                        int cantidad = Integer.parseInt(valor);
	                        queryString = queryString + " " + atributo + " = " + cantidad; //si es un entero traemos lo menos o igual a ese numero
	                    } else {
	                        valor = "'%" + valor + "%'";
	                        queryString = queryString + " " + atributo + " LIKE " + valor; //si el valor a filtrar es una cadena , traemos la misma cadena o la que le contiene
	                    }

	                } catch (Exception e) {

	                }
	            }
	        }
	        //Para el ordenamiento, se agrega el order by a la consulta con el campo que se ordena y la direccion de ordenamiento
	        queryString = queryString + "ORDER BY o." + filtros.getSortField() + " " + filtros.getSortOrder();

	        return em.createQuery(queryString, SolicitudCompra.class)
	                .setFirstResult(filtros.getFirst())
	                .setMaxResults(filtros.getPageSize())
	                .getResultList();
	    }

	    public int filtrarCantidadRegistros(FiltersObject filtros) {
	        String queryString;

	        // Establece el nombre de la entidad que se esta buscando
	        queryString = "SELECT COUNT( o ) FROM SolicitudCompra o ";

	        // Si existen filtros los agrega a la consulta
	        if (filtros.getFilters() != null && !filtros.getFilters().isEmpty()) {
	            queryString = queryString + "WHERE";
	            for (Iterator<String> it = filtros.getFilters().keySet().iterator(); it.hasNext();) {
	                try {
	                    String atributo = it.next();
	                    String valor = filtros.getFilters().get(atributo).toString();

	                    if (atributo.equals("id")) {
	                        int cantidad = Integer.parseInt(valor);
	                        queryString = queryString + " " + atributo + " = " + cantidad;
	                    } else {
	                        valor = "'%" + valor + "%'";
	                        queryString = queryString + " " + atributo + " LIKE " + valor;
	                    }

	                } catch (Exception e) {

	                }
	            }
	        }
	        Long result = em.createQuery(queryString, Long.class).getSingleResult();
	        return result.intValue();
	    }
	    
}
