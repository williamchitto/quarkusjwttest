package br.com.psainfo.mimir.dao.jpa;

import br.com.psainfo.mimir.model.dto.analise.ConsignatariaDTO;
import br.com.psainfo.mimir.model.jpa.base.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioDao implements PanacheRepository<Usuario> {

  @Inject EntityManager entityManager;

  public Integer updateStatus(final Usuario usuario) {
    final StringBuilder sql = new StringBuilder();
    sql.append(" UPDATE mimir.usuario user");
    sql.append(" SET user.ativo = :ativo ");
    sql.append(" WHERE user.id = :id ");

    final Query query = this.entityManager.createNativeQuery(sql.toString(), Usuario.class);

    query.setParameter("ativo", usuario.getAtivo().getCod());
    query.setParameter("id", usuario.getId());

    return query.executeUpdate();
  }

  public Integer updateConsignataria(final Usuario usuario, final Long idConsignataria) {
    final StringBuilder sql = new StringBuilder();
    sql.append(" UPDATE mimir.usuario user");
    sql.append(" SET user.fk_consignataria = :fk_consignataria ");
    sql.append(" WHERE user.id = :id ");

    final Query query = this.entityManager.createNativeQuery(sql.toString(), Usuario.class);

    query.setParameter("fk_consignataria", idConsignataria);
    query.setParameter("id", usuario.getId());

    return query.executeUpdate();
  }

  public List<String> allUsersByLogin(final String login) {

    final List<Usuario> usuarios = this.streamAll().collect(Collectors.toList());

    final Optional<Usuario> usuarioLoginOptional = usuarios.stream().filter(usuario -> usuario.getLogin().equals(login)).findFirst();

    if(usuarioLoginOptional.isPresent()) {
      if(usuarioLoginOptional.get().getIsGestor()) {
        return usuarios.stream().map(Usuario::getLogin).distinct().collect(Collectors.toList());
      }
      else {
        return Arrays.asList(login);
      }
    }

    return new ArrayList<>();
  }

  public List<String> allUsers() {
    return this.streamAll().map(Usuario::getLogin).distinct().collect(Collectors.toList());
  }

  public Optional<Usuario> findByLogin(final String login) {
    return this.streamAll().filter(usuario -> usuario.getLogin().equals(login.trim())).findAny();
  }

  public Map<Long, Usuario> users() {
    return this.streamAll().collect(Collectors.toMap(Usuario::getId, Function.identity()));
  }
}
