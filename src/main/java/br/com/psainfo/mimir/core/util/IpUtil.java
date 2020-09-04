package br.com.psainfo.mimir.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@Log
@UtilityClass
public class IpUtil {

	private static final String UNKNOWN = "unknown";

	public static String getIp(final WebServiceContext wsContext) {

		try {
			final MessageContext mc = wsContext.getMessageContext();
			final HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
			String ipAddress = req.getHeader("X-FORWARDED-FOR");
			if(ipAddress == null) {
				ipAddress = req.getRemoteAddr();
			}
			return ipAddress;
		}
		catch (final Exception e) {
			log.severe(e.getMessage());
			return "";
		}
	}

	public static String getIp(final HttpServletRequest request) {

		try {
			String ip = request.getHeader("X-Forwarded-For");

			if((ip == null) || (ip.length() == 0) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}

			if((ip == null) || (ip.length() == 0) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}

			if((ip == null) || (ip.length() == 0) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}

			if((ip == null) || (ip.length() == 0) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}

			if((ip == null) || (ip.length() == 0) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}

			return ip;
		}
		catch (final Exception e) {
			log.severe(e.getMessage());
			return "";
		}
	}

}
