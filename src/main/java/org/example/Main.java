package org.example;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        // Create a new actor system in which the actors will reside
        ActorSystem system = ActorSystem.create("BankSystem");

        // Create the BankAccountActor and AuditActor
        ActorRef bankAccountActor = system.actorOf(Props.create(BankAccountActor.class), "bankAccountActor");
        ActorRef auditActor = system.actorOf(Props.create(AuditActor.class, bankAccountActor), "auditActor");

        // Create TransactionProducer actors for credits and debits
        ActorRef creditProducer = system.actorOf(Props.create(TransactionProducerActor.class, bankAccountActor, auditActor, true), "creditProducer");
        ActorRef debitProducer = system.actorOf(Props.create(TransactionProducerActor.class, bankAccountActor, auditActor, false), "debitProducer");

        // Spring application for REST API
        SpringApplication.run(Main.class, args);
    }

}
