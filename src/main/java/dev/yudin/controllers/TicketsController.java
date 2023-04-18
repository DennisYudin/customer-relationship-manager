package dev.yudin.controllers;

import dev.yudin.entities.Ticket;
import dev.yudin.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketsController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/list")
    public String showListTickets(Model model) {

        List<Ticket> tickets = ticketService.findAll(null);

        model.addAttribute("tickets", tickets);

        return "tickets/list-tickets";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {

        Ticket ticket = new Ticket();

        model.addAttribute("ticket", ticket);

        return "tickets/ticket-save-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("ticketId") long id, Model model) {

        Ticket ticket = ticketService.getById(id);

        model.addAttribute("ticket", ticket);

        return "tickets/ticket-update-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("ticket") Ticket ticket) {

        ticketService.save(ticket);

        return "redirect:/tickets/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("ticketId") long id) {

        ticketService.delete(id);

        return "redirect:/tickets/list";
    }
}
