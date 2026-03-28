package com.gplx.gplx_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileModuleController {

    @GetMapping("/profiles")
    public String profileModuleHome() {
        return "profiles/index";
    }
}
