package com.example.english_learning.view.vaadin.dialog;

import com.example.english_learning.domain.Card;
import com.example.english_learning.service.CardService;
import com.example.english_learning.view.vaadin.LearningView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDateTime;

public class TextCardDialog extends AbstractCardDialog {
    private final TextField answerField = new TextField("Your answer");

    public TextCardDialog(CardService cardService, LearningView learningView) {
        super(cardService, learningView);
        var mainLayout = new VerticalLayout();
        mainLayout.add(answerField, success, wrong);
        add(mainLayout);
        answerField.setWidthFull();
        answerField.addKeyPressListener(Key.ENTER, event -> {
            if (isActive) {
                submit.click();
                next.focus();
            }
        });
    }

    @Override
    void handleSubmit() {
        currentCard
                .setShowCount(currentCard.getShowCount() + 1)
                .setLastShownDateTime(LocalDateTime.now());

        if (isValidTextAnswer(answerField.getValue(), currentCard.getAnswers()))  {
            correctAnswerAction();
            return;
        }
        wrong.setVisible(true);
        next.setVisible(true);
        submit.setVisible(false);
        answerField.setEnabled(false);

        cardService.saveCard(currentCard);
    }

    @Override
    void handleNext() {
        close();
        answerField.clear();
        learningView.showRandomCard();
    }

    @Override
    public void initiate(Card card) {
        super.initiate(card);
        answerField.clear();
        answerField.setEnabled(true);
        answerField.focus();
        setHeaderTitle("Question: " + currentCard.getQuestion());
    }
}
