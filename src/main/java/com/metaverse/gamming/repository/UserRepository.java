package com.metaverse.gamming.repository;

import com.metaverse.gamming.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

  boolean existsByUsername(String username);

  List<User> findAll();

  User findByUsername(String username);

  @Transactional
  void deleteByUsername(String username);

}
