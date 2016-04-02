package com.number26.service;

import com.number26.model.Transaction;

import java.util.List;

public interface TransactionService {

    /**
     * Creates a new transaction
     *
     * @param txn - new transaction
     * */
    void putTxn(Transaction txn);

    /**
     * Finds transaction by id
     *
     * @param id - unique id specifying a new transaction
     *
     * @return Transaction
     * */
    Transaction getTxnById(long id);

    /**
     * Finds all transactions that share the same type
     *
     * @param type - type of the transaction
     *
     * @return a list of ids transactions
     * */
    List<Long> getTxnsByType(String type);

    /**
     * Returns a sum of all transactions that are transitively linked by their parent_id to id
     *
     * @param id - unique id specifying a new transaction
     *
     * @return sum
     * */
    double getTxnSumWithChildren(long id);
}
