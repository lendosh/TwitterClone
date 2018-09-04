package com.vlad.TwiterClone.repos;

import com.vlad.TwiterClone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);


}
