package com.brasa.restaurant.service;

import com.brasa.restaurant.dto.CadastroRestauranteDtos;
import com.brasa.restaurant.enums.Enums.Perfil;
import com.brasa.restaurant.exception.ApiExceptions;
import com.brasa.restaurant.model.Mesa;
import com.brasa.restaurant.model.Restaurante;
import com.brasa.restaurant.model.Usuario;
import com.brasa.restaurant.repository.Repositories.MesaRepository;
import com.brasa.restaurant.repository.Repositories.RestauranteRepository;
import com.brasa.restaurant.repository.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RestaurantRegistrationService {

    private static final int MESAS_INICIAIS = 15;
    private static final int MAX_FALHAS_POR_ORIGEM = 5;
    private static final int MAX_FALHAS_GLOBAL = 30;
    private static final Duration JANELA = Duration.ofMinutes(15);
    private static final String CHAVE_GLOBAL = "global";

    private final RestauranteRepository restaurantes;
    private final UsuarioRepository usuarios;
    private final MesaRepository mesas;
    private final PasswordEncoder encoder;
    private final String codigoMestre;

    private final Map<String, Deque<Instant>> falhasPorChave = new ConcurrentHashMap<>();

    public RestaurantRegistrationService(
            RestauranteRepository restaurantes,
            UsuarioRepository usuarios,
            MesaRepository mesas,
            PasswordEncoder encoder,
            @Value("${app.registration.master-code:}") String codigoMestre
    ) {
        this.restaurantes = restaurantes;
        this.usuarios = usuarios;
        this.mesas = mesas;
        this.encoder = encoder;
        this.codigoMestre = codigoMestre == null ? "" : codigoMestre.trim();
    }

    @Transactional
    public CadastroRestauranteDtos.Response cadastrar(
            CadastroRestauranteDtos.Request requisicao,
            String origem
    ) {
        if (codigoMestre.isEmpty()) {
            throw new ApiExceptions.Forbidden(
                    "O cadastro de novos restaurantes não está habilitado."
            );
        }

        if (excedeuLimite(origem, MAX_FALHAS_POR_ORIGEM)
                || excedeuLimite(CHAVE_GLOBAL, MAX_FALHAS_GLOBAL)) {
            throw new ApiExceptions.Business(
                    "Muitas tentativas inválidas. Aguarde alguns minutos e tente novamente."
            );
        }

        if (!codigoConfere(requisicao.codigo_mestre())) {
            registrarFalha(origem);
            registrarFalha(CHAVE_GLOBAL);

            throw new ApiExceptions.Forbidden("Código de autorização inválido.");
        }

        String email = requisicao.email().trim().toLowerCase(Locale.ROOT);

        if (usuarios.findFirstByEmailIgnoreCaseAndAtivoTrue(email).isPresent()) {
            throw new ApiExceptions.Business("Este e-mail já está cadastrado.");
        }

        String nomeRestaurante = requisicao.nome_restaurante().trim();

        try {
            Restaurante restaurante = new Restaurante();
            restaurante.setNome(nomeRestaurante);
            restaurante.setSlug(gerarSlugUnico(nomeRestaurante));
            restaurante.setAtivo(true);

            Restaurante salvo = restaurantes.saveAndFlush(restaurante);

            Usuario administrador = new Usuario();
            administrador.setRestauranteId(salvo.getId());
            administrador.setNome(requisicao.nome_responsavel().trim());
            administrador.setEmail(email);
            administrador.setSenhaHash(encoder.encode(requisicao.senha()));
            administrador.setPerfil(Perfil.ADMIN);
            administrador.setAtivo(true);
            usuarios.saveAndFlush(administrador);

            for (int numero = 1; numero <= MESAS_INICIAIS; numero++) {
                Mesa mesa = new Mesa();
                mesa.setRestauranteId(salvo.getId());
                mesa.setNumero(numero);
                mesas.save(mesa);
            }

            return new CadastroRestauranteDtos.Response(
                    salvo.getId(),
                    salvo.getNome(),
                    salvo.getSlug(),
                    email,
                    "Restaurante cadastrado com sucesso."
            );
        } catch (DataIntegrityViolationException erro) {
            throw new ApiExceptions.Business(
                    "Não foi possível concluir o cadastro. Tente novamente."
            );
        }
    }

    private boolean codigoConfere(String informado) {
        byte[] esperado = codigoMestre.getBytes(StandardCharsets.UTF_8);

        byte[] recebido = informado == null
                ? new byte[0]
                : informado.trim().getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(esperado, recebido);
    }

    private boolean excedeuLimite(String chave, int maximo) {
        Deque<Instant> falhas =
                falhasPorChave.computeIfAbsent(chave, k -> new ArrayDeque<>());

        synchronized (falhas) {
            removerFalhasAntigas(falhas);
            return falhas.size() >= maximo;
        }
    }

    private void registrarFalha(String chave) {
        Deque<Instant> falhas =
                falhasPorChave.computeIfAbsent(chave, k -> new ArrayDeque<>());

        synchronized (falhas) {
            removerFalhasAntigas(falhas);
            falhas.addLast(Instant.now());
        }
    }

    private void removerFalhasAntigas(Deque<Instant> falhas) {
        Instant limite = Instant.now().minus(JANELA);

        while (!falhas.isEmpty() && falhas.peekFirst().isBefore(limite)) {
            falhas.pollFirst();
        }
    }

    private String gerarSlugUnico(String nome) {
        String base = gerarSlugBase(nome);
        String slug = base;
        int sufixo = 2;

        while (restaurantes.existsBySlug(slug)) {
            slug = base + "-" + sufixo;
            sufixo++;
        }

        return slug;
    }

    private String gerarSlugBase(String nome) {
        String semAcentos = Normalizer
                .normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = semAcentos
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.length() > 40) {
            slug = slug.substring(0, 40).replaceAll("-+$", "");
        }

        return slug.isEmpty() ? "restaurante" : slug;
    }
}