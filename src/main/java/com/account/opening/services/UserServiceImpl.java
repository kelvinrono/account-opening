package com.account.opening.services;

import com.account.opening.AccountOpeningApplication;
import com.account.opening.Role;
import com.account.opening.configs.JwtService;
import com.account.opening.models.User;
import com.account.opening.objects.UserObject;
import com.account.opening.repositories.UserRepository;
import com.smattme.requestvalidator.RequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public HashMap openAccount(HashMap params) {
        HashMap<String, Object> response=new HashMap<>();
        try {
            Optional<User> existingUser = userRepository.findByEmail(params.get("email").toString());
            if (existingUser.isPresent()){
                response.put("message", "user already exist");
                response.put("status", false);
            }
            else {
                String encodedPassword  = passwordEncoder.encode(params.get("password").toString());
                User newUser = User.builder()
                        .email(params.get("email").toString().toLowerCase())
                        .firstName(params.get("firstName").toString())
                        .lastName(params.get("lastName").toString())
                        .phoneNumber(params.get("phoneNumber").toString())
                        .password(encodedPassword)
                        .account(BigDecimal.valueOf(0))
                        .role(Role.USER)
                        .build();
                userRepository.save(newUser);
                var jwtToken = jwtService.generateToken(newUser);
                response.put("message", "Account created successfully for "+newUser.getFirstName());
                response.put("status", true);
                response.put("token", jwtToken);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            log.error("error::::"+e.getMessage());
            response.put("message", "Ooops! Something went wrong");
            response.put("status", false);
        }
        return response;
    }

    @Override
    public HashMap getSpecificUserAccount(long id) {
        HashMap<String, Object> response = new HashMap();
        try {
            User user =  userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("User with the given id does not exist"));
             log.info("....user information retrieved successfully....");
             response.put("message", "User data retrieved");
             response.put("status", true);
             response.put("data", user);
        }catch (Exception e){
            e.printStackTrace();
            log.error("error::::"+e.getMessage());
            response.put("message", "Ooops! Something went wrong");
            response.put("status", false);
        }
        return response;
    }

    @Override
    public HashMap debitAccount(long id, UserObject userObject) {
         HashMap<String, Object> response = new HashMap();
        try {
            User user = userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("User with that id does not exist"));
            if(((user.getAccount()).compareTo(userObject.getAccount()) )<0){
                log.info("....Debit fail....");
                response.put("message", "Insufficient funds! Available balance "+ user.getAccount());
                response.put("status", false);
            }
            else {
                user.setAccount(user.getAccount().subtract(userObject.getAccount()));
                userRepository.save(user);
                log.info("debit successful...");
                response.put("message", userObject.getAccount()+" has been withdrawn successfully. ");
                response.put("status", true);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            log.error("error::::"+e.getMessage());
            response.put("message", "Ooops! Something went wrong");
            response.put("status", false);
        }
        return response;
    }

    @Override
    public HashMap creditAccount(long id, UserObject userObject) {
        HashMap<String, Object> response = new HashMap();
        try {
            User user = userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("User with that id does not exist"));
            user.setAccount(user.getAccount().add(userObject.getAccount()));
            userRepository.save(user);
            response.put("message", userObject.getAccount()+" has been credited successfully. ");
            response.put("status", true);
        }
        catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            response.put("message", "Oops! An error occurred!");
            response.put("status", false);
            return response;
        }
        return response;
    }


    @Override
    public HashMap updateDetails(long id, UserObject userObject) {
         HashMap<String, Object> response = new HashMap();

        try {
            User existingUser = userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("User with the id provided does not exist"));
            if(userObject.getFirstName() !=null){
                existingUser.setFirstName(userObject.getFirstName());
            }
            if(userObject.getEmail() !=null){
                existingUser.setEmail(userObject.getEmail().toLowerCase());
            }
            if(userObject.getPassword() !=null){
                existingUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
            }
            if (userObject.getPhoneNumber()!=null){
                existingUser.setPhoneNumber(userObject.getPhoneNumber());
            }
            userRepository.save(existingUser);
            response.put("message", "information updated successfully");
            response.put("status", true);
        }
        catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            response.put("message", "Oops! An error occurred!");
            response.put("status", false);
            return response;
        }
        return response;
    }

    @Override
    public HashMap loginUser(HashMap params) {
        HashMap<String, Object> response = new HashMap();
        try {
            Optional<User> user = userRepository.findByEmail(params.get("email").toString().toLowerCase());
            if(user.isEmpty()){
                response.put("message", "User does not exist");
                response.put("status", false);
                log.info("...User not found....");
            }
            else if(!passwordEncoder.matches(params.get("password").toString(), user.get().getPassword())){
                response.put("message", "invalid credentials");
                response.put("status", "false");
                log.info("....invalid credentials...");
            }
            else {
                var jwtToken = jwtService.generateToken(user.get());
                response.put("message", "login successful");
                response.put("status", true);
                response.put("token", jwtToken);
                log.info("...Login successful....");
            }

        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            response.put("message", "Oops! An error occurred!");
            response.put("status", false);
            return response;
        }
        return response;
    }

    public HashMap validateUser(HashMap params) {

        HashMap<String, Object> response = new HashMap<>();

        HashMap<String, String> rules = new HashMap<>();

        try {
            rules.put("firstName", "required");
            rules.put("lastName", "required");
            rules.put("email", "required");
            rules.put("phoneNumber", "required");
            rules.put("password", "required");
            List<String> errors = RequestValidator.validate(params, rules);
            if (!errors.isEmpty()) {
                log.info("An error occurred...");
                response.put("message", "All fields are required");
                response.put("errors", errors);
                response.put("status", false);
                return response;
            } else {
                log.info("Parameters validated");
                return openAccount(params);
            }

        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            response.put("message", "Oops! An error occurred!");
            response.put("status", false);
            return response;
        }
    }


}
