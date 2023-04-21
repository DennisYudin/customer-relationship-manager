package dev.yudin.controllers;


import dev.yudin.entities.Customer;
import dev.yudin.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/list")
	public String showListCustomers(Model model) {

		List<Customer> customers = customerService.findAll();

		model.addAttribute("customers", customers);

		return "customer/all-customers";
	}

	@GetMapping("/formForUpdate")
	public String showFormForUpdate(@RequestParam("customerId") int id, Model model) {

		Customer customer = customerService.getById(id);

		model.addAttribute("customer", customer);

		return "customer/customer-update-form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("customer") Customer customer) {

		customerService.save(customer);

		return "redirect:/customer/list";
	}

	@GetMapping("/formForAddNewCustomer")
	public String showFormForAdd(Model model) {

		Customer customer = new Customer();

		model.addAttribute("customer", customer);

		return "customer/save-new-customer-form";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("customerId") int id) {

		customerService.delete(id);

		return "redirect:/customer/list";
	}
}
