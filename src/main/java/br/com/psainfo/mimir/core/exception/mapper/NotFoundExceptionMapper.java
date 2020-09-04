package br.com.psainfo.mimir.core.exception.mapper;

import br.com.psainfo.mimir.core.model.RetornoDTO;
import lombok.NoArgsConstructor;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@NoArgsConstructor
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(final NotFoundException e) {

		return Response.status(Response.Status.NOT_FOUND).entity(RetornoDTO.builder().codigo(-1).mensagem("Recurso não encontrado: " + e.getMessage()).build()).type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

}
