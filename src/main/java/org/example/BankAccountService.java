package org.example;

public interface BankAccountService {

    void processTransaction(Transaction transaction);

    void processTransaction(BankAccountActor.ProcessTransaction msg);

    double retrieveBalance();

}
