/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;


import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {
    private ExpenseManager expenseManager;

    @Before
    public void setup(){
        expenseManager = new PersistentExpenseManager(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void addAccountTest(){
        expenseManager.addAccount("150A","BOC","Kasun Pavithra",4500.00);
        List<String> accountNumbers = expenseManager.getAccountNumbersList();
        assertTrue(accountNumbers.contains("150A"));

    }
    @Test
    public void logTransactionTest(){
        //add account first
        String testAccountNO = "124B";
        expenseManager.addAccount(testAccountNO,"ABC bank","Kasun",45000.00);
        // log transaction and test
        Date date = new Date();
        TransactionDAO transactionDAO = expenseManager.getTransactionsDAO();
        transactionDAO.logTransaction(date,testAccountNO, ExpenseType.INCOME,250.00);
        Transaction transaction = new Transaction(date,testAccountNO,ExpenseType.INCOME,250.00);
        List<Transaction> transactionsFromDatabase = transactionDAO.getPaginatedTransactionLogs(10);

        Transaction trn=null;
        for (Transaction trnscn: transactionsFromDatabase) {
            if(trnscn.getAccountNo().equals(transaction.getAccountNo())){
                trn = trnscn;
                break;
            }
        }
        if(trn==null) assertTrue(false);

        assertTrue(trn.getAccountNo().equals(transaction.getAccountNo()));
        assertTrue(trn.getAmount()==transaction.getAmount());
        assertTrue(isSameDate(transaction.getDate(),trn.getDate()));

    }

    @Test
    public void updateAccountBalanceTest1() {
        String testAccountNO = "400C";
        expenseManager.addAccount(testAccountNO,"ABC bank","Kasun",45000.00);

        AccountDAO accountDAO = expenseManager.getAccountsDAO();
        TransactionDAO transactionDAO = expenseManager.getTransactionsDAO();

        //test updateAccountBalance funtion
        try {
            double previousBalance = accountDAO.getAccount(testAccountNO).getBalance();
            expenseManager.updateAccountBalance(testAccountNO, 22, 5, 2022, ExpenseType.INCOME, "250.00");
           assertTrue(accountDAO.getAccount(testAccountNO).getBalance()==previousBalance+250.00);
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }
    }

    @Test
    public void updateAccountBalanceTest2() {
        String testAccountNO = "777Z";
        expenseManager.addAccount(testAccountNO,"ABC bank","Kasun",45000.00);

        AccountDAO accountDAO = expenseManager.getAccountsDAO();
        TransactionDAO transactionDAO = expenseManager.getTransactionsDAO();

        try {
            double previousBalance = accountDAO.getAccount(testAccountNO).getBalance();
            expenseManager.updateAccountBalance(testAccountNO, 22, 5, 2022, ExpenseType.EXPENSE, "250.00");
            assertTrue(accountDAO.getAccount(testAccountNO).getBalance()==previousBalance-250.00);
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }
    }

    @Test
    public void removeAccountTest(){
        String testAccountNO = "800P";
        expenseManager.addAccount(testAccountNO,"ABC bank","Kasun",45000.00);

        try {
            expenseManager.getAccountsDAO().removeAccount(testAccountNO);
            assertTrue(!expenseManager.getAccountNumbersList().contains(testAccountNO));
        } catch (InvalidAccountException e) {
            assertTrue(false);
        }
    }

    private boolean isSameDate(Date date1,Date date2){
        if(date1.getDate()==date2.getDate() && date1.getMonth()==date2.getMonth() && date1.getYear()==date2.getYear()){
            return true;
        }
        return false;
    }


}