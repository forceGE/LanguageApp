package com.example.english_learning.service;

import com.example.english_learning.domain.Answer;
import com.example.english_learning.domain.Card;
import com.example.english_learning.domain.CardType;
import com.example.english_learning.repo.AnswerRepo;
import com.example.english_learning.repo.CardRepo;
import com.nimbusds.jose.util.Pair;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class CardService {
    @Value("${spring.application.technical_values.max_correct_answer_for_show}")
    private int maxCorrectAnswerForShow;
    private final CardRepo cardRepo;
    private final AnswerRepo answerRepo;
    @PersistenceContext
    private EntityManager entityManager;

    public Card getRandomCard() {
        List<Card> cards = cardRepo.findRandomCardWithWeightedChance(
                maxCorrectAnswerForShow,
                PageRequest.of(0, 1)
        );
        if (isEmpty(cards)) return null;
        return cards.get(0);
    }

    public boolean existsByQuestion(String question) {
        return cardRepo.existsCardByQuestion(question);
    }

    @Transactional
    public Card saveCard(Card card, Collection<String> newAnswerSet) {
        var updatedCard = cardRepo.save(card);

        var currentAnswers = updatedCard.getAnswers();
        if (currentAnswers != null && !currentAnswers.isEmpty()) {
            answerRepo.deleteAll(currentAnswers);
            currentAnswers.clear();
        }

        var newAnswers = newAnswerSet.stream()
                .map(answerText -> new Answer()
                        .setCard(card)
                        .setAnswerText(answerText))
                .collect(Collectors.toSet());

        var savedAnswers = answerRepo.saveAll(newAnswers);

        card.setAnswers(new HashSet<>(savedAnswers));
        return cardRepo.save(card);
    }

    public void saveCard(Card card) {
        cardRepo.save(card);
    }

    public void saveNewCard(String question, Set<String> answer, CardType type, String example) {
        var card = new Card()
                .setQuestion(question)
                .setCardType(type)
                .setExampleOfUsage(example);

        cardRepo.save(card);

        var answers = answer.stream()
                .map(a -> new Answer()
                        .setAnswerText(a.trim())
                        .setCard(card)
                )
                .collect(Collectors.toSet());

        card.getAnswers().addAll(answers);

        answerRepo.saveAll(answers);
    }

    public void deleteById(UUID id) {
        cardRepo.deleteById(id);
    }

    public long countAll() {
        return cardRepo.count();
    }

    public long countActualCard() {
        return cardRepo.countActualCard(maxCorrectAnswerForShow );
    }

//    @PostConstruct
    public void parsWords() throws IOException {
        var content = new String(
                Files.readAllBytes(
                        Paths.get(
                                "/Users/sheykinsemyon/IdeaProjects/english_lerning/src/main/resources/words.txt"
                        )
                )
        );
        var lines = content.split("\n");

        Arrays.stream(lines).forEach(x -> {
            var arr = x.split(":");
            var question = arr[0].trim();
            var answers = Arrays.stream(arr[1].split(",")).map(String::trim).collect(Collectors.toSet());
            var example = arr[2].trim();

            try {
                saveNewCard(question, answers, CardType.LANGUAGE, example);
            } catch (Exception ex) {
                log.error("error: {} by question {}", ex.getClass(), question);
            }
        });
    }
}
