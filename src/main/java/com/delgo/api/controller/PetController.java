package com.delgo.api.controller;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.dto.PetDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PetController {
    @Autowired
    private PetService petService;

}
