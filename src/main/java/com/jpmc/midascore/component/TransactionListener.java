package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    /**
     * This method listens to the Kafka topic.
     * The annotation tells Spring Boot to automatically pass any 
     * message from the topic into this method as a Transaction object.
     */
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        // Place your debugger breakpoint on the line below
        logger.info("Processing received transaction: {}", transaction);
    }
}