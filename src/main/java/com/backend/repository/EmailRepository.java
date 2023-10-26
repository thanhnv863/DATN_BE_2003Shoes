package com.backend.repository;

import com.backend.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailTemplate,Long> {
}
