package com.brasa.restaurant.service;

import com.brasa.restaurant.dto.Dtos.RestauranteResponse;
import com.brasa.restaurant.exception.ApiExceptions;
import com.brasa.restaurant.mapper.ApiMapper;
import com.brasa.restaurant.model.Restaurante;
import com.brasa.restaurant.repository.Repositories.RestauranteRepository;
import com.brasa.restaurant.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestauranteRepository restaurantes;

    public RestaurantService(RestauranteRepository restaurantes) {
        this.restaurantes = restaurantes;
    }

    @Transactional(readOnly = true)
    public RestauranteResponse atual() {
        String restauranteId = SecurityUtils.current().restaurantId();

        Restaurante restaurante = restaurantes.findById(restauranteId)
                .orElseThrow(() -> new ApiExceptions.NotFound("Restaurante não encontrado"));

        return ApiMapper.restaurant(restaurante);
    }
}