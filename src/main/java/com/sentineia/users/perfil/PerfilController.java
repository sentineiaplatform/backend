package com.sentineia.users.perfil;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfis")
public class PerfilController extends BaseController<Perfil, PerfilService> {

    public PerfilController(PerfilService service) {
        super(service);
    }

    @PostMapping
    @Override
    public ResponseEntity<Perfil> create(@Valid @RequestBody Perfil entity) {
        return super.create(entity);
    }
}
