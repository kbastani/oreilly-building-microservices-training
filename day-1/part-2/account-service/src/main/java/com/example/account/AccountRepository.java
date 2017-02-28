package com.example.account;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface AccountRepository extends GraphRepository<Account> {

    Account getAccountByAccountNumber(String accountNumber);
}
