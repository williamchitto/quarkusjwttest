package br.com.psainfo.mimir.core.exception.mapper;

import br.com.psainfo.mimir.core.model.RetornoDTO;
import lombok.NoArgsConstructor;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@NoArgsConstructor
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

	@Override
	public Response toResponse(final NotAllowedException e) {

		return Response
				.status(e.getResponse().getStatus())
				.entity(RetornoDTO.builder().codigo(-1)
						.mensagem("Erro ao acessar recurso: " + e.getMessage())
						.build())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

}
