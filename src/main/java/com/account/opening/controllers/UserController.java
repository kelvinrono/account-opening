package com.account.opening.controllers;

import com.account.opening.objects.UserObject;
import com.account.opening.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/account")
public class UserController {
    private  final UserService userService;

    @PostMapping("/create-account")
    public HashMap createAccount(@RequestBody HashMap params){
        return userService.validateUser(params);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/debit-account/{id}")
    public HashMap debitAccount(@RequestBody UserObject userObject, @PathVariable long id){
        return userService.debitAccount(id, userObject);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/credit-account/{id}")
    public HashMap creditAccount(@RequestBody UserObject userObject,@PathVariable long id){
        return userService.creditAccount(id, userObject);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get-user/{id}")
    public HashMap getSpecificUserAccount(@PathVariable long id){
        return userService.getSpecificUserAccount(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/update-details/{id}")
    public HashMap updateDetails(@PathVariable long id, @RequestBody UserObject userObject){
        return userService.updateDetails(id, userObject);
    }

    @PostMapping("/login-user")
    public HashMap loginUser(@RequestBody HashMap params){
        return userService.loginUser(params);
    }


}
