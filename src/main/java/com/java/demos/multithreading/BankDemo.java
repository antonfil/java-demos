package com.java.demos.multithreading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankDemo {
	private static final int ACCOUNTS_COUNT = 100;
	private static final int SINGLE_ACCOUNT_INITIAL_BALANCE = 100_000;
	
    private List<Account> accounts = new ArrayList<>(ACCOUNTS_COUNT);
    private Set<Integer> lockedAccounts = new HashSet<>();
    
    public BankDemo() {
    	for (int i = 0; i < ACCOUNTS_COUNT; i++) {
    		var account = new Account(i+1);
    		account.deposit(SINGLE_ACCOUNT_INITIAL_BALANCE);
    		accounts.add(account);
    	}
    }
    
    public List<Account> getAccounts() {
    	return accounts;
    }
    
    public int getTotalBalance() {
    	return accounts.stream().mapToInt(acc -> acc.getBalance()).sum();
    }
    
    public void transfer_1(Account from, Account to, int amount) throws InterruptedException {
    	if (from.getId() == to.getId()) return;
    	if (from.getBalance() < amount) return;
    	
    	try {
    		lockAccounts(from, to);
    		
    		from.withdraw(amount);
    		Thread.sleep(10); //Simulate long duration
    		to.deposit(amount);
    	} finally {
    		unlockAccounts(from, to);
    	}
    }
    
    public void transfer_2(Account from, Account to, int amount) throws InterruptedException {
    	if (from == to) return;
    	if (from.getBalance() < amount) return;
    	
    	var firstLock = from.getId() < to.getId() ? from : to;
    	var secondLock = from.getId() < to.getId() ? to : from;
    	synchronized(firstLock) {
    		synchronized(secondLock) {
    			from.withdraw(amount);
    			Thread.sleep(10); //Simulate long duration
    			to.deposit(amount);
    		}
    	}
    }
    
    public synchronized void transfer_3(Account from, Account to, int amount) throws InterruptedException {
    	if (from == to) return;
    	if (from.getBalance() < amount) return;
    	
		from.withdraw(amount);
		Thread.sleep(10); //Simulate long duration
		to.deposit(amount);
    }
    
    private void lockAccounts(Account from, Account to) throws InterruptedException {
    	synchronized (lockedAccounts) {
    		while(lockedAccounts.contains(from.getId()) || lockedAccounts.contains(to.getId())) {
    			lockedAccounts.wait();
    		}
    		lockedAccounts.add(from.getId());
    		lockedAccounts.add(to.getId());
    	}
    }
    
    private void unlockAccounts(Account from, Account to) {
    	synchronized (lockedAccounts) {
    		lockedAccounts.remove(from.getId());
    		lockedAccounts.remove(to.getId());
    		lockedAccounts.notifyAll();
    	}
    }
}

class Account {
	private int id;
	private int balance;
	
	public Account(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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
