package com.example.account;

import com.example.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.UUID;

@NodeEntity
public class Account {

    @GraphId
    private Long id;

    @JsonIgnore
    @Relationship(type = "HAS_ACCOUNT", direction = "INCOMING")
    private User user;
    private String accountNumber;
    private AccountStatus status;

    public Account() {
        accountNumber = UUID.randomUUID().toString();
    }

    public Account(User user) {
        this();
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", user=" + user +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
