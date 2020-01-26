package com.frizo.demo.polling.dao;

import com.frizo.demo.polling.entity.Role;
import com.frizo.demo.polling.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
