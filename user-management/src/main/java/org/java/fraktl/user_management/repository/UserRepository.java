package org.java.fraktl.user_management.repository;

import org.java.fraktl.user_management.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
