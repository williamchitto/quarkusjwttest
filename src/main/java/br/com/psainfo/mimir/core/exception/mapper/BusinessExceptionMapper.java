package br.com.psainfo.mimir.core.exception.mapper;

import br.com.psainfo.mimir.core.exception.BusinessException;
import br.com.psainfo.mimir.core.model.RetornoDTO;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@NoArgsConstructor
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

	@Override
	public Response toResponse(final BusinessException e) {

		return Response.status(Response.Status.BAD_REQUEST).entity(RetornoDTO.builder().codigo(-1).mensagem(e.getMessage()).build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
