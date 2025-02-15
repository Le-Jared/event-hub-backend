package com.fdmgroup.backend_eventhub.authenticate.service;

import com.fdmgroup.backend_eventhub.authenticate.dto.LoginRequest;
import com.fdmgroup.backend_eventhub.authenticate.dto.RegistrationRequest;
import com.fdmgroup.backend_eventhub.authenticate.exceptions.*;
import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private static final Logger accountServiceLogger = LogManager.getLogger(AccountService.class);

  @Autowired private AccountRepository accountRepository;
  private final BCryptPasswordEncoder oldEncoder = new BCryptPasswordEncoder(16);
  private final BCryptPasswordEncoder newEncoder = new BCryptPasswordEncoder(10);

  public AccountService() {
    this.accountRepository = accountRepository;
  }

  public Account updateAccount(Account accountToUpdate) {
    return accountRepository
        .findById(accountToUpdate.getId())
        .map(
            existingAccount -> {
              existingAccount.setUsername(accountToUpdate.getUsername());
              existingAccount.setEmail(accountToUpdate.getEmail());
              existingAccount.setPassword(newEncoder.encode(accountToUpdate.getPassword()));
              return accountRepository.save(existingAccount);
            })
        .orElse(null); // Account not found
  }

  public boolean deleteAccount(long accountId) {
    return accountRepository
        .findById(accountId)
        .map(
            account -> {
              accountRepository.delete(account);
              return true; // Account deleted successfully
            })
        .orElse(false); // Account not found
  }

  public Account loginUser(LoginRequest loginRequest)
      throws UsernameNotFoundException, IncorrectPasswordException {
    accountServiceLogger.info("Login attempt | {}", loginRequest.toString());

    // Retrieve an account by username.
    Optional<Account> accountOptional =
        accountRepository.findByUsername(loginRequest.getUsername());

    // Unsuccessful login due to incorrect username.
    if (accountOptional.isEmpty()) {
      accountServiceLogger.error("Unsuccessful login as username entered not found.");
      throw new UsernameNotFoundException();
    }

    Account account = accountOptional.get();

    boolean passwordMatches =
        newEncoder.matches(loginRequest.getPassword(), account.getPassword())
            || oldEncoder.matches(loginRequest.getPassword(), account.getPassword());

    // Unsuccessful login due to incorrect password.
    if (!passwordMatches) {
      accountServiceLogger.error("Unsuccessful login due to incorrect password.");
      throw new IncorrectPasswordException();
    }
    accountServiceLogger.info("Successful login | {}", loginRequest.toString());
    return account;
  }

  public Account registerUser(RegistrationRequest registrationRequest)
      throws InvalidUsernameException,
          InvalidEmailAddressException,
          InvalidPasswordException,
          UnavailableUsernameException,
          UnavailableEmailAddressException {
    accountServiceLogger.info("Registration attempt | {}", registrationRequest.toString());

    String username = registrationRequest.getUsername();
    String email = registrationRequest.getEmail();
    String password = newEncoder.encode(registrationRequest.getPassword());

    if (!isValidUsername(username)) {
      throw new InvalidUsernameException();
    }

    if (!isValidEmailAddress(email)) {
      throw new InvalidEmailAddressException();
    }

    if (!isValidPassword(password)) {
      throw new InvalidPasswordException();
    }

    if (!isUsernameAvailable(username)) {
      throw new UnavailableUsernameException();
    }

    if (!isEmailAddressAvailable(email)) {
      throw new UnavailableEmailAddressException();
    }

    Account account = new Account(username, email, password);
    accountRepository.save(account);
    accountServiceLogger.info("Successful registration | {}", account.toString());
    return account;
  }

  // ping user checks that a user exists and returns the user, or null if user does not exist.
  public Account pingUser(RegistrationRequest registrationRequest) {
    accountServiceLogger.info("Pinging user | {}", registrationRequest.toString());

    Optional<Account> accountOptional =
        accountRepository.findByUsername(registrationRequest.getUsername());

    if (accountOptional.isPresent()) {
      accountServiceLogger.info("User {} found.", registrationRequest.getUsername());
      return accountOptional.get();
    }
    accountServiceLogger.info("User {} not found.", registrationRequest.getUsername());
    return null;
  }

  private boolean isEmailAddressAvailable(String email) {
    return accountRepository.findByEmail(email).isEmpty();
  }

  private boolean isUsernameAvailable(String username) {
    return accountRepository.findByUsername(username).isEmpty();
  }

  private boolean isValidPassword(String password) {
    if (password.length() < 8) {
      return false;
    }

    int numberOfUppercaseLetters = 0;
    int numberOfNumbers = 0;
    int numberOfSpecialCharacters = 0;
    for (int i = 0; i < password.length(); i++) {
      char character = password.charAt(i);
      if (Character.isUpperCase(character)) {
        numberOfUppercaseLetters++;
      }
      if (Character.isDigit(character)) {
        numberOfNumbers++;
      }
      if (!Character.isLetterOrDigit(character)) {
        numberOfSpecialCharacters++;
      }
    }

    return numberOfUppercaseLetters >= 1 && numberOfNumbers >= 1 && numberOfSpecialCharacters >= 1;
  }

  private boolean isValidEmailAddress(String email) {
    if (email == null) {
      return false;
    }
    String trimmedEmail = email.trim();
    // Regex pattern for basic email validation
    return trimmedEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  }

  private boolean isValidUsername(String username) {
    String[] word = username.split(" ");
    if (word.length != 1) {
      return false;
    }

    int numberOfCharacters = 0;
    for (int i = 0; i < username.length(); i++) {
      numberOfCharacters++;
    }

    return numberOfCharacters >= 5;
  }
}
