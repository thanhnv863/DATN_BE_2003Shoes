package com.backend.repository;

import com.backend.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmailRepository extends JpaRepository<EmailTemplate, Long> {
    @Query(value = "SELECT * FROM email_template where email_template.mail_type = ?1", nativeQuery = true)
    Optional<EmailTemplate> checkSendMail(Integer type);
}
