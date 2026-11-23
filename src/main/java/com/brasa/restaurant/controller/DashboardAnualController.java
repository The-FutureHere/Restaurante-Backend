package com.brasa.restaurant.controller;

import com.brasa.restaurant.dto.DashboardAnualResponse;
import com.brasa.restaurant.service.DashboardAnualService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardAnualController {

    private final DashboardAnualService service;

    public DashboardAnualController(DashboardAnualService service) {
        this.service = service;
    }

    @GetMapping("/anual")
    public DashboardAnualResponse anual(
            @RequestParam(name = "ano", required = false) Integer ano
    ) {
        return service.resumo(ano);
    }
}