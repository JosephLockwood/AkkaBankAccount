package org.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.List;

public class AuditActor extends AbstractActor {

    private static final double MAX_BATCH_VALUE = 1000000.0;
    private static final int MAX_TRANSACTIONS = 1000;

    private final List<Transaction> transactions = new ArrayList<>();
    private final ActorRef bankAccountActor;

    public static class SubmitTransaction {
        public final Transaction transaction;

        public SubmitTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    public static Props props(ActorRef bankAccountActor) {
        return Props.create(AuditActor.class, bankAccountActor);
    }

    public AuditActor(ActorRef bankAccountActor) {
        this.bankAccountActor = bankAccountActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SubmitTransaction.class, this::submitTransaction)
                .build();
    }

    private void submitTransaction(SubmitTransaction msg) {
        transactions.add(msg.transaction);
        // Limit amount of transactions per submission
        if (transactions.size() == MAX_TRANSACTIONS) {
            submitToAuditSystem();
            transactions.clear();
        }
    }

    private void submitToAuditSystem() {
        List<List<Transaction>> batches = new ArrayList<>();
        List<Transaction> currentBatch = new ArrayList<>();
        int currentBatchTotal = 0;

        for (Transaction transaction : transactions) {
            double transactionValue = Math.abs(transaction.getAmount());

            if (currentBatchTotal + transactionValue > MAX_BATCH_VALUE) {
                batches.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
                currentBatchTotal = 0;
            }

            currentBatch.add(transaction);
            currentBatchTotal += transactionValue;
        }

        if (!currentBatch.isEmpty()) {
            batches.add(currentBatch);
        }

        printBatchDetails(batches);
    }

    private void printBatchDetails(List<List<Transaction>> batches) {
        int totalbatches = 1;
        int totalTransactions = 1;

        System.out.println("{");
        System.out.println("  submission: {");
        System.out.println("    batches: [");

        for (List<Transaction> batch : batches) {
            double batchTotalValue = batch.stream().mapToDouble(t -> Math.abs(t.getAmount())).sum();

            System.out.println("      {");
            System.out.println("        totalValueOfAllTransactions: " + batchTotalValue);
            System.out.println("        countOfTransactions: " + batch.size());
            System.out.println("        totalBatchesInSubmission: " + totalbatches);
            System.out.println("        totalTransactionsInSubmission: " + totalTransactions);
            System.out.println("      },");

            // Increment counters
            totalbatches++;
            totalTransactions += batch.size();
        }

        System.out.println("    ]");
        System.out.println("  }");
        System.out.println("}");
    }

}
