package com.example.english_learning.repo;

import com.example.english_learning.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, UUID> {
}
