package com.sentineia.settings.general;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/general")
public class GeneralController {

    private final GeneralService generalService;

    public GeneralController(GeneralService generalService) {
        this.generalService = generalService;
    }

    @GetMapping
    public GeneralResponse get() {
        return generalService.getOrCreate();
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GeneralResponse patch(@Valid @RequestBody GeneralUpdateRequest body) {
        return generalService.update(body);
    }
}
