package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionProcessor {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveClient incentiveClient;

    public TransactionProcessor(
            UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository,
            IncentiveClient incentiveClient
    ) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    public void process(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if (sender == null) {
            return;
        }

        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (recipient == null) {
            return;
        }

        float amount = transaction.getAmount();
        if (amount <= 0) {
            return;
        }

        if (sender.getBalance() < amount) {
            return;
        }

        float incentive = incentiveClient.fetchIncentiveAmount(transaction);

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        // Persist record; user balance changes will flush on commit.
        transactionRecordRepository.save(new TransactionRecord(sender, recipient, amount, incentive));
    }
}

