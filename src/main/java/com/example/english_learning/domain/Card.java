package com.example.english_learning.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"answers"})
@Accessors(chain = true)
public class Card {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            strategy = "org.hibernate.id.UUIDGenerator", //TODO разобраться с хуйней
            name = "UUID"
    )
    private UUID id;
    private String question;
    private int showCount;
    private int correctAnswerCount;
    private LocalDateTime lastShownDateTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "card", cascade = CascadeType.REMOVE)
    private Set<Answer> answers = new HashSet<>();
    @Enumerated(value = EnumType.STRING)
    private CardType cardType;
    private String exampleOfUsage;
}