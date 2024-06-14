package com.bookstore.controller;

import com.bookstore.entity.User;
import com.bookstore.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Controller
public class UserController {
    @Autowired
    private UserServices userServices;

    @GetMapping("/login")
    public String login(){
        return "user/login";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "user/register";
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect error messages
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        try {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userServices.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký người dùng");
        }

        // Successful registration and redirect
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/login"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found for redirection
    }
}
