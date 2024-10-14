package com.Proyecto.Integrador.service;

import com.Proyecto.Integrador.dto.UserDto;
import com.Proyecto.Integrador.entity.UserMongoEntity;
import com.Proyecto.Integrador.exception.UserNotFoundException;
import com.Proyecto.Integrador.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceMap implements UserService{
    @Autowired
    private UserMongoRepository userMongoRepository;

    @Override
    public List<UserDto> all() {
        return this.userMongoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<UserDto> findById(String id) {
        UserMongoEntity entity = this.userMongoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return Optional.of(toDto(entity));
    }

    @Override
    public UserDto save(UserDto user) {
        UserMongoEntity entity = new UserMongoEntity();
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        UserMongoEntity entitySaved = this.userMongoRepository.save(entity);
        return this.toDto(entitySaved);
    }

    @Override
    public UserDto update(UserDto user, String id) {
        UserMongoEntity entity = this.userMongoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        UserMongoEntity entitySaved = this.userMongoRepository.save(entity);
        return this.toDto(entitySaved);
    }

    @Override
    public void deleteById(String id) {
        UserMongoEntity entity = this.userMongoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        this.userMongoRepository.delete(entity);
    }

    private UserDto toDto(UserMongoEntity entity) {
        return new UserDto(entity.getId(), entity.getName(), entity.getEmail());
    }
}
