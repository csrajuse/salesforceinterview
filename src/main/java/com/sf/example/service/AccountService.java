package com.sf.example.service;

import com.sf.example.entity.Account;
import com.sf.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service class
 * Interacting with the data layer and push and pull Account data.
 */
//TODO: Hookup the database layer.
@Component
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAccounts(){return accountRepository.findAll();}

    public Account saveAccount(Account account){return accountRepository.save(account);}

    public Account upsertAccount(Account account){
        //find if account has an id and if the id exists in the database.
        return null;
    }
}
