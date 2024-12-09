package org.example;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class BankAccountActor extends AbstractActor implements BankAccountService {

    private double balance = 0;

    public static class ProcessTransaction {
        public final Transaction transaction;

        public ProcessTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    public static Props props() {
        return Props.create(BankAccountActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessTransaction.class, this::processTransaction)
                .build();
    }

    @Override
    public void processTransaction(Transaction transaction) {

    }

    @Override
    public void processTransaction(ProcessTransaction msg) {
        balance += msg.transaction.getAmount();
    }

    @Override
    public double retrieveBalance() {
        return balance;
    }

}
