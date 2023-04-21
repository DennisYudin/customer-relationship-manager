package dev.yudin.dao.impl;

import dev.yudin.dao.CustomerDAO;
import dev.yudin.entities.Customer;
import lombok.extern.log4j.Log4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j
@Repository("customerDAO")
public class CustomerDAOImpl implements CustomerDAO {

	private SessionFactory sessionFactory;

	@Autowired
	public CustomerDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Customer getById(int id) {
		var session = sessionFactory.getCurrentSession();
		return session.get(Customer.class, id);
	}

	@Override
	public List<Customer> findAll() {
		var session = sessionFactory.getCurrentSession();

		Query<Customer> query = session.createQuery(
				"FROM Customer ORDER BY lastName",
				Customer.class);

		return query.getResultList();
	}

	@Override
	public void save(Customer customer) {
		var session = sessionFactory.getCurrentSession();

		session.saveOrUpdate(customer);
	}

	@Override
	public void delete(int id) {
		var session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("DELETE FROM Customer WHERE id=:customerId");
		query.setParameter("customerId", id);

		query.executeUpdate();
	}
}
