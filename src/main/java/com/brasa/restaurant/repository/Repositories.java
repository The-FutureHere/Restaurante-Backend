package com.brasa.restaurant.repository;

import com.brasa.restaurant.enums.Enums.StatusChamado;
import com.brasa.restaurant.enums.Enums.StatusPedido;
import com.brasa.restaurant.model.Categoria;
import com.brasa.restaurant.model.Chamado;
import com.brasa.restaurant.model.EscalaSemanal;
import com.brasa.restaurant.model.Funcionario;
import com.brasa.restaurant.model.Mesa;
import com.brasa.restaurant.model.Pedido;
import com.brasa.restaurant.model.Produto;
import com.brasa.restaurant.model.Restaurante;
import com.brasa.restaurant.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class Repositories {

    private Repositories() {
    }

    public interface RestauranteRepository extends JpaRepository<Restaurante, String> {

        Optional<Restaurante> findBySlugAndAtivoTrue(String slug);

        boolean existsBySlug(String slug);
    }

    public interface UsuarioRepository extends JpaRepository<Usuario, String> {

        Optional<Usuario> findFirstByEmailIgnoreCaseAndAtivoTrue(String email);

        Optional<Usuario> findByRestauranteIdAndEmailIgnoreCase(String r, String email);
    }

    public interface CategoriaRepository extends JpaRepository<Categoria, String> {

        List<Categoria> findByRestauranteIdOrderByOrdemAsc(String r);
    }

    public interface ProdutoRepository extends JpaRepository<Produto, String> {

        List<Produto> findByRestauranteIdOrderByNomeAsc(String r);

        List<Produto> findByRestauranteIdAndDisponivelTrueOrderByNomeAsc(String r);
    }

    public interface PedidoRepository extends JpaRepository<Pedido, String> {

        List<Pedido> findByRestauranteIdOrderByCriadoEmDesc(String r);

        List<Pedido> findByRestauranteIdAndNumeroMesaAndStatusNotOrderByCriadoEmDesc(
                String r, int mesa, StatusPedido status);

        @Query("select p from Pedido p where p.restauranteId=:r and p.criadoEm between :ini and :fim")
        List<Pedido> periodo(
                @Param("r") String r,
                @Param("ini") Instant ini,
                @Param("fim") Instant fim);
    }

    public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {

        List<Funcionario> findByRestauranteIdOrderByNomeAsc(String r);
    }

    public interface EscalaRepository extends JpaRepository<EscalaSemanal, String> {

        List<EscalaSemanal> findByRestauranteIdAndSemanaReferencia(String r, LocalDate s);

        Optional<EscalaSemanal> findByRestauranteIdAndFuncionarioIdAndSemanaReferencia(
                String r, String f, LocalDate s);
    }

    public interface MesaRepository extends JpaRepository<Mesa, String> {

        Optional<Mesa> findByRestauranteIdAndNumero(String r, int n);
    }

    public interface ChamadoRepository extends JpaRepository<Chamado, String> {

        List<Chamado> findByRestauranteIdAndStatusOrderByCriadoEmAsc(String r, StatusChamado s);

        List<Chamado> findByRestauranteIdAndNumeroMesaAndStatus(String r, int n, StatusChamado s);
    }
}