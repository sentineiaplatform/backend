package com.sentineia.users.perfil;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRepository extends BaseRepository<Perfil> {

    Optional<Perfil> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
