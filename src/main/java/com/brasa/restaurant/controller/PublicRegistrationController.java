package com.brasa.restaurant.controller;

import com.brasa.restaurant.dto.CadastroRestauranteDtos;
import com.brasa.restaurant.service.RestaurantRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/restaurantes")
public class PublicRegistrationController {

    private final RestaurantRegistrationService service;

    public PublicRegistrationController(RestaurantRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroRestauranteDtos.Response cadastrar(
            @Valid @RequestBody CadastroRestauranteDtos.Request requisicao,
            HttpServletRequest http
    ) {
        return service.cadastrar(requisicao, origem(http));
    }

    private String origem(HttpServletRequest http) {
        String encaminhado = http.getHeader("X-Forwarded-For");

        if (encaminhado != null && !encaminhado.isBlank()) {
            return encaminhado.split(",")[0].trim();
        }

        return http.getRemoteAddr();
    }
}