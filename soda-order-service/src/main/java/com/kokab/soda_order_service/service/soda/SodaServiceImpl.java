package com.kokab.soda_order_service.service.soda;

import bottling.model.SodaDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
@ConfigurationProperties(prefix = "sfg.bottling", ignoreUnknownFields = false)
@Service
public class SodaServiceImpl implements SodaService{

    public final static String BEER_PATH_V1 = "/api/v1/soda/";
    public final static String BEER_UPC_PATH_V1 = "/api/v1/sodaUpc/";
    private final RestTemplate restTemplate;

    private String sodaServiceHost;

    public SodaServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<SodaDto> getSodaById(UUID uuid){
        return Optional.of(restTemplate.getForObject(sodaServiceHost + BEER_PATH_V1 + uuid.toString(), SodaDto.class));
    }

    @Override
    public Optional<SodaDto> getSodaByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(sodaServiceHost + BEER_UPC_PATH_V1 + upc, SodaDto.class));
    }

    public void setSodaServiceHost(String sodaServiceHost) {
        this.sodaServiceHost = sodaServiceHost;
    }
    
}
