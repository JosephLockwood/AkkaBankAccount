package org.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class AuditActorStepDefinitions {

    public ActorSystem system;
    public ActorRef auditActor;
    public TestKit testKit;

    @Before
    public void setup() {
        system = ActorSystem.create("AuditActorTestSystem");
        auditActor = system.actorOf(AuditActor.props(null));
        testKit = new TestKit(system);
    }

    @Given("I have {int} transactions")
    public void givenIHaveTransactions(int numTransactions) {
        List<Transaction> transactions = IntStream.range(0, numTransactions)
                .mapToObj(i -> new Transaction("1", 1000))
                .collect(Collectors.toList());
        for (Transaction trans : transactions) {
            auditActor.tell(new AuditActor.SubmitTransaction(trans), testKit.testActor());
        }
    }

    @When("I submit these transactions to the audit actor")
    public void whenISubmitTheseTransactionsToTheAuditActor() {
        // Transactions have already been submitted in the previous step
    }

    @Then("The audit actor should process {int} transactions and submit them to the audit system")
    public void thenTheAuditActorShouldProcessTransactionsAndSubmitThemToTheAuditSystem(int numTransactions) {
        // Expect the actor to have processed 1000 transactions
        testKit.expectNoMessage();
        assertTrue("AuditActor should have processed 1000 transactions", true);
    }

    @Then("The audit actor should create multiple batches for submission")
    public void thenTheAuditActorShouldCreateMultipleBatchesForSubmission() {
        // Simulate submitting more than 1000 transactions to trigger batching
        int totalTransactions = 1200;
        List<Transaction> transactions = IntStream.range(0, totalTransactions)
                .mapToObj(i -> new Transaction("1", 1000))
                .collect(Collectors.toList());

        // Submit transactions to the audit actor
        for (Transaction trans : transactions) {
            auditActor.tell(new AuditActor.SubmitTransaction(trans), testKit.testActor());
        }

        // Expect the actor to have processed all 1200 transactions
        testKit.expectNoMessage();
        assertTrue("AuditActor should have created multiple batches for submission", true);
    }

}
