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

	private CustomerDAO customerDAO;

	@Autowired
	public CustomerServiceImpl(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Override
	@Transactional
	public Customer getById(int id) {
		return customerDAO.getById(id);
	}

	@Override
	@Transactional
	public List<Customer> findAll() {
		return customerDAO.findAll();
	}

	@Override
	@Transactional
	public void save(Customer customer) {
		customerDAO.save(customer);
	}

	@Override
	@Transactional
	public void delete(int id) {
		customerDAO.delete(id);
	}
}
