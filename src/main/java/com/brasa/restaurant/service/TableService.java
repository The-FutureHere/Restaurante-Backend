package com.brasa.restaurant.service;

import com.brasa.restaurant.dto.MesaQuantidadeDtos;
import com.brasa.restaurant.model.Mesa;
import com.brasa.restaurant.repository.Repositories.MesaRepository;
import com.brasa.restaurant.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TableService {

    private final MesaRepository mesas;

    public TableService(MesaRepository mesas) {
        this.mesas = mesas;
    }

    @Transactional(readOnly = true)
    public MesaQuantidadeDtos.Response quantidade() {
        String restauranteId = SecurityUtils.current().restaurantId();

        return new MesaQuantidadeDtos.Response(
                mesas.findByRestauranteId(restauranteId).size()
        );
    }

    @Transactional
    public MesaQuantidadeDtos.Response garantir(int quantidade) {
        String restauranteId = SecurityUtils.current().restaurantId();

        Set<Integer> existentes = new HashSet<>();

        for (Mesa mesa : mesas.findByRestauranteId(restauranteId)) {
            existentes.add(mesa.getNumero());
        }

        for (int numero = 1; numero <= quantidade; numero++) {
            if (!existentes.contains(numero)) {
                Mesa nova = new Mesa();
                nova.setRestauranteId(restauranteId);
                nova.setNumero(numero);
                mesas.save(nova);

                existentes.add(numero);
            }
        }

        return new MesaQuantidadeDtos.Response(existentes.size());
    }
}