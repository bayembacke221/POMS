package sn.bmbacke.repository;

import sn.bmbacke.models.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByLogin(String login);
    Optional<User> findByKcId(String kcId);
    Optional<User> findByEmail(String email);
    boolean existsByLogin(String login);
}
