package dev.yudin.dao.impl;

import dev.yudin.dao.CustomerDAO;
import dev.yudin.entities.Customer;
import lombok.extern.log4j.Log4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Log4j
@Repository("customerDAO")
public class CustomerDAOImpl implements CustomerDAO {


	private SessionFactory sessionFactory;

	@Autowired
	public CustomerDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Object getBy(long id) {
		return null;
	}


	@Override
	public List<Customer> findAll() {
		var session = sessionFactory.getCurrentSession();

		Query<Customer> query = session.createQuery("from Customer", Customer.class);

		return query.getResultList();
	}

	@Override
	public void save(Object o) {

	}

	@Override
	public void delete(long id) {

	}
}
