package com.example.account;

import com.example.user.User;
import com.example.user.UserRepository;
import com.example.user.UserService;
import com.example.user.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

@Service
@Transactional
public class AccountService {

    private AccountRepository accountRepository;
    private UserService userService;
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserService userService, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Account createAccount(Long userId, Account account) {
        // Call the user microservice
        User user = userService.getUser(userId);

        Assert.notNull(user, "The user with the ID could not be found");

        // The user must not be inactive
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Cannot create a new account for an inactive user");
        }

        // Set the user relationship on the new account object
        account.setUser(new User(userId));

        return accountRepository.save(account);
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }

    public Set<Account> getAccounts(Long userId) {
        User user = userRepository.getUserByUserId(userId);

        if(user == null) {
            return Collections.emptySet();
        }

        return user.getAccounts();
    }
}
