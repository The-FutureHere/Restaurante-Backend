package com.brasa.restaurant.controller;

import com.brasa.restaurant.dto.MesaQuantidadeDtos;
import com.brasa.restaurant.service.TableService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mesas")
public class TableController {

    private final TableService service;

    public TableController(TableService service) {
        this.service = service;
    }

    @GetMapping("/quantidade")
    public MesaQuantidadeDtos.Response quantidade() {
        return service.quantidade();
    }

    @PutMapping("/quantidade")
    public MesaQuantidadeDtos.Response definir(
            @Valid @RequestBody MesaQuantidadeDtos.Request requisicao
    ) {
        return service.garantir(requisicao.quantidade());
    }
}