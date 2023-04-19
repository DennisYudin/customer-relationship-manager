package dev.yudin.services.impl;

import dev.yudin.dao.CustomerDAO;
import dev.yudin.entities.Customer;
import dev.yudin.services.CustomerService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.transaction.Transactional;

@Log4j
@Service
public class CustomerServiceImpl implements CustomerService {

	private CustomerDAO<Customer> customerDAO;

	@Autowired
	public CustomerServiceImpl(CustomerDAO<Customer> customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Override
	public Object getBy(long id) {
		return null;
	}

	@Transactional
	@Override
	public List<Customer> findAll() {
		return customerDAO.findAll();
	}

	@Override
	public void save(Object o) {

	}

	@Override
	public void delete(long id) {

	}
}
