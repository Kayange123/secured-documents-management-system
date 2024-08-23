package dev.kayange.sdms.dao;

import dev.kayange.sdms.entity.Confirmation;
import dev.kayange.sdms.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Optional<Confirmation> findByKey(String key);
    Optional<Confirmation> findByUser(UserEntity user);
}
