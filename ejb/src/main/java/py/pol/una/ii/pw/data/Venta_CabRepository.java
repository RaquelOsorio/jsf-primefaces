package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.Venta_Cab;


@ApplicationScoped

public class Venta_CabRepository {

	@PersistenceContext(unitName="PersistenceApp")
    private EntityManager em;

    public Venta_Cab findById(Long id) {
        return em.find(Venta_Cab.class, id);
    }

    public Venta_Cab findByVenta_Cab(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Venta_Cab> criteria = cb.createQuery(Venta_Cab.class);
        Root<Venta_Cab> venta_Cab = criteria.from(Venta_Cab.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(venta_Cab).where(cb.equal(venta_Cab.get("id"), id));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Venta_Cab> findAllOrderedByVenta_Cab() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Venta_Cab> criteria = cb.createQuery(Venta_Cab.class);
        Root<Venta_Cab> venta_Cab = criteria.from(Venta_Cab.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(venta_Cab).orderBy(cb.asc(venta_Cab.get("id")));
        return em.createQuery(criteria).getResultList();
    }
}
