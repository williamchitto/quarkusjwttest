package br.com.psainfo.mimir.service.acesso;

import br.com.psainfo.mimir.core.seguranca.HashGeneratorUtil;
import br.com.psainfo.mimir.core.service.BaseService;
import br.com.psainfo.mimir.dao.jpa.ConsignatariaDao;
import br.com.psainfo.mimir.dao.jpa.UsuarioDao;
import br.com.psainfo.mimir.dominio.DominioConvenio;
import br.com.psainfo.mimir.dominio.DominioPlanoUsuario;
import br.com.psainfo.mimir.dominio.DominioSituacao;
import br.com.psainfo.mimir.model.dto.analise.ConsignatariaArtemisDTO;
import br.com.psainfo.mimir.model.dto.login.LoginDTO;
import br.com.psainfo.mimir.model.jpa.base.Consignataria;
import br.com.psainfo.mimir.model.jpa.base.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.vertx.ConsumeEvent;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Log
public class UsuarioService extends BaseService<Usuario> {

  @Inject UsuarioDao dao;

  @Inject PlanoService planoService;

  @Inject ConsignatariaDao consignatariaDao;

  @Override
  public PanacheRepository<Usuario> getDao() {

    return this.dao;
  }

  public Usuario changeStatus(final Long id, final Boolean status) {

    final Usuario usuario = super.findById(id);

    if (usuario == null) {
      return null;
    }

    usuario.setAtivo(DominioSituacao.valueOf(status));

    this.dao.updateStatus(usuario);

    return usuario;
  }

  public Usuario findByLogin(final String login) {

    return this.dao.find("login", login.trim().toLowerCase()).singleResult();
  }

  public Usuario findByLoginAtivo(final String login) {

    return this.dao.find("login = ?1 and ativo = 1", login.trim().toLowerCase()).singleResult();
  }

  public Usuario findByLoginConvenioAtivo(final LoginDTO loginDto) {
    final List<Usuario> usuarios = this.dao.findAll().list();
    final DominioConvenio convenioLogin = DominioConvenio.valueOf(loginDto.getIdParceiro().intValue());

    return usuarios.stream()
            .filter(
                    user ->
                            user.getAtivo().equals(DominioSituacao.ATIVO)
                                    && user.getLogin().equals(loginDto.getLogin().trim()) &&
                                      user.getConvenio().equals(convenioLogin))
            .findFirst().orElse(null);

  }

  public Usuario findByLoginConveniosSenhaAtivo(final LoginDTO loginDto) {

    try {
      final List<Usuario> usuarios = this.dao.findAll().list();
      final DominioConvenio convenioLogin = DominioConvenio.valueOf(loginDto.getIdParceiro().intValue());
      final String senhaCriptografada = HashGeneratorUtil.gerarHashParaLogin(loginDto.getLogin(), loginDto.getSenha());

      return usuarios.stream()
              .filter(
                      user ->
                              user.getAtivo().equals(DominioSituacao.ATIVO)
                                      && user.getLogin().equals(loginDto.getLogin().trim()) &&
                                      user.getConvenio().equals(convenioLogin) &&
                                      user.getSenha() != null &&  user.getSenha().equals(senhaCriptografada))
              .findFirst().orElse(null);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public List<Usuario> findAllUsuarioAvulsoAtivo() {

    return this.dao.findAll().list().stream()
        .filter(
            user ->
                user.getAtivo().equals(DominioSituacao.ATIVO)
                    && user.getConsignataria().getPlano().getTipo().equals(DominioPlanoUsuario.EFETIVO))
        .collect(Collectors.toList());
  }

  @ConsumeEvent(value = "update-requests-usuario", blocking = true)
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void registraRequisicaoPorRecurso(final String message) {
    log.info("Atualizando consultas do usuário: " + message);

    try {

      final Usuario usuario = Usuario.fromJson(message);

      if (usuario.getConsignataria() != null && usuario.getConsignataria().getPlano() != null) {
        this.planoService.updateConsultaByLogin(usuario.getConsignataria().getPlano());
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public List<String> allUsersByLogin(final String login) {
    return this.dao.allUsersByLogin(login);
  }

  public List<String> allUsers() {
    return this.dao.allUsers();
  }

  public Usuario salvar(final LoginDTO loginDto, final ConsignatariaArtemisDTO consignatariaDTO ,
                        final HashSet<String> permissions) {

    try {
      final Consignataria consignataria =
              this.consignatariaDao.findByIdConsignataria(consignatariaDTO.getIdConsignatariaArtemis().longValue()).get();

      final Usuario usuario = Usuario.builder()
              .nome(consignatariaDTO.getNomeUsuario())
              .ativo(DominioSituacao.ATIVO)
              .login(loginDto.getLogin())
              .senha(HashGeneratorUtil.gerarHashParaLogin(loginDto.getLogin(), loginDto.getSenha()))
              .convenio(DominioConvenio.valueOf(loginDto.getIdParceiro().intValue()))
              .isGestor(Boolean.FALSE)
              .permissao(permissions.stream().collect(Collectors.joining(",")))
              .consignataria(consignataria)
              .build();

      return this.salvarUsuario(usuario);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  @Transactional
  public Usuario salvarUsuario(final Usuario usuarioNovo) {

    final Usuario usuario = this.persist(usuarioNovo);

    return usuario;
  }
}
