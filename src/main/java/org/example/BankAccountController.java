package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    // Expose the balance over a GET endpoint
    @GetMapping("/balance")
    public double getBalance() {
        return bankAccountService.retrieveBalance();
    }

}
