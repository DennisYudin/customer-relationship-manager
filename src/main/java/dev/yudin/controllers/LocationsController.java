package dev.yudin.controllers;

import dev.yudin.entities.Location;
import dev.yudin.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/location")
    public String show(@RequestParam("locationId") long id, Model model) {

        Location location = locationService.getById(id);

        model.addAttribute("location", location);

        return "locations/single-location-form";
    }

    @GetMapping("/list")
    public String showListLocations(Model model) {

        List<Location> locations = locationService.findAll(null);

        model.addAttribute("locations", locations);

        return "locations/list-locations";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {

        Location location = new Location();

        model.addAttribute("location", location);

        return "locations/location-save-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("locationId") long id, Model model) {

        Location location = locationService.getById(id);

        model.addAttribute("location", location);

        return "locations/location-update-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("location") Location location) {

        locationService.save(location);

        return "redirect:/locations/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("locationId") long id) {

        locationService.delete(id);

        return "redirect:/locations/list";
    }
}

