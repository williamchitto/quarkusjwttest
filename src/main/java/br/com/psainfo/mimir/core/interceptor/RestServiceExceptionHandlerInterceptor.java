package br.com.psainfo.mimir.core.interceptor;

import br.com.psainfo.mimir.core.model.RetornoDTO;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.Serializable;

@Interceptor
@RestServiceExceptionHandler
public class RestServiceExceptionHandlerInterceptor implements Serializable {

  private static final long serialVersionUID = 926805010049284260L;

  @AroundInvoke
  public Object processRequest(final InvocationContext invocationContext) {

    try {
      return invocationContext.proceed();

    } catch (final WebApplicationException we) {

      throw we;
    } catch (final Exception e) {

      e.printStackTrace();

      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR)
              .entity(RetornoDTO.builder().codigo(-1).mensagem(e.getMessage()).build())
              .build());
    }
  }
}
