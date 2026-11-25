package com.brasa.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public final class MesaQuantidadeDtos {

    private MesaQuantidadeDtos() {
    }

    public record Request(
            @Min(value = 1, message = "A quantidade mínima é 1.")
            @Max(value = 200, message = "A quantidade máxima é 200.")
            int quantidade
    ) {
    }

    public record Response(int quantidade) {
    }
}