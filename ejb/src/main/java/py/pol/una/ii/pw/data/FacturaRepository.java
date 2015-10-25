package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.Factura;


@ApplicationScoped
public class FacturaRepository {

	@Inject
    private EntityManager em;

    public Factura findById(Long id) {
        return em.find(Factura.class, id);
    }

    public Factura findByFactura(Integer mt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(factura).where(cb.equal(factura.get("monto_total"), mt));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Factura> findAllOrderedByFactura() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> criteria = cb.createQuery(Factura.class);
        Root<Factura> factura = criteria.from(Factura.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(factura).orderBy(cb.asc(factura.get("monto_total")));
        return em.createQuery(criteria).getResultList();
    }
}
