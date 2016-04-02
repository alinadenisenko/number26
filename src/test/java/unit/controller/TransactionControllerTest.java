package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.number26.Application;
import com.number26.model.Transaction;
import com.number26.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private TransactionService txnService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(wac).build();

        txnService.putTxn(getTestTxn(10, 1000, "cars", null));
        txnService.putTxn(getTestTxn(11, 500, "grocery", 10l));
        txnService.putTxn(getTestTxn(12, 10000, "cars", 11l));
    }

    @Test
    public void getTxnSuccess() throws Exception {
        mockMvc.perform(get("/transactionservice/transaction/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.amount", is(1000.0)));
    }

    @Test
    public void getTxnNotFound() throws Exception {
        mockMvc.perform(get("/transactionservice/transaction/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putTxnSuccess() throws Exception {
        Transaction txn = getTestTxn(13, 12000, "transfer", null);
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/transactionservice/transaction/" + txn.getId())
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(txn)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.status", is("ok")));
    }

    @Test
    public void putTxnBadDataError() throws Exception {
        Transaction txn = getTestTxn(14, 12000, "transfer", 400l);
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(put("/transactionservice/transaction/" + txn.getId(), txn)
                .contentType(contentType)
                .content(mapper.writeValueAsString(txn)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putTxnAlreadyExistsError() throws Exception {
        Transaction txn = getTestTxn(10, 12000, "transfer", 400l);
        mockMvc.perform(put("/transactionservice/transaction/" + txn.getId(), txn))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTxnsByTypeSuccess() throws Exception {
        mockMvc.perform(get("/transactionservice/types/cars"))
                .andExpect(status().isOk())
                .andExpect(content().string("[10,12]"));
    }

    @Test
    public void getTxnsByTypeEmpty() throws Exception {
        mockMvc.perform(get("/transactionservice/types/notvalidtype"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getTxnSumNoChildren() throws Exception {
        mockMvc.perform(get("/transactionservice/sum/12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.sum", is(10000.0)));
    }

    @Test
    public void getTxnSumWithChildren() throws Exception {
        mockMvc.perform(get("/transactionservice/sum/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.sum", is(11500.0)));
    }

    @Test
    public void getTxnSumTxnNotFoundError() throws Exception {
        mockMvc.perform(get("/transactionservice/sum/100"))
                .andExpect(status().isNotFound());
    }

    private Transaction getTestTxn(long id, double amount, String type, Long parentId) {
        Transaction txn = new Transaction();
        txn.setId(id);
        txn.setAmount(amount);
        txn.setType(type);
        txn.setParentId(parentId);

        return txn;
    }
}
