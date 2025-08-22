package com.kt.ems.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ExternalWeatherService {
    private final WebClient client = WebClient.builder().baseUrl("https://api.open-meteo.com").build();

    /**
     * Open-Meteo: free & no-key. Seoul coords: 37.5665, 126.9780
     */
    public Map<String,Object> currentSeoul(){
        var uri = "/v1/forecast?latitude=37.5665&longitude=126.9780&current_weather=true";
        Map body = client.get().uri(uri).retrieve().bodyToMono(Map.class)
            .onErrorResume(e-> Mono.just(Map.of("error", e.getMessage())))
            .block();
        return body;
    }
}
