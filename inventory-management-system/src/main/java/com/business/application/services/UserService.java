package com.business.application.services;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import com.business.application.domain.*;
import com.business.application.repository.*;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

@Service
public class UserService {

     private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }


    public int count() {
        return (int) repository.count();
    }

    public Optional<User> validateUserDetails(String name, String username) {
        return repository.findByNameAndUsername(name, username);
    }

    public boolean resetPassword(User user, String newPassword) {
        user.setHashedPassword(new BCryptPasswordEncoder().encode(newPassword));
        repository.save(user);
        return true;
    }
    public List<User> findUnassignedManagers() {

        
        return repository.findAllByRoleAndStoreIsNull(Role.USER);
    }
    

}
