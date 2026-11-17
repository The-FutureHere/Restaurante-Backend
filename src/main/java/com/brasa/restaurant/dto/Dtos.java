package com.brasa.restaurant.dto;

import com.brasa.restaurant.enums.Enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class Dtos {

    private Dtos() {}

    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String senha
    ) {}

    public record UsuarioResponse(
        String id,
        String nome,
        String email,
        Perfil perfil
    ) {}

    public record AuthResponse(
        String token,
        UsuarioResponse usuario
    ) {}

    public record RestauranteResponse(
        String id,
        String nome,
        String slug,
        String logo_url,
        String cor_primaria,
        String cor_destaque,
        boolean ativo
    ) {}

    public record CategoriaDto(
        String id,
        @NotBlank String nome,
        int ordem
    ) {}

    public record ProdutoDto(
        String id,
        @NotBlank String categoria_id,
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin("0.01") BigDecimal preco,
        String imagem_url,
        boolean disponivel
    ) {}

    public record ItemPedidoRequest(
        @NotBlank String produto_id,
        @Min(1) int quantidade,
        String observacao
    ) {}

    public record CriarPedidoRequest(
        @Min(1) int numero_mesa,
        @NotBlank String nome_cliente,
        @NotEmpty List<@Valid ItemPedidoRequest> itens
    ) {}

    public record ItemPedidoResponse(
        String id,
        String produto_id,
        String nome_produto,
        int quantidade,
        BigDecimal preco_unitario,
        String observacao
    ) {}

    public record PedidoResponse(
        String id,
        int numero_mesa,
        String nome_cliente,
        StatusPedido status,
        BigDecimal total,
        Instant criado_em,
        List<ItemPedidoResponse> itens
    ) {}

    public record StatusPedidoRequest(
        @NotNull StatusPedido status
    ) {}

    public record FuncionarioDto(
        String id,
        @NotBlank String nome,
        @NotNull Cargo cargo,
        @NotNull Turno turno,
        boolean ativo
    ) {}

    public record DiasDto(
        DiaEscala seg,
        DiaEscala ter,
        DiaEscala qua,
        DiaEscala qui,
        DiaEscala sex,
        DiaEscala sab,
        DiaEscala dom
    ) {}

    public record EscalaDto(
        String funcionario_id,
        LocalDate semana_referencia,
        @NotNull DiasDto dias
    ) {}

    public record ChamadoRequest(
        @Min(1) int numero_mesa,
        @NotNull TipoChamado tipo
    ) {}

    public record ChamadoResponse(
        String id,
        int numero_mesa,
        TipoChamado tipo,
        StatusChamado status,
        Instant criado_em
    ) {}

    public record MesaStatusResponse(
        int numero_mesa,
        StatusMesa status
    ) {}

    public record HoraDto(
        String hora,
        long pedidos,
        BigDecimal faturamento
    ) {}

    public record DiaDto(
        String dia,
        BigDecimal valor
    ) {}

    public record RankingDto(
        String produto_id,
        String nome,
        String imagem_url,
        String categoria,
        long quantidade,
        BigDecimal receita
    ) {}

    public record DashboardResponse(
        BigDecimal faturado_dia,
        double variacao_dia,
        BigDecimal faturado_semana,
        BigDecimal ticket_medio,
        long pedidos_hoje,
        long pedidos_ativos,
        List<HoraDto> horas,
        List<DiaDto> semana,
        List<RankingDto> ranking
    ) {}
}