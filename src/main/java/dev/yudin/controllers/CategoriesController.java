package dev.yudin.controllers;

import dev.yudin.entities.Category;
import dev.yudin.exceptions.ValueExistException;
import dev.yudin.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/ping")
    public String sayHello() {
        return "hello-page";
    }

    @GetMapping("/category")
    public String show(@RequestParam("categoryName") String name, Model model) {

        List<Category> categories = categoryService.getByName(name);

        model.addAttribute("categories", categories);

        return "categories/category-search-form";
    }

    @GetMapping("/list")
    public String showListCategories(Model model,
                                     @RequestParam(
                                             value = "page",
                                             required = false,
                                             defaultValue = "1") Integer currentPage,
                                     @RequestParam(
                                             value = "size",
                                             required = false,
                                             defaultValue = "5") Integer pageSize) {

        List<Category> allCategories = categoryService.findAll(null);

        int totalElements = allCategories.size();

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        List<Category> categories = categoryService.findAll(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("categories", categories);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream
                    .rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "categories/category-get-list-form";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {

        Category category = new Category();

        model.addAttribute("category", category);

        return "categories/category-save-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("categoryId") long id, Model model) {

        Category category = categoryService.getById(id);

        model.addAttribute("category", category);

        return "categories/category-update-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("category") Category category,
                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "categories/category-save-form";
        } else {
            try {
                categoryService.save(category);
            } catch (ValueExistException ex) {
                bindingResult.rejectValue("title", "error.category", "already exist in DB");

                return "categories/category-save-form";
            }
            return "redirect:/categories/list";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("categoryId") long id) {

        categoryService.delete(id);

        return "redirect:/categories/list";
    }

    @GetMapping("/details")
    public String showDetails(@RequestParam("categoryId") long id, Model model) {

        Category category = categoryService.getById(id);

        model.addAttribute("category", category);

        return "categories/category-single-form";
    }
}

