package com.brasa.restaurant.controller;

import com.brasa.restaurant.dto.Dtos.RestauranteResponse;
import com.brasa.restaurant.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurante")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping("/atual")
    public RestauranteResponse atual() {
        return service.atual();
    }
}