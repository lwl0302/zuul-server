package cn.mrray;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
@EnableZuulWebSocket
@EnableWebSocketMessageBroker
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulServerApplication.class, args);
	}
}
