package com.example.english_learning.repo;

import com.example.english_learning.domain.Card;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepo extends JpaRepository<Card, UUID> {
    @Query(value = """
            SELECT c FROM Card c WHERE c.correctAnswerCount < :threshold
            ORDER BY FUNCTION('RANDOM') / (c.correctAnswerCount + 1) DESC""")
    List<Card> findRandomCardWithWeightedChance(int threshold, Pageable pageable);

    @Query(value = "SELECT count(c) FROM Card c WHERE c.correctAnswerCount < :count")
    long countActualCard(int count);

    boolean existsCardByQuestion(String question);
}