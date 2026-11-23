package com.brasa.restaurant.service;

import com.brasa.restaurant.dto.DashboardAnualResponse;
import com.brasa.restaurant.dto.DashboardAnualResponse.FaturamentoMesResponse;
import com.brasa.restaurant.enums.Enums.StatusPedido;
import com.brasa.restaurant.exception.ApiExceptions;
import com.brasa.restaurant.model.Pedido;
import com.brasa.restaurant.repository.Repositories.PedidoRepository;
import com.brasa.restaurant.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardAnualService {

    private static final ZoneId ZONA = ZoneId.of("America/Recife");

    private static final String[] NOMES_MESES = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    private final PedidoRepository pedidoRepository;

    public DashboardAnualService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardAnualResponse resumo(Integer anoSolicitado) {
        int anoAtual = Year.now(ZONA).getValue();
        int ano = anoSolicitado == null ? anoAtual : anoSolicitado;

        if (ano < 2000 || ano > anoAtual + 1) {
            throw new ApiExceptions.Business("Ano inválido: " + ano);
        }

        String restauranteId = SecurityUtils.current().restaurantId();

        Instant inicio = LocalDate.of(ano, 1, 1)
                .atStartOfDay(ZONA)
                .toInstant();

        Instant fim = LocalDate.of(ano + 1, 1, 1)
                .atStartOfDay(ZONA)
                .toInstant()
                .minusNanos(1);

        List<Pedido> pedidosDoAno =
                pedidoRepository.periodo(restauranteId, inicio, fim);

        BigDecimal[] faturamentoPorMes = new BigDecimal[12];
        long[] pedidosPorMes = new long[12];
        Arrays.fill(faturamentoPorMes, BigDecimal.ZERO);

        for (Pedido pedido : pedidosDoAno) {
            if (pedido.getStatus() == StatusPedido.CANCELADO) {
                continue;
            }

            int indice = pedido.getCriadoEm()
                    .atZone(ZONA)
                    .getMonthValue() - 1;

            BigDecimal total = pedido.getTotal() == null
                    ? BigDecimal.ZERO
                    : pedido.getTotal();

            faturamentoPorMes[indice] = faturamentoPorMes[indice].add(total);
            pedidosPorMes[indice]++;
        }

        List<FaturamentoMesResponse> meses = new ArrayList<>();
        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        long totalPedidos = 0;

        int indiceMelhorMes = -1;
        BigDecimal maiorFaturamento = BigDecimal.ZERO;

        for (int i = 0; i < 12; i++) {
            BigDecimal faturamento =
                    faturamentoPorMes[i].setScale(2, RoundingMode.HALF_UP);

            meses.add(new FaturamentoMesResponse(
                    i + 1,
                    NOMES_MESES[i],
                    faturamento,
                    pedidosPorMes[i]
            ));

            faturamentoTotal = faturamentoTotal.add(faturamento);
            totalPedidos += pedidosPorMes[i];

            if (faturamento.compareTo(maiorFaturamento) > 0) {
                maiorFaturamento = faturamento;
                indiceMelhorMes = i;
            }
        }

        BigDecimal ticketMedio = totalPedidos == 0
                ? BigDecimal.ZERO.setScale(2)
                : faturamentoTotal.divide(
                        BigDecimal.valueOf(totalPedidos),
                        2,
                        RoundingMode.HALF_UP
                );

        String melhorMes = indiceMelhorMes >= 0
                ? NOMES_MESES[indiceMelhorMes]
                : null;

        return new DashboardAnualResponse(
                ano,
                faturamentoTotal,
                ticketMedio,
                totalPedidos,
                melhorMes,
                maiorFaturamento,
                meses
        );
    }
}