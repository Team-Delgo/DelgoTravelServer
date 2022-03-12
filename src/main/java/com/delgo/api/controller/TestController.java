package com.delgo.api.controller;

import com.delgo.api.domain.Place;
import com.delgo.api.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
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
}
