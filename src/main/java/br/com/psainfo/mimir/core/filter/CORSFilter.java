// package br.com.psainfo.mimir.core.filter;
//
// import javax.ws.rs.container.ContainerRequestContext;
// import javax.ws.rs.container.ContainerResponseContext;
// import javax.ws.rs.container.ContainerResponseFilter;
// import javax.ws.rs.ext.Provider;
// import java.io.IOException;
//
// @Provider
// public class CORSFilter implements ContainerResponseFilter {
//
//  @Override
//  public void filter(
//      final ContainerRequestContext requestContext, final ContainerResponseContext
// responseContext)
//      throws IOException {
//
//    responseContext.getHeaders().add("Access-Control-Allow-Origin",
// "https://mimir.psainfo.com.br");
//
//    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
//
//    responseContext
//        .getHeaders()
//        .add("Access-Control-Allow-Headers", "content-type, accept, authorization");
//
//    responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
//
//    responseContext
//        .getHeaders()
//        .add("Access-Control-Expose-Headers", "Authorization, systemMessage");
//  }
// }
