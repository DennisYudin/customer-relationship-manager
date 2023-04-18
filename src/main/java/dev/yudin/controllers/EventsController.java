package dev.yudin.controllers;

import dev.yudin.dto.EventDTO;
import dev.yudin.entities.Event;
import dev.yudin.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventsController {

    @Autowired
    private EventService eventService;

    @GetMapping("/list")
    public String showListEvents(Model model) {

        List<Event> allEvents = eventService.findAll(null);

        model.addAttribute("events", allEvents);

        return "events/event-list-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("event") Event event) {

        eventService.save(event);

        return "redirect:/events/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(
            @RequestParam("eventId") long id,
            Model model) {

        Event event = eventService.getById(id);

        model.addAttribute("event", event);

        return "events/event-update-form";
    }

    @GetMapping("/details")
    public String showEventDetails(@RequestParam("eventId") long id, Model model) {

        EventDTO eventDTO = eventService.getEventWithDetails(id);

        model.addAttribute("eventDTO", eventDTO);

        return "events/event-details-form";
    }
}
