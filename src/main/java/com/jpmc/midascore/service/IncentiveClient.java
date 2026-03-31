package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveClient {
    private final RestTemplate restTemplate;
    private final String incentiveUrl;

    public IncentiveClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${general.incentive-api-url:http://localhost:33433/incentive}") String incentiveUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.incentiveUrl = incentiveUrl;
    }

    public float fetchIncentiveAmount(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveUrl, transaction, Incentive.class);
            if (incentive == null) {
                return 0f;
            }
            return Math.max(0f, incentive.getAmount());
        } catch (Exception e) {
            // Treat incentive service as best-effort; if down, no incentive.
            return 0f;
        }
    }
}

