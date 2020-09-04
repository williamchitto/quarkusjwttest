package br.com.psainfo.mimir.core.exception.mapper;

import br.com.psainfo.mimir.core.model.RetornoDTO;
import lombok.NoArgsConstructor;

import javax.persistence.NoResultException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@NoArgsConstructor
public class NoResultExceptionMapper implements ExceptionMapper<NoResultException> {

	@Override
	public Response toResponse(final NoResultException exception) {

		return Response.status(Response.Status.NOT_FOUND).entity(RetornoDTO.builder().codigo(-1).mensagem("Recurso não encontrado: " + exception.getMessage()).build())
				.type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
