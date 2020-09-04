package br.com.psainfo.mimir.core.rest;

import br.com.psainfo.mimir.core.model.RetornoDTO;
import br.com.psainfo.mimir.core.seguranca.TokenUtil;
import br.com.psainfo.mimir.core.util.IpUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceContext;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractEndpoint implements Serializable {

	private static final long serialVersionUID = 7814283633678150541L;

	@Resource
	private WebServiceContext wsContext;

	@Context
	private HttpServletRequest request;

	public String getIp() {

		if(this.wsContext != null) {
			return IpUtil.getIp(this.wsContext);
		}
		else {
			return IpUtil.getIp(this.request);
		}
	}

	public Claims getClaim(final String token) {

		return TokenUtil.validateToken(token);
	}

	public Long getIdUsuarioLogado(final String token) {

		return Long.parseLong(this.getClaim(token).getId());
	}

	public String getLogin(final String token) {

		return this.getClaim(token).getIssuer();
	}

	public String serializeObjectAsJson(final List<?> result) throws JsonProcessingException {

		return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(result);
	}

	protected Response returnUnauthorizedRetornoDTO(final String msg) {

		return Response.status(Response.Status.UNAUTHORIZED).entity(RetornoDTO.builder().codigo(-1).mensagem("Acesso não permitido! " + msg).build()).type(MediaType.APPLICATION_JSON).build();
	}

}
