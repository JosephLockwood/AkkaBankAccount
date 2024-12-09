package junit;

import org.example.Transaction;
import org.junit.Test;

/*
 * This is a basic JUnit class to exercise getters/setters within the Transaction class,
 * actor behaviour will be validated using Cucumber
 */
public class TransactionTest {

    @Test
    public void testGetIdTransaction() {
        // Basic unit test to exercise Transactions getters, actor behaviour to be tested using Cucumber
        Transaction transaction = new Transaction("Test", 1);
        assert(transaction.getId().equals("Test"));
    }

    @Test
    public void testGetAmountTransaction() {
        // Basic unit test to exercise Transactions getters, actor behaviour to be tested using Cucumber
        Transaction transaction = new Transaction("Test", 1);
        assert(transaction.getAmount() == 1);
    }

}
