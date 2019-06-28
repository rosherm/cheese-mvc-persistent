package org.launchcode.controllers;


import org.launchcode.models.Category;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.Normalizer;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    public MenuDao menuDao;

    @Autowired
    public CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menuDao", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAdd(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){

        Menu menu = menuDao.findOne(id);

        model.addAttribute(menu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){

        Menu menu = menuDao.findOne(id);

        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", form);

        model.addAttribute("title", "'Add item to menu: '"+ form.getMenu().getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String processAddItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){


        if (errors.hasErrors()) {
            model.addAttribute("title", "'Add item to menu: '"+ form.getMenu().getName());
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(form.getMenuId());

        menu.addItem(cheeseDao.findOne(form.getCheeseId()));

        menuDao.save(menu);

        return "redirect:/menu/view/"+ menu.getId();

    }
}
