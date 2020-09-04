package br.com.psainfo.mimir.core.exception.mapper;

import br.com.psainfo.mimir.core.model.RetornoDTO;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@NoArgsConstructor
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

	@Override
	public Response toResponse(final NotAuthorizedException e) {

		final RetornoDTO retornoDTO = RetornoDTO.builder().codigo(-1)
				.mensagem("Erro ao acessar recurso: " + e.getMessage())
				.build();

		if(CollectionUtils.isNotEmpty(e.getChallenges())) {
			retornoDTO.setMensagem(e.getChallenges().stream().findFirst().get().toString());
		}

		return Response
				.status(e.getResponse().getStatus())
				.entity(retornoDTO)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

}
