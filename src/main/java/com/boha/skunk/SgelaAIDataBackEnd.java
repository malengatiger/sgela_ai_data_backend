package com.boha.skunk;

import com.boha.skunk.util.DirectoryUtils;
import com.boha.skunk.util.E;
import com.google.firebase.database.annotations.NotNull;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
@SuppressWarnings("all")
@SpringBootApplication
/**
 *  SgelaAIDataBackEnd is a Springboot backend app that manages data for Sgela AI Agents and Chatbots
 */
public class SgelaAIDataBackEnd implements ApplicationListener<ApplicationReadyEvent> {
	static final String mm = "\uD83E\uDD66\uD83E\uDD66\uD83E\uDD66 SgelaAIDataBackEnd \uD83E\uDD66";

	static final Logger logger = Logger.getLogger(SgelaAIDataBackEnd.class.getSimpleName());
	@Value("${educUrl}")
	private String educUrl;
	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Autowired
	private ServletContext servletContext;

	public static void main(String[] args) {
		logger.info(mm+" SgelaAIDataBackEnd starting ....");
		SpringApplication.run(SgelaAIDataBackEnd.class, args);
		logger.info(mm+" SgelaAIDataBackEnd started ok!");

	}

	@Override
	public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
		logger.info(mm+" SgelaAIDataBackEnd onApplicationEvent: timestamp: "
				+ event.getTimestamp());
		logger.info(mm+"servletContext path: \uD83D\uDD90\uD83C\uDFFD "+servletContext.getContextPath());

		showApis(event);
		InetAddress ip;
		try {
			DirectoryUtils.deleteFilesInDirectories();
			ip = InetAddress.getLocalHost();
			logger.info(E.PEAR + E.PEAR + E.PEAR + E.PEAR
					+ " Current IP address : " + ip.getHostAddress());

			if (activeProfile.equalsIgnoreCase("dev")) {
				logger.info(E.PEAR + E.PEAR + E.PEAR + E.PEAR
						+ " Active Profile : Development");
				IpAddress address = getIpAddress2();
				logger.info(E.PEAR + E.PEAR + E.PEAR + E.PEAR
						+ " Current Real IP address : " + address.ipAddress + " port: " + address.port);
			}
			if (activeProfile.equalsIgnoreCase("prod")) {
				logger.info(E.PEAR + E.PEAR + E.PEAR + E.PEAR
						+ " Active Profile : Production");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Autowired
	private Environment environment;
// create test


	@Override
	public boolean supportsAsyncExecution() {
		logger.info(mm+" SgelaAIDataBackEnd supportsAsyncExecution ....");

		return ApplicationListener.super.supportsAsyncExecution();
	}
	private void showApis(ApplicationReadyEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
				.getHandlerMethods();

		logger.info(mm +
				" \uD83D\uDD35\uD83D\uDD35 Total Endpoints: " + map.size());

		List<String> pints = new ArrayList<>();
		for (HandlerMethod info : map.values()) {
			var pc = info.getMethod().getName();
			var pp = info.getMethod().getParameterCount();
			pints.add(pc + " - parameters: " + pp);
		}
		Collections.sort(pints);
		for (String pint : pints) {
			logger.info(mm + " \uD83D\uDD35\uD83D\uDD35 endPoint: " + pint);
		}
	}

	public String getIpAddress() throws Exception {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
					return address.getHostAddress();
				}
			}
		}
		throw new Exception("Unable to determine IP address");
	}

	public record IpAddress(String ipAddress, int port) {}

	public IpAddress getIpAddress2() throws UnknownHostException {
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
						try (ServerSocket socket = new ServerSocket(0)) {
							int port = socket.getLocalPort();
							return new IpAddress(address.getHostAddress(), port);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UnknownHostException("Unable to determine IP address");
	}
}
