package com.gplx.gplx_management.controller;

import com.gplx.gplx_management.repository.HanggiayphepRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HanggiayphepRepository hanggiayphepRepository;

    public HomeController(HanggiayphepRepository hanggiayphepRepository) {
        this.hanggiayphepRepository = hanggiayphepRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("licenseRanks", hanggiayphepRepository.findAll(Sort.by(Sort.Direction.ASC, "maHang")));
        return "home/index";
    }
}
