package cn.mrray;

import cn.mrray.filter.AccessFilter;
import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableZuulProxy
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableZuulWebSocket
@EnableWebSocketMessageBroker
public class ZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulServerApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}

	@Bean
	public AccessFilter accessControlFilter() {
		return new AccessFilter();
	}
}
