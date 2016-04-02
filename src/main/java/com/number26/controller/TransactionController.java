package com.number26.controller;

import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/transactionservice")
public class TransactionController {

    private static final Map<String, String> OK_MESSAGE = Collections.singletonMap("status", "ok");
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService txnService;

    @RequestMapping(value = "/transaction/{id}", method = RequestMethod.PUT)
    public Map<String, String> putTxn(@PathVariable long id, @Valid @RequestBody Transaction txn) {
        logger.debug("putTxn(): id={}, txn{}", id, txn);
        txn.setId(id);
        txnService.putTxn(txn);
        return OK_MESSAGE;
    }

    @RequestMapping(value = "/transaction/{id}", method = RequestMethod.GET)
    public ResponseEntity<Transaction> getTxn(@PathVariable long id) {
        logger.debug("getTxn(): id={}", id);
        return new ResponseEntity<>(txnService.getTxnById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public List<Long> getTxnsByType(@PathVariable String type) {
        logger.debug("getTxnsByType(): type={}", type);
        return txnService.getTxnsByType(type);
    }

    @RequestMapping(value = "/sum/{id}", method = RequestMethod.GET)
    public Map<String, Double> getTxnSumWithChildren(@PathVariable long id) {
        logger.debug("getTxnSumWithChildren(): id={}", id);
        return Collections.singletonMap("sum", txnService.getTxnSumWithChildren(id));
    }
}
