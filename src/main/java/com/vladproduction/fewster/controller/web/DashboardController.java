package com.vladproduction.fewster.controller.web;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UrlService urlService;

    public DashboardController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping
    public String dashboard(Model model) {
        try {
            List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
            model.addAttribute("urls", userUrls);
            model.addAttribute("urlDTO", new UrlDTO());
            return "dashboard/index";
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "Error loading your URLs");
            return "dashboard/index";
        }
    }

    @PostMapping("/create")
    public String createUrl(@Valid @ModelAttribute("urlDTO") UrlDTO urlDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (bindingResult.hasErrors()) {
            List<UrlDTO> userUrls = urlService.getAllUrlsForCurrentUser();
            model.addAttribute("urls", userUrls);
            return "dashboard/index";
        }

        try {
            UrlDTO createdUrl = urlService.create(urlDTO.getOriginalUrl(), false);
            redirectAttributes.addFlashAttribute("success",
                    "Short URL created successfully: " + createdUrl.getShortUrl());
            return "redirect:/dashboard";
        } catch (Exception e) {
            log.error("Error creating URL for: {}", urlDTO.getOriginalUrl(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to create short URL");
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/update/{id}")
    public String updateUrl(@PathVariable Long id,
                            @RequestParam String newOriginalUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            urlService.updateUrl(id, newOriginalUrl);
            redirectAttributes.addFlashAttribute("success", "URL updated successfully");
        } catch (Exception e) {
            log.error("Error updating URL with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to update URL");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteUrl(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            urlService.deleteUrl(id);
            redirectAttributes.addFlashAttribute("success", "URL deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting URL with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete URL");
        }
        return "redirect:/dashboard";
    }

}
