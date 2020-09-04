package br.com.psainfo.mimir.core.seguranca;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGeneratorUtil {

	/**
	 * Default construct
	 */
	protected HashGeneratorUtil() {

		// Utility class
	}

	public static String gerarHashParaLogin(final String usuario, final String senha) {

		return HashGeneratorUtil.generateSHA1(usuario + senha);
	}

	// Criado para bater com o gerador MD5 do MySQL
	public static String gerarHashParaLogin(final String senha) {

		if (senha == null) {
			return null;
		}

		return org.apache.commons.codec.digest.DigestUtils.md5Hex(senha);
	}

	public static String generateSHA1(final String key) {

		final StringBuffer sb = new StringBuffer();
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-1");

			md.reset();

			md.update(key.getBytes());

			for (final byte mdbytes : md.digest()) {
				sb.append(Integer.toString((mdbytes & 0xff) + 0x100, 16).substring(1));
			}

		} catch (final NoSuchAlgorithmException e) {

		}

		return sb.toString();

	}

	public static String generateMD5(final String senha) throws Exception {

		if (senha == null) {
			return null;
		}

		final MessageDigest md = MessageDigest.getInstance("MD5");

		final BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));

		return String.format("%32x", hash);

	}

	public static void main(String[] args) {

		System.out.println(HashGeneratorUtil.gerarHashParaLogin("nomedousuario", "senha"));

	}

}
