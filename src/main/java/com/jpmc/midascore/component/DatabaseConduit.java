package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplateBuilder restTemplateBuilder) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    @Transactional
    public void process(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender == null || recipient == null) {
            return;
        }

        float amount = transaction.getAmount();
        if (sender.getBalance() < amount) {
            return;
        }

        float incentive = getIncentive(transaction);
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, amount, incentive));
    }

    private float getIncentive(Transaction transaction) {
        ResponseEntity<Incentive> response = restTemplate.postForEntity(INCENTIVE_URL, transaction, Incentive.class);
        Incentive incentive = response.getBody();
        return incentive == null ? 0.0f : incentive.getAmount();
    }

}
