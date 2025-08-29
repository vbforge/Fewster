package com.vladproduction.fewster.controller.web;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping
public class HomeController {

    private final UrlService urlService;

    public HomeController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("urlDTO", new UrlDTO());
        return "index";
    }

    @PostMapping("/demo-create")
    public String createDemoUrl(@Valid @ModelAttribute("urlDTO") UrlDTO urlDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "index";
        }

        try {
            UrlDTO createdUrl = urlService.create(urlDTO.getOriginalUrl(), true);
            redirectAttributes.addFlashAttribute("success",
                    "Demo short URL created: " + createdUrl.getShortUrl());
            redirectAttributes.addFlashAttribute("demoUrl", createdUrl);
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error creating demo URL for: {}", urlDTO.getOriginalUrl(), e);
            model.addAttribute("error", "Failed to create short URL");
            return "index";
        }
    }

    @GetMapping("/about")
    public String about() { return "footer_links/about";
    }

    @GetMapping("/contact")
    public String contact() { return "footer_links/contact";
    }


}
