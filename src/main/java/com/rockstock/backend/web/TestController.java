package com.rockstock.backend.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-email")
    public String testEmailTemplate(Model model) {
        model.addAttribute("verificationLink", "http://example.com/verify?token=123456");
        return "verification-email"; // Sesuai dengan nama file di templates
    }
}
