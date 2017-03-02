package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Stream;

@EnableResourceServer
@SpringBootApplication
public class AuthServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	private final AccountRepository accountRepository;

	AuthServiceApplication(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of("jlong,spring", "kbastani,data", "dsyer,cloud", "pwebb,twitter")
				.map(s -> s.split(","))
				.forEach(tpl -> accountRepository.save(new Account(tpl[0], tpl[1], true)));
	}
}

@RestController
class UserRestController {

	@RequestMapping("/user")
	Principal principal(Principal principal) {
		return principal;
	}
}

@Configuration
@EnableAuthorizationServer
class AuthConfiguration extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;

	public AuthConfiguration(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients
				.inMemory()
				.withClient("html5")
				.secret("secret")
				.scopes("openid")
				.authorizedGrantTypes("password");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}
}


@Service
class AccountUserDetailsService implements UserDetailsService {

	private final AccountRepository accountRepository;

	public AccountUserDetailsService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return accountRepository.findByUsername(username)
				.map(account -> new User(username, account.getPassword(),
						account.isEnabled(), account.isEnabled(), account.isEnabled(), account.isEnabled(),
						AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")))
				.orElseThrow(() -> new UsernameNotFoundException("user " + username + " not found!"));
	}
}

interface AccountRepository extends JpaRepository<Account, Long> {

	// select * from accounts where username = :username
	Optional<Account> findByUsername(String username);

}

@Entity
class Account {

	private String username, password;
	private boolean enabled;

	@Id
	@GeneratedValue
	private Long id;

	Account() {// why JPA why?
	}

	public Account(String username, String password, boolean enabled) {

		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "Account{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", enabled=" + enabled +
				", id=" + id +
				'}';
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Long getId() {
		return id;
	}
}