package com.delgo.api.controller;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.user.User;
import com.delgo.api.repository.UserRepository;
import com.delgo.api.security.services.PrincipalDetails;
import com.delgo.api.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class TestController {

    private final TestService testService;

    private final UserRepository userRepository;

    @Autowired
    public TestController(TestService testService, UserRepository userRepository) {
        this.testService = testService;
        this.userRepository = userRepository;
    }


    @GetMapping("/api/home")
    public String home() {
        return "<h1>home</h1>";
    }

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("/login")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal : "+principal.getUser().getUser_id());
        System.out.println("principal : "+principal.getUser().getEmail());
        System.out.println("principal : "+principal.getUser().getPassword());

        return "<h1>user</h1>";
    }

    @GetMapping("/")
    @ResponseBody
    public Place select() {
        System.out.println("들어옴");
        Optional<Place> result = testService.getPlaceData();
        Place place = result.get();
        System.out.println("test 성공,"+place.getName());

        return place;
    }

    @GetMapping("/test")
    @ResponseBody
    public User test(@RequestParam String email) {
        System.out.println("test 들어옴,"+email);
        User user = userRepository.findByEmail(email);
        return user;
    }
}
