package com.example.user;

import com.example.account.Account;
import com.example.account.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private AccountService accountService;

    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{userId}/accounts")
    public Set<Account> getUserAccounts(@PathVariable Long userId) {
        return accountService.getAccounts(userId);
    }

    @PostMapping("/{userId}/accounts")
    public Account createAccount(@PathVariable Long userId, @RequestBody Account account) {
        return accountService.createAccount(userId, account);
    }
}
