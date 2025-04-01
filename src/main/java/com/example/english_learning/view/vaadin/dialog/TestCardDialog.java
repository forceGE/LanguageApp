package com.example.english_learning.view.vaadin.dialog;

import com.example.english_learning.domain.Answer;
import com.example.english_learning.domain.Card;
import com.example.english_learning.service.CardService;
import com.example.english_learning.view.vaadin.LearningView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import java.time.LocalDateTime;

public class TestCardDialog extends AbstractCardDialog {
    private final RadioButtonGroup<String> testRadioGroup = new RadioButtonGroup<>("Choose the correct answer");

    public TestCardDialog(CardService cardService, LearningView learningView) {
        super(cardService, learningView);
        var mainLayout = new VerticalLayout();
        testRadioGroup.addValidationStatusChangeListener(event -> {
            if (isActive) {
                submit.focus();
            }
        });
        submit.addClickListener(event -> next.focus());
        mainLayout.add(testRadioGroup, wrong, success);
        add(mainLayout);
    }

    @Override
    void handleSubmit() {
        var answer = testRadioGroup.getValue();
        currentCard
                .setShowCount(currentCard.getShowCount() + 1)
                .setLastShownDateTime(LocalDateTime.now());
        // TODO необходимо добавить новый признак для карточек типа TEST и исправить валидацию ответа
        if (isValidTextAnswer(answer, currentCard.getAnswers()))  {
            correctAnswerAction();
            return;
        }
        wrong.setVisible(true);
        next.setVisible(true);
        submit.setVisible(false);
        testRadioGroup.setEnabled(false);
        testRadioGroup.clear();

        cardService.saveCard(currentCard);
    }

    @Override
    void handleNext() {
        close();
        learningView.showRandomCard();
    }

    @Override
    public void initiate(Card card) {
        super.initiate(card);
        setHeaderTitle("Question: " + currentCard.getQuestion());
        testRadioGroup.setItems(currentCard.getAnswers().stream()
                .map(Answer::getAnswerText)
                .toList());
    }
}