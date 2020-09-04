package br.com.psainfo.mimir.rest;

import br.com.psainfo.mimir.client.GestorWsAuthenticationClient;
import br.com.psainfo.mimir.core.model.JwtBodyDTO;
import br.com.psainfo.mimir.core.model.RetornoDTO;
import br.com.psainfo.mimir.core.rest.AbstractEndpoint;
import br.com.psainfo.mimir.core.seguranca.Secured;
import br.com.psainfo.mimir.core.seguranca.TokenUtil;
import br.com.psainfo.mimir.dominio.DominioTipoUsuario;
import br.com.psainfo.mimir.model.dto.analise.ConsignatariaArtemisDTO;
import br.com.psainfo.mimir.model.dto.login.GestorWsLoginResponseDTO;
import br.com.psainfo.mimir.model.dto.login.LoginDTO;
import br.com.psainfo.mimir.model.jpa.base.Usuario;
import br.com.psainfo.mimir.service.acesso.ConsignatariaService;
import br.com.psainfo.mimir.service.acesso.UsuarioService;
import com.google.gson.Gson;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.HashSet;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação")
@Log
public class AuthenticationEndpoint extends AbstractEndpoint {

  private static final long serialVersionUID = 1614067073907379061L;

  @Inject HttpServletRequest request;

  @Inject UsuarioService usuarioService;

  @Inject ConsignatariaService consignatariaService;

  @Inject @RestClient GestorWsAuthenticationClient gestorWs;

  @POST
  @Path("/login")
  @Operation(summary = "Efetua Login na API")
  @RequestBody(
      name = "loginDto",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LoginDTO.class),
              example = "{\"idParceiro\":31,\"login\":\"admin\",\"senha\":\"artemisdev022020\"}"),
      required = true)
  @APIResponse(
      responseCode = "200",
      description = "OK",
      headers = @Header(name = "Authorization", description = "JWT Token"),
      content = @Content(schema = @Schema(implementation = GestorWsLoginResponseDTO.class)))
  @APIResponse(responseCode = "404", description = "Registro não encontrado")
  @APIResponse(
      responseCode = "400",
      description = "Erro ao executar operação",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  @APIResponse(
      responseCode = "401",
      description = "Não Autorizado",
      content = @Content(schema = @Schema(implementation = RetornoDTO.class)))
  public Response login(@Valid final LoginDTO loginDto) {

    try {
      final Usuario usuarioMimir = this.usuarioService.findByLoginConveniosSenhaAtivo(loginDto);

      if (usuarioMimir != null && usuarioMimir.getIsGestor()) {

        // Efetua o login sem consultar no gestorws, pois é admin
        final GestorWsLoginResponseDTO gestorWsLoginResponseDTO =
            GestorWsLoginResponseDTO.builder()
                .idUsuario(usuarioMimir.getId())
                .nomeUsuario(usuarioMimir.getNome())
                .tipoAcesso(DominioTipoUsuario.GESTOR_PLATAFORMA.getCod())
                .build();

        final HashSet<String> permissions = new HashSet<>();
        permissions.add("administrador");

        final String generatedToken =
            TokenUtil.generatedToken(
                gestorWsLoginResponseDTO.getIdUsuario(),
                loginDto.getLogin(),
                loginDto.getIdParceiro(), // Codigo do Convênio
                null, // IsGestor não estão atrelados a nenhuma consignataria
                permissions,
                gestorWsLoginResponseDTO.getNomeUsuario(),
                gestorWsLoginResponseDTO.getTipoAcesso());

        gestorWsLoginResponseDTO.setTokenJwt(generatedToken);

        return Response.ok()
            .entity(gestorWsLoginResponseDTO)
            .header("Authorization", generatedToken)
            .build();
      }

      final Response validarUsuarioResponse =
          this.gestorWs.validarUsuarioPlataformaGestao(loginDto);

      if (validarUsuarioResponse.getStatus() != Status.OK.getStatusCode()) {
        if (validarUsuarioResponse.getStatus() == Status.UNAUTHORIZED.getStatusCode()) {
          final String msg = validarUsuarioResponse.readEntity(String.class);
          return super.returnUnauthorizedRetornoDTO(msg);
        }
        return validarUsuarioResponse;
      }

      final GestorWsLoginResponseDTO gestorWsLoginResponseDTO =
          validarUsuarioResponse.readEntity(GestorWsLoginResponseDTO.class);

      if (gestorWsLoginResponseDTO.getTipoAcesso() < 1) {
        return super.returnUnauthorizedRetornoDTO(gestorWsLoginResponseDTO.getMensagem());
      }

      final boolean permissaoAcesso =
          this.consignatariaService.consultarPermissaoByUsuario(
              loginDto.getIdParceiro(), gestorWsLoginResponseDTO.getIdUsuario(), "acesso_mimir");

      if (!permissaoAcesso) {
        return super.returnUnauthorizedRetornoDTO(
            "Usuário não possui permissão de acesso ao sistema. Solicite ao usuário master da consignatária.");
      }

      final HashSet<String> permissions = new HashSet<>();
      permissions.add("acesso_mimir");

        // Atualiza no mimir a consignataria retornada pelo gestorws
        final ConsignatariaArtemisDTO consignatariaArtemisDTO = this.consignatariaService.atualizaConsignataria(loginDto,
                gestorWsLoginResponseDTO.getIdUsuario());

      // Salva usuario no banco mimir se não cadastrado
      if (usuarioMimir == null && consignatariaArtemisDTO != null) {
        this.usuarioService.salvar(loginDto, consignatariaArtemisDTO, permissions);
      }

      // ADICIONAR O CODIGO DA CONSIGNATARIA E DO CONVENIO NO TOKEN
      final String generatedToken =
          TokenUtil.generatedToken(
              gestorWsLoginResponseDTO.getIdUsuario(),
              loginDto.getLogin(),
              loginDto.getIdParceiro(), // Codigo do Convênio
              consignatariaArtemisDTO != null
                  ? consignatariaArtemisDTO.getIdConsignatariaArtemis().longValue()
                  : null,
              permissions,
              gestorWsLoginResponseDTO.getNomeUsuario(),
              gestorWsLoginResponseDTO.getTipoAcesso());

      gestorWsLoginResponseDTO.setTokenJwt(generatedToken);

      return Response.ok()
          .entity(gestorWsLoginResponseDTO)
          .header("Authorization", generatedToken)
          .build();

    } catch (final Exception e) {
      e.printStackTrace();
      return super.returnUnauthorizedRetornoDTO(e.getMessage());
    }
  }

  @GET
  @Path("/me")
  @Secured
  public Response me(@Context final ContainerRequestContext requestContext) {

    final String token = this.request.getHeader(HttpHeaders.AUTHORIZATION);

    final JwtBodyDTO jwtBodyDTO = TokenUtil.parseToken(token);

    final String json = new Gson().toJson(jwtBodyDTO);

    return Response.ok(json).build();
  }
}
