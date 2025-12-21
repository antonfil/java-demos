package com.java.demos.multithreading;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;


@FunctionalInterface
interface TransferOp {
    void transfer(Account from, Account to, int amount) throws InterruptedException;
}


/**
 * Unit tests for BankDemo
 */
public class BankDemoTest {
	private static final int TEST_TRANSFERS_COUNT = 100;
	private static final int TEST_TRANSFERS_MAX_AMOUNT = 1000;

    @Test
    public void shouldSyncTransfersBetweenAccounts_1() throws Exception {
    	BankDemo bank = new BankDemo(); 
        int initialTotalBalance = bank.getTotalBalance();
        
        runTransfers(bank, bank::transfer_1);

        int finalTotalBalance = bank.getTotalBalance();
        assertEquals(initialTotalBalance, finalTotalBalance);
    }
    
    @Test
    public void shouldSyncTransfersBetweenAccounts_2() throws Exception {
    	BankDemo bank = new BankDemo(); 
        int initialTotalBalance = bank.getTotalBalance();
        
        runTransfers(bank, bank::transfer_2);

        int finalTotalBalance = bank.getTotalBalance();
        assertEquals(initialTotalBalance, finalTotalBalance);
    }
    
    @Test
    public void shouldSyncTransfersBetweenAccounts_3() throws Exception {
    	BankDemo bank = new BankDemo(); 
        int initialTotalBalance = bank.getTotalBalance();
        
        runTransfers(bank, bank::transfer_3);

        int finalTotalBalance = bank.getTotalBalance();
        assertEquals(initialTotalBalance, finalTotalBalance);
    }
    
    private void runTransfers(BankDemo bank, TransferOp operation) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(TEST_TRANSFERS_COUNT);
        List<Future<?>> futures = new ArrayList<>(TEST_TRANSFERS_COUNT); 
        for (int i = 0; i < TEST_TRANSFERS_COUNT; i++) {
        	futures.add(executorService.submit(() -> {
        		Random rand = new Random();
        		int fromAccountIndex = rand.nextInt(bank.getAccounts().size());
        		int toAccountIndex = rand.nextInt(bank.getAccounts().size());
        		Account from = bank.getAccounts().get(fromAccountIndex);
        		Account to = bank.getAccounts().get(toAccountIndex);
        		int amount = rand.nextInt(TEST_TRANSFERS_MAX_AMOUNT);
        		operation.transfer(from, to, amount);
        		return true;
        	}));
        }
        executorService.shutdown();
        for (Future<?> f : futures) {
            f.get();
        }
	}
}
