package com.business.application.repository;


import java.util.Optional;

//import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;



import com.business.application.domain.Role;
import com.business.application.domain.User;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);
    Optional<User> findByNameAndUsername(String name, String username);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles AND u.id NOT IN (SELECT s.managerId FROM Store s)")
    List<User> findAllByRoleAndStoreIsNull(@Param("role") Role role);
}
