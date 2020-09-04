// package br.com.psainfo.mimir.core.filter;
//
// import lombok.extern.java.Log;
// import org.jboss.resteasy.plugins.interceptors.CorsFilter;
//
// import javax.ws.rs.container.PreMatching;
// import javax.ws.rs.ext.Provider;
//
// @Provider
// @PreMatching
// @Log
// public class CORSFilterOld extends CorsFilter {
//
//  public CORSFilterOld() {
//
//    super();
//    super.getAllowedOrigins().add("*");
//    super.setAllowedMethods("POST, PUT, GET, DELETE, OPTIONS");
//    super.setAllowedHeaders("Origin, X-Requested-With, Content-Type, Accept, Authorization");
//    super.setCorsMaxAge(3600);
//    super.setAllowCredentials(true);
//  }
// }
