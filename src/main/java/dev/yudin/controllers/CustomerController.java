package dev.yudin.controllers;


import dev.yudin.entities.Category;
import dev.yudin.entities.Customer;
import dev.yudin.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private CustomerService<Customer> customerService;

	@Autowired
	public CustomerController(CustomerService<Customer> customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/list")
	public String showListCustomers(Model model) {

		List<Customer> customers = customerService.findAll();

		model.addAttribute("customers", customers);

		return "customer/all-customers";
	}

	@GetMapping("/formForUpdate")
	public String showFormForUpdate(@RequestParam("customerId") long id, Model model) {

		Customer customer = customerService.getBy(id);

		model.addAttribute("customer", customer);

		return "categories/category-update-form";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("customerId") long id) {

		customerService.delete(id);

		return "redirect:/categories/list";
	}
}
