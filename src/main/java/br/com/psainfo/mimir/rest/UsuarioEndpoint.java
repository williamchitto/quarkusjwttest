package br.com.psainfo.mimir.rest;

import br.com.psainfo.mimir.core.interceptor.RestServiceExceptionHandler;
import br.com.psainfo.mimir.core.model.RetornoDTO;
import br.com.psainfo.mimir.core.rest.AbstractEndpoint;
import br.com.psainfo.mimir.dominio.DominioSituacao;
import br.com.psainfo.mimir.model.jpa.base.Plano;
import br.com.psainfo.mimir.model.jpa.base.Usuario;
import br.com.psainfo.mimir.service.acesso.PlanoService;
import br.com.psainfo.mimir.service.acesso.UsuarioService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/usuario")
@Tag(name = "Manter Usuário")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(
    value = {
      @SecurityScheme(
          securitySchemeName = "http_secure",
          description =
              "Autenticação necessária para acesso aos recursos da API. (Use endpoint /auth/login)",
          type = SecuritySchemeType.HTTP,
          scheme = "bearer",
          bearerFormat = "JWT")
    })
@RestServiceExceptionHandler
// @Secured
@SecurityRequirement(
    name = "http_secure",
    scopes = {""})
public class UsuarioEndpoint extends AbstractEndpoint {

  private static final long serialVersionUID = -531784990554081362L;

  @Inject UsuarioService usuarioService;

  @Inject PlanoService planoService;

  @GET
  @Operation(summary = "Lista todos Usuários")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = Usuario.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response findAll() {

    return Response.ok(this.usuarioService.findAll()).build();
  }

  @GET
  @Path("/{id}")
  @Operation(summary = "Busca Usuário por Id")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(implementation = Usuario.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response findById(@PathParam("id") final Long id) {

    final Usuario usuario = this.usuarioService.findById(id);

    if (usuario == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    return Response.ok(usuario).build();
  }

  @GET
  @Path("/login/{login}")
  @Operation(summary = "Busca Usuário Ativo por Id")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(implementation = Usuario.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response findByLoginAtivo(@PathParam("login") final String login) {

    final Usuario usuario = this.usuarioService.findByLoginAtivo(login);

    if (usuario == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    return Response.ok(usuario).build();
  }

  @POST
  @Transactional
  @Operation(summary = "Inclui um Usuário")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(implementation = Usuario.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response persist(final Usuario usuario) {

    usuario.setAtivo(DominioSituacao.ATIVO);
    return Response.ok(this.usuarioService.persist(usuario)).build();
  }

  @PUT
  @Transactional
  @Operation(summary = "Atualiza um Usuário")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(implementation = Usuario.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response update(final Usuario usuario) {

    final Usuario usuarioManaged = this.usuarioService.findById(usuario.getId());

    usuarioManaged.setNome(usuario.getNome());
    usuarioManaged.setLogin(usuario.getLogin());
    usuarioManaged.setAtivo(usuario.getAtivo());
    usuarioManaged.setConsignataria(usuario.getConsignataria());
    usuarioManaged.setDataContratacao(usuario.getDataContratacao());
    usuarioManaged.setObservacao(usuario.getObservacao());
    usuarioManaged.setTelefone(usuario.getTelefone());
    usuarioManaged.setEmail(usuario.getEmail());
    usuarioManaged.setIsGestor(usuario.getIsGestor());
    usuarioManaged.setPermissao(usuario.getPermissao());

    return Response.ok(this.usuarioService.merge(usuarioManaged)).build();
  }

  @PUT
  @Path("/{id}/{status}")
  @Transactional
  @Operation(summary = "Alterar o Status de um Usuário")
  @APIResponse(responseCode = "200", description = "OK")
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response changeStatus(
      @PathParam("id") final Long id, @PathParam("status") final Boolean status) {

    final Usuario usuario = this.usuarioService.changeStatus(id, status);

    if (usuario == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    return Response.ok().build();
  }

  @GET
  @Path("/logins/{login}")
  @Operation(summary = "Lista os logins permitidos do usuario")
  @APIResponse(
      responseCode = "200",
      description = "OK",
      content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = String.class)))
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response allUsersByLogin(@PathParam("login") final String login) {
    return Response.ok(this.usuarioService.allUsersByLogin(login)).build();
  }

  @GET
  @Path("/logins/")
  @Operation(summary = "Lista o login de todos os Usuários")
  @APIResponse(
          responseCode = "200",
          description = "OK",
          content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = String.class)))
  @APIResponse(
          responseCode = "400",
          description = "Erro ao executar operação",
          content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
          responseCode = "401",
          description = "Não Autorizado",
          content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  public Response allUsers() {
    return Response.ok(this.usuarioService.allUsers()).build();
  }
}
