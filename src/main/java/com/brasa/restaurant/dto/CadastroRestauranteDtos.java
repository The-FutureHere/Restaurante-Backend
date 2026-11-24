package com.brasa.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CadastroRestauranteDtos {

    private CadastroRestauranteDtos() {
    }

    public record Request(
            @NotBlank @Size(min = 2, max = 120) String nome_restaurante,
            @NotBlank @Size(min = 2, max = 120) String nome_responsavel,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(min = 8, max = 72) String senha,
            @NotBlank @Size(max = 200) String codigo_mestre
    ) {
    }

    public record Response(
            String restaurante_id,
            String nome,
            String slug,
            String email,
            String mensagem
    ) {
    }
}