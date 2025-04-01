package com.example.english_learning.view.vaadin.dialog;

import com.example.english_learning.domain.Answer;
import com.example.english_learning.domain.Card;
import com.example.english_learning.service.CardService;
import com.example.english_learning.utils.CommonUtils;
import com.example.english_learning.view.vaadin.LearningView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LanguageCardDialog extends AbstractCardDialog {
    private final TextField answerField = new TextField("Your answer");
    private final TextField example = new TextField("Example of usage");
    private final TextField correctAnswer = new TextField("Correct answer(s)");// TODO стоит вынести в AbstractCardDialog
    private final Button edit = new Button("Edit card", event -> handleUpdateCard());
    private final UpdateCardDialog updateCardDialog;
    private boolean isFirstAnswer;

    public LanguageCardDialog(CardService cardService, LearningView learningView) {
        super(cardService, learningView);
        var mainLayout = new VerticalLayout();
        this.updateCardDialog = new UpdateCardDialog(cardService);
        mainLayout.add(answerField, success, correctAnswer, example, wrong);
        add(mainLayout);
        example.setWidthFull();
        example.setReadOnly(true);
        example.setVisible(false);
        correctAnswer.setReadOnly(true);
        correctAnswer.setVisible(false);
        correctAnswer.setWidthFull();
        answerField.setWidthFull();
        edit.setVisible(false);
        answerField.addKeyPressListener(Key.ENTER, event -> {
           if (isActive) {
               submit.click();
               next.focus();
           }
        });
        var footer = getFooter();
        footer.removeAll();
        footer.add(close, delete, edit, submit, next);

        addDialogCloseActionListener(event -> {
            edit.setVisible(false);
            next.setVisible(false);
            correctAnswer.setVisible(false);
            example.setVisible(false);
            wrong.setVisible(false);
            success.setVisible(false);
            event.getSource().close();
        });
    }

    public void handleSubmit() {
        answerField.setReadOnly(true);
        var answer = answerField.getValue();
        currentCard
                .setShowCount(currentCard.getShowCount() + 1)
                .setLastShownDateTime(LocalDateTime.now());

        if (isValidLanguageAnswer(answer, currentCard))  {
            if (!example.getValue().equals("")) {
                example.setVisible(true);
            }
            correctAnswer.setVisible(true);
            edit.setVisible(true);
            correctAnswerAction();
            return;
        }
        wrong.setVisible(true);
        next.setVisible(true);
        submit.setVisible(false);
        answerField.setEnabled(false);
        if (!example.getValue().equals("")) {
            example.setVisible(true);
        }
        correctAnswer.setVisible(true);
        edit.setVisible(true);

        cardService.saveCard(currentCard);
    }

    @Override
    void handleNext() {
        close();
        correctAnswer.clear();
        correctAnswer.setVisible(false);
        example.clear();
        example.setVisible(false);
        answerField.clear();
        learningView.showRandomCard();
        edit.setVisible(false);
    }

    private void handleUpdateCard() {
        updateCardDialog.initiate(currentCard);
        updateCardDialog.open();
    }

    @Override
    public void initiate(Card card) {
        super.initiate(card);
        if (card.getExampleOfUsage() != null) {
            example.setValue(card.getExampleOfUsage());
        }
        answerField.setReadOnly(false);
        answerField.clear();
        answerField.setEnabled(true);
        answerField.focus();
        isFirstAnswer = randomBoolean();
        var question = isFirstAnswer ? randomAnswer() : currentCard.getQuestion();
        var correct = isFirstAnswer ? currentCard.getQuestion() : allAnswers();
        correctAnswer.setValue(correct == null ? "NPE" : correct);
        setHeaderTitle("Question: " + question);
    }


    private boolean isValidLanguageAnswer(String answer, Card card) {
        if (isFirstAnswer) return CommonUtils.equalsByLevenshtein(answer, card.getQuestion());
        return isValidTextAnswer(answer, card.getAnswers());
    }

    private String allAnswers() {
        return currentCard.getAnswers().stream()
                .map(Answer::getAnswerText)
                .reduce((x, y) -> x + ", " + y)
                .orElse(null);
    }

    private String randomAnswer() {
        var answersList = new ArrayList<>(
                currentCard.getAnswers().stream()
                        .map(Answer::getAnswerText)
                        .toList()
        );
        Collections.shuffle(answersList);
        return answersList.get(0);
    }

    private boolean randomBoolean() {
        return new Random().nextBoolean();
    }
}