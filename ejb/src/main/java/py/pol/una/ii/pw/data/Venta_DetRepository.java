package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.Compra_Det;

import py.pol.una.ii.pw.model.Venta_Det;


@ApplicationScoped
public class Venta_DetRepository {

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    public Venta_Det findById(Long id) {
        return em.find(Venta_Det.class, id);
    }
    

    //Listar por cabecera
    public List<Venta_Det> findAllByCabecera(Long cab) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Venta_Det> criteria = cb.createQuery(Venta_Det.class);
        Root<Venta_Det> detalle = criteria.from(Venta_Det.class);
        criteria.select(detalle).where(cb.equal(detalle.get("cabecera"), cab));
        return em.createQuery(criteria).getResultList();
         //  criteria.select(detalle).orderBy(cb.asc(detalle.get("producto")));
        //return em.createQuery(criteria).getResultList();
    }
    
    
    
    public Venta_Det findByVenta_Det(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Venta_Det> criteria = cb.createQuery(Venta_Det.class);
        Root<Venta_Det> venta_Det = criteria.from(Venta_Det.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(venta_Det).where(cb.equal(venta_Det.get("id"), id));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Venta_Det> findAllOrderedByVenta_Det() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Venta_Det> criteria = cb.createQuery(Venta_Det.class);
        Root<Venta_Det> venta_Det = criteria.from(Venta_Det.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(venta_Det).orderBy(cb.asc(venta_Det.get("id")));
        return em.createQuery(criteria).getResultList();
    }
    

    
    
    

}
