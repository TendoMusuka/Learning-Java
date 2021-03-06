package src.main.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import src.main.model.account.*;
import src.main.model.account.impl.Taxable;
import src.main.model.*;

public class Bank { //In charge of managing accounts and transactions 
    private ArrayList <Account> accounts;
    private ArrayList<Transaction> transactions;

    //Constructor :Initializes each ArrayList
    public Bank() {
        this.accounts = new ArrayList<Account>();
        this.transactions = new ArrayList<Transaction>();
    }


     /**
     * Name: addAccount
     * @param account (Account)
     * 
     * Inside the function:
     *   1. adds an account to the accounts ArrayList
     */

     public void addAccount(Account account){
         //this.accounts.add(new Account(account)); //Can not create a type of account so will use clone method 
         this.accounts.add(account.clone()); //use the clone method from the correct class
     }
  
    /**
     * Name: addTransaction
     * @param transaction
     * 
     * Inside the function:
     *   1. adds a new transaction object to the array list.
     */

     private void addTransaction(Transaction transaction){
         //this.transactions.add(transaction); //Rookie mistake :You need to add a copy of transaction and not the original transaction
         this.transactions.add(new Transaction(transaction));
     }

    /**
     * Name: getTransactions
     * @param accoundId (String)
     * @return (Transaction[])
     * 
     * 1. returns an array of transactions whose id matches the accountId 
     */

     public Transaction[] getTransactions(String accountId){
        List<Transaction> list = this.transactions.stream()
        .filter((transaction) -> transaction.getId().equals(accountId))
        .collect(Collectors.toList());
        
        return list.toArray(new Transaction[list.size()]); 
        // By default, toArray returns Object[]. You can force toArray to return a specific type using this syntax:
        //list.toArray(new CustomClass[list.size()]);
     }

      /**
   * Name: getAccount()
   * @param transactionId (String)
   * @return (Account)
   * 
   * 1. returns an account whose id matches a transaction. 
   */

   public Account getAccount(String transactionId){
    return accounts.stream()
        .filter((account) ->account.getId().equals(transactionId))//filter : Filters elements based on a predicate 
        .findFirst() //returns the element of the stream
        .orElse(null); //return null if findFirst does not return anything 
   }

   private void withdrawTransaction(Transaction transaction){
       if (getAccount(transaction.getId()).withdraw(transaction.getAmount())){
           addTransaction(transaction);
       }
   }

       private void depositTransaction(Transaction transaction){
        getAccount(transaction.getId()).deposit(transaction.getAmount());
        addTransaction(transaction);
   }

   /**
     * Name: executeTransaction
     * @param transaction
     * 
     * Inside the function:
     *  1. calls withdrawTransaction if transaction type is WITHDRAW
     *  2. calls depositTransaction if transaction type is DEPOSIT
     * 
     */

    public void executeTransaction(Transaction transaction){
        switch(transaction.getType()){
            case WITHDRAW: withdrawTransaction(transaction); break;
            case DEPOSIT: depositTransaction(transaction); break;
        }

    }

    private double getIncome(Taxable account) {
        Transaction[] transactions = getTransactions(((Chequing)account).getId());
        return Arrays.stream(transactions)
        .mapToDouble((transaction)->{
            switch(transaction.getType()){//compare transaction type against two possible accounts
                case WITHDRAW: return -transaction.getAmount();
                case DEPOSIT: return transaction.getAmount();
                default: return 0;
            }
        }).sum(); //return the sum of every element in the stream
    }


    public void deductTaxes(){
        for (Account account:accounts){
            if (Taxable.class.isAssignableFrom(account.getClass())){ //Checking to see if this account implements the taxable phase
                Taxable taxable = (Taxable)account; //type cast our account to taxable
                taxable.tax(getIncome(taxable));
            }
        }
    }


    
    

    
 

   


}
