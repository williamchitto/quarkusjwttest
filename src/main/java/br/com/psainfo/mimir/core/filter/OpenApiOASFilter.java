package br.com.psainfo.mimir.core.filter;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;

import javax.ws.rs.ext.Provider;


@Provider
public class OpenApiOASFilter implements OASFilter {

	@Override
	public void filterOpenAPI(final OpenAPI openAPI) {

		openAPI.
				info(OASFactory
						.createObject(Info.class)
						.title("Mimir API")
						.version("1.0.0")
						.description("Análise Preditiva de Crédito Consignado")
						.contact(OASFactory
								.createObject(Contact.class)
								.name("PSAInfo")
								.email("contato@psainfo.com.br")
								.url("http://www.psainfo.com.br")));

	}

/*	.components(
			OASFactory
					.createComponents()
								.addSecurityScheme("Token", OASFactory
			.createSecurityScheme()
										.name("Token")
										.type(HTTP)
										.description("Autenticação necessária para acesso aos recursos da API")
										.scheme("bearer")
										.bearerFormat("jwt")))*/


}
