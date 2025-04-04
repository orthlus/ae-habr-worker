package art.aelaort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplatesConfig {
	@Bean
	public RestTemplate habrRestTemplate(RestTemplateBuilder restTemplateBuilder,
							 @Value("${habr.http.timeout}") int timeout,
							 @Value("${habr.http.delay}") int delay) {
		String baseUrl = "https://habr.com/ru";
		return restTemplateBuilder.rootUri(baseUrl)
				.connectTimeout(Duration.ofSeconds(timeout))
				.readTimeout(Duration.ofSeconds(timeout))
				.interceptors((request, body, execution) -> {
					try {
						TimeUnit.SECONDS.sleep(delay);
					} catch (InterruptedException ignored) {
					}
					return execution.execute(request, body);
				})
				.build();
	}
}
