package com.brasa.restaurant.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardAnualResponse(
        int ano,
        BigDecimal faturamento_total,
        BigDecimal ticket_medio,
        long total_pedidos,
        String melhor_mes,
        BigDecimal faturamento_melhor_mes,
        List<FaturamentoMesResponse> meses
) {

    public record FaturamentoMesResponse(
            int mes,
            String nome,
            BigDecimal faturamento,
            long pedidos
    ) {
    }
}