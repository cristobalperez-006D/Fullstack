package cl.duoc.nat_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class NatGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NatGatewayApplication.class, args);
	}

}
