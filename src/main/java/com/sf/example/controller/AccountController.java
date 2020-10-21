package com.sf.example.controller;

import com.sf.example.entity.Account;
import com.sf.example.service.AccountService;
import com.sf.example.util.AccountConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This controller is used by the web application.
 */

@RestController
@RequestMapping("/api/")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger("AccountController");
    @Autowired
    private AccountService accountService;

    @GetMapping(path = AccountConstants.LIST_ACCOUNTS)
    public ResponseEntity<?> listAccounts(){
        log.info("Listing accounts.");
        List<Account> resource = accountService.getAccounts();
        return ResponseEntity.ok(resource);
    }

    @PostMapping(path = AccountConstants.ADD_ACCOUNT)
    public ResponseEntity<?> saveAccount(@RequestBody Account account){
        log.info("Adding account with account name:"+account.getAccountNumber());
        Account resource =accountService.saveAccount(account);
        return ResponseEntity.ok(resource);
    }

    @PutMapping(path = AccountConstants.UPSERT_ACCOUNT)
    public ResponseEntity<?> upsertAccount(@RequestBody Account account){
        return null;
    }
}
