package com.number26.service.impl;

import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import com.number26.utils.DataNotFoundException;
import com.number26.utils.InvalidDataException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionInMemoryService implements TransactionService {

    private Map<Long, Transaction> txns = new HashMap<>();
    private Map<Long, Set<Long>> parentMapping = new HashMap<>();

    @Override
    public void putTxn(Transaction txn) {
        if (txns.containsKey(txn.getId())) {
            throw new InvalidDataException("Transaction with id: " + txn.getId() + " already exists");
        }
        if (txn.getParentId() != null) {
            checkTxnExist(txn.getParentId());
            Set<Long> children = parentMapping.get(txn.getParentId());
            if (children == null) {
                children = new HashSet<>();
                parentMapping.put(txn.getParentId(), children);
            }
            children.add(txn.getId());
        }
        txns.put(txn.getId(), txn);
    }

    @Override
    public Transaction getTxnById(long id) {
        checkTxnExist(id);
        return txns.get(id);
    }

    @Override
    public List<Long> getTxnsByType(String type) {
        return txns.entrySet().stream()
                .filter(entry -> type.equals(entry.getValue().getType()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public double getTxnSumWithChildren(long id) {
        Transaction txn = getTxnById(id);
        double sum = txn.getAmount();
        Set<Long> children = parentMapping.get(id);
        if (children != null && !children.isEmpty()) {
            sum = sum + children.stream().mapToDouble(this::getTxnSumWithChildren).sum();
        }
        return sum;
    }

    private void checkTxnExist(long id) {
        if (!txns.containsKey(id)) {
            throw new DataNotFoundException("Txn with id: " + id + " was not found");
        }
    }
}
