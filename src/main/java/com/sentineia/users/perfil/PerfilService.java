package com.sentineia.users.perfil;

import com.sentineia.base.BaseService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilService extends BaseService<Perfil> {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository repository) {
        super(repository);
        this.perfilRepository = repository;
    }

    @Override
    @Transactional
    public Perfil save(Perfil perfil) {
        String name = normalizeName(perfil.getName());
        perfil.setName(name);
        if (perfil.getId() == null) {
            if (perfilRepository.existsByName(name)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um perfil com este nome.");
            }
        } else if (perfilRepository.existsByNameAndIdNot(name, perfil.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um perfil com este nome.");
        }
        return super.save(perfil);
    }

    private static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
