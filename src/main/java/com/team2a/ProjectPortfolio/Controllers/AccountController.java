package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.ACCOUNT)
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructor for the Account Controller
     * @param accountService - the Account Service
     */
    @Autowired
    public AccountController (AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates an Account in the database
     * @param account - the Account to be created
     * @return - the Account that was created
     */
    @PostMapping("/")
    public ResponseEntity<Account> createAccount (@RequestBody Account account) {
        try {
            return ResponseEntity.ok(accountService.createAccount(account));
        }
        catch(FieldNullException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch(DuplicatedUsernameException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Edits an already existing Account
     * @param account - the Account to be modified
     * @return - the Account with the necessary modifications
     */
    @PutMapping("/")
    public ResponseEntity<Account> editAccount (@RequestBody Account account) {
        try {
            return ResponseEntity.ok(accountService.editAccount(account));
        }
        catch(FieldNullException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Gets an Account from the database based on id
     * @param username - the id of the Account to be searched
     * @return - the Account with the given id, provided it exists
     */
    @GetMapping("/{username}")
    public ResponseEntity<Account> getAccountById (@PathVariable("username") String username) {
        try {
            return ResponseEntity.ok(accountService.getAccountById(username));
        }
        catch(IdIsNullException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes an Account from the database
     * @param username - the id of the Account to be deleted
     * @return - the status of the deletion
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteAccount (@PathVariable("username") String username) {
        try {
            accountService.deleteAccount(username);
            return ResponseEntity.status(HttpStatus.OK).body("Success.");
        }
        catch(IdIsNullException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}