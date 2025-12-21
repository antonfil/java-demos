package com.java.demos.multithreading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankDemo {
	private static final int ACCOUNTS_COUNT = 10;
	private static final int SINGLE_ACCOUNT_INITIAL_BALANCE = 100_000;
	
    private List<Account> accounts = new ArrayList<>(ACCOUNTS_COUNT);
    private Set<Account> lockedAccounts = new HashSet<>();
    
    public BankDemo() {
    	for (int i = 0; i < ACCOUNTS_COUNT; i++) {
    		accounts.add(new Account(SINGLE_ACCOUNT_INITIAL_BALANCE));
    	}
    }
    
    public List<Account> getAccounts() {
    	return accounts;
    }
    
    public int getTotalBalance() {
    	return accounts.stream().mapToInt(acc -> acc.getBalance()).sum();
    }
    
    public void transfer(Account from, Account to, int amount) throws InterruptedException {
    	try {
    		lockAccounts(from, to);
    		
    		from.withdraw(amount);
    		Thread.sleep(100); //Simulate long duration
    		to.deposit(amount);
    	} finally {
    		unlockAccounts(from, to);
    	}
    }
    
    public synchronized void transfer_bad(Account from, Account to, int amount) throws InterruptedException {
		from.withdraw(amount);
		Thread.sleep(100); //Simulate long duration
		to.deposit(amount);
    }
    
    private void lockAccounts(Account from, Account to) throws InterruptedException {
    	synchronized (lockedAccounts) {
    		while(lockedAccounts.contains(from) || lockedAccounts.contains(to)) {
    			lockedAccounts.wait();
    		}
    		lockedAccounts.add(from);
    		lockedAccounts.add(to);
    	}
    }
    
    private void unlockAccounts(Account from, Account to) {
    	synchronized (lockedAccounts) {
    		lockedAccounts.remove(from);
    		lockedAccounts.remove(to);
    		lockedAccounts.notifyAll();
    	}
    }
}

class Account {
	private int balance;
	
	public Account(int balance) {
		this.balance = balance;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public void deposit(int amount) {
		balance += amount;
	}
	
	public void withdraw(int amount) {
		balance -= amount;
	}
}
