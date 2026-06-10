package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    // This annotation binds the method to the topic defined in your YAML file
    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        // We will place our debugger breakpoint on the line below
        float amount = transaction.getAmount();
        System.out.println("Received transaction with amount: " + amount);
    }
}
