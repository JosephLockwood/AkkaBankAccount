package org.example;

import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Random;
import java.util.UUID;

public class TransactionProducerActor extends AbstractActor {

    private final ActorRef bankAccountActor;
    private final ActorRef auditActor;
    private final boolean isCredit;

    public static class GenerateTransaction {}

    public static Props props(ActorRef bankAccountActor, ActorRef auditActor, boolean isCredit) {
        return Props.create(TransactionProducerActor.class, bankAccountActor, auditActor, isCredit);
    }

    public TransactionProducerActor(ActorRef bankAccountActor, ActorRef auditActor, boolean isCredit) {
        this.bankAccountActor = bankAccountActor;
        this.auditActor = auditActor;
        this.isCredit = isCredit;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GenerateTransaction.class, this::generateTransaction)
                .build();
    }

    private void generateTransaction(GenerateTransaction msg) {
        Random random = new Random();
        double amount = 200 + (500000 - 200) * random.nextDouble();
        if (!isCredit) {
            amount = -amount;
        }

        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(transactionId, amount);

        bankAccountActor.tell(new BankAccountActor.ProcessTransaction(transaction), getSelf());
        auditActor.tell(new AuditActor.SubmitTransaction(transaction), getSelf());
    }

    @Override
    public void preStart() {
        // Generate 50 transactions per second
        getContext().getSystem().scheduler().scheduleAtFixedRate(
                scala.concurrent.duration.Duration.Zero(),
                scala.concurrent.duration.Duration.create(40, "milliseconds"),
                getSelf(),
                new GenerateTransaction(),
                getContext().dispatcher(),
                getSelf()
        );
    }

}
