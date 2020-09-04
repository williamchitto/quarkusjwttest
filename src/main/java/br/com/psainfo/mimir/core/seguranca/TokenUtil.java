package br.com.psainfo.mimir.core.seguranca;

import br.com.psainfo.mimir.core.model.JwtBodyDTO;
import br.com.psainfo.mimir.core.util.DataUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.NotAuthorizedException;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Log
public class TokenUtil {

	private static final String API_SECRET = "FD878QW666cii4df7856sd78fashfakj55IIsdghJHJHh34232kjh4kjdfgd7866574fhUI94";

	public static String generatedToken(final Long idUsuario, final String login) {

		return generatedToken(idUsuario, login, new HashSet<>());
	}

	public static String generatedToken(final Long idUsuario, final String login, final Set<String> permissions) {

		return generatedToken(idUsuario, login, permissions, null, null);
	}

	public static String generatedToken(final Long idUsuario, final String login, final Set<String> permissions, final String nomeUsuario, final Integer tipoAcesso) {

		// The JWT signature algorithm we will be using to sign the token
		final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		final Date now = new Date();

		// We will sign our JWT with our ApiKey secret
		final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(TokenUtil.API_SECRET);
		final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		final JwtBuilder builder = Jwts.builder()
				.setId(idUsuario.toString())
				.setIssuedAt(now)
				.setIssuer(login)
				.signWith(signatureAlgorithm, signingKey)
				.claim("permissions", permissions)
				.claim("name", (StringUtils.isBlank(nomeUsuario) ? "" : nomeUsuario))
				.claim("accessType", (tipoAcesso == null ? 0 : tipoAcesso));

		// if it has been specified, let's add the expiration
		builder.setExpiration(DataUtil.somaMinutos(now, 120L)); //TODO: Voltar expiração do token para 15 minutos

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	public static String generatedToken(final Long idUsuario, final String login,
										final Long codCovenio, final Long idConsignataria,
										final Set<String> permissions, final String nomeUsuario, final Integer tipoAcesso) {

		// The JWT signature algorithm we will be using to sign the token
		final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		final Date now = new Date();

		// We will sign our JWT with our ApiKey secret
		final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(TokenUtil.API_SECRET);
		final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		final JwtBuilder builder = Jwts.builder()
				.setId(idUsuario.toString())
				.setIssuedAt(now)
				.setIssuer(login)
				.signWith(signatureAlgorithm, signingKey)
				.claim("permissions", permissions)
				.claim("name", (StringUtils.isBlank(nomeUsuario) ? "" : nomeUsuario))
				.claim("covenio", codCovenio == null ? "" : codCovenio.toString())
				.claim("consignataria", idConsignataria == null ? "" : idConsignataria.toString())
				.claim("accessType", (tipoAcesso == null ? 0 : tipoAcesso));

		// if it has been specified, let's add the expiration
		builder.setExpiration(DataUtil.somaMinutos(now, 120L)); //TODO: Voltar expiração do token para 15 minutos

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	public static Claims validateToken(final String token) {

		// This line will throw an exception if it is not a signed JWS (as expected)
		try {
			return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(TokenUtil.API_SECRET)).parseClaimsJws(token.replaceAll("Bearer ", "")).getBody();
		}
		catch (final Exception e) {
			log.severe(e.getMessage());
			throw new NotAuthorizedException("Accesso não Autorizado! " + e.getMessage());
		}
	}

	public static JwtBodyDTO parseToken(final String token) {

		if(token == null || token.isEmpty()) {
			return null;
		}

		final JwtParser jwtParser = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(TokenUtil.API_SECRET));
		final Jwt jwt = jwtParser.parse(token.replaceAll("Bearer ", ""));

		if(jwt == null) {
			return null;
		}

		final String json = new Gson().toJson(jwt.getBody());

		return new Gson().fromJson(json, JwtBodyDTO.class);
	}


}
