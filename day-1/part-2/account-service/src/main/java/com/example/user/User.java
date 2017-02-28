package com.example.user;

import com.example.account.Account;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

@NodeEntity
public class User {

    @GraphId
    private Long id;

    @Transient
    private UserStatus status;

    @Index(unique = true, primary = true)
    private Long userId;

    @Relationship(type = "HAS_ACCOUNT")
    private Set<Account> accounts;

    public User() {
    }

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", status=" + status +
                ", userId=" + userId +
                ", accounts=" + accounts +
                '}';
    }
}
