package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final DatabaseConduit databaseConduit;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    // This annotation binds the method to the topic defined in your YAML file
    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        databaseConduit.process(transaction);
    }
}
