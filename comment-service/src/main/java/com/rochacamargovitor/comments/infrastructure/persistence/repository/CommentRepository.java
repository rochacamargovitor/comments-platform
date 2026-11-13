package com.rochacamargovitor.comments.infrastructure.persistence.repository;

import com.rochacamargovitor.comments.infrastructure.persistence.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    Page<CommentEntity> findAll(Pageable pageable);

}
