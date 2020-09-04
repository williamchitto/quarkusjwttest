package br.com.psainfo.mimir.core.seguranca;

import br.com.psainfo.mimir.core.model.JwtBodyDTO;
import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Priority;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityCheckFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Context ResourceInfo resourceInfo;

  @Override
  public void filter(final ContainerRequestContext requestContext) {

    // Get the HTTP Authorization header from the request
    final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    // Check if the HTTP Authorization header is present and formatted correctly
    if (authorizationHeader == null) {
      throw new NotAuthorizedException("Accesso não Autorizado! Token não encontrado.");
    }

    try {
      // Validate the token
      final Claims claims = TokenUtil.validateToken(authorizationHeader);

      if ((claims == null) || claims.getExpiration().before(new Date())) {
        throw new NotAuthorizedException("Accesso não Autorizado! Token expirado.");
      }

      final Set<String> requiredPermissions = this.getRequiredPermissions();

      if (!requiredPermissions.isEmpty()) {

        final Set<String> userPermissions =
            ((List<String>) claims.get("permissions")).stream().collect(Collectors.toSet());

        if (!userPermissions.containsAll(requiredPermissions)) {
          throw new NotAllowedException(
              "Accesso não Autorizado! Usuário não tem permissão para realizar esta operação.");
        }
      }
    } catch (final NotAuthorizedException nae) {

      if (CollectionUtils.isNotEmpty(nae.getChallenges())) {
        throw new NotAuthorizedException(nae.getChallenges().stream().findFirst().get().toString());
      }

      throw nae;
    } catch (final Exception e) {
      throw new NotAuthorizedException("Accesso não Autorizado! " + e.getMessage());
    }
  }

  /**
   * Set of required permissions annotated in method
   *
   * @return
   */
  private Set<String> getRequiredPermissions() {

    final Set<String> requiredPermissions = new HashSet<>();

    final Class<?> resourceClass = this.resourceInfo.getResourceClass();
    final Secured securedAnnotationClass = resourceClass.getAnnotation(Secured.class);

    if (securedAnnotationClass != null && securedAnnotationClass.permissions().length > 0) {
      requiredPermissions.addAll(
          new HashSet<>(Arrays.asList(securedAnnotationClass.permissions())));
    }

    final Method resourceMethod = this.resourceInfo.getResourceMethod();
    final Secured securedAnnotationMethod = resourceMethod.getAnnotation(Secured.class);

    if (securedAnnotationMethod != null && securedAnnotationMethod.permissions().length > 0) {
      requiredPermissions.addAll(
          new HashSet<>(Arrays.asList(securedAnnotationMethod.permissions())));
    }

    return requiredPermissions;
  }

  @Override
  public void filter(
      final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
      throws IOException {

    // Get the HTTP Authorization header from the request
    final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    // Check if the HTTP Authorization header is present and formatted correctly
    if (authorizationHeader == null) {
      return;
    }

    try {

      // Validate the token
      final Claims claims = TokenUtil.validateToken(authorizationHeader);

      if ((claims == null) || claims.getExpiration().before(new Date())) {
        return;
      }

      final JwtBodyDTO jwtBodyDTO = TokenUtil.parseToken(authorizationHeader);

      responseContext
          .getHeaders()
          .add(
              HttpHeaders.AUTHORIZATION,
              TokenUtil.generatedToken(
                  Long.parseLong(TokenUtil.validateToken(authorizationHeader).getId()),
                  TokenUtil.validateToken(authorizationHeader).getIssuer(),
                  new HashSet<>(),
                  jwtBodyDTO.getName(),
                  jwtBodyDTO.getAccessType()));
    } catch (final Exception e) {
      // Desconsidera qualquer erro ao tentar renovar o token
    }
  }
}
