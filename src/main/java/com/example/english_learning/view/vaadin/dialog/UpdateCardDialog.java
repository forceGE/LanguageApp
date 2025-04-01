package com.example.english_learning.view.vaadin.dialog;

import com.example.english_learning.domain.Card;
import com.example.english_learning.service.CardService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.english_learning.view.vaadin.notification.CommonNotificationUtils.incorrectCard;
import static com.example.english_learning.view.vaadin.notification.CommonNotificationUtils.nonUniqueQuestion;

public class UpdateCardDialog extends Dialog {
    private final TextField question = new TextField("Question");
    private final VerticalLayout answersLayout = new VerticalLayout();
    private final TextArea example = new TextArea("Example of usage");
    private final TextField answerAria = new TextField("Write answer variant");
    private final CardService cardService;
    private final Button save = new Button("Save", event -> handleSave());
    private Card currentCard;
    private final Set<TextField> answerSet = new HashSet<>();

    public UpdateCardDialog(CardService cardService) {
        this.cardService = cardService;
        setWidth("30%");
        answersLayout.setWidthFull();
        var questionLayout = new VerticalLayout(question);
        question.setWidthFull();
        example.setWidthFull();

        var exit = new Button("Exit", event -> handleExit());
        exit.addThemeVariants(ButtonVariant.LUMO_ERROR);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        var cancel = new Button("Cancel changes", event -> handleCancel());
        cancel.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        var addNewAnswer = new Button(new Icon(VaadinIcon.PLUS_CIRCLE), event -> handleAddNewAnswer());
        addNewAnswer.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addNewAnswer.setWidth("5%");
        var newAnswerLayout = new HorizontalLayout(answerAria, addNewAnswer);
        newAnswerLayout.setWidthFull();
        newAnswerLayout.setAlignItems(FlexComponent.Alignment.END);
        answerAria.setWidth("95%");
        var verticalNewAnswer = new VerticalLayout(newAnswerLayout);
        verticalNewAnswer.setWidthFull();

        var exampleLayout = new VerticalLayout(example);

        add(questionLayout, verticalNewAnswer, answersLayout, exampleLayout);
        var footer = getFooter();
        footer.add(exit, cancel, save);
    }

    private void handleAddNewAnswer() {
        if (answerAria.getValue().trim().equals("")) {
            return;
        }
        var answerPlace = new HorizontalLayout();
        answerPlace.setWidthFull();
        var newAnswer = new TextField();
        newAnswer.setWidth("95%");
        newAnswer.setValue(answerAria.getValue().trim());

        answerSet.add(newAnswer);

        var delete = new Button(new Icon(VaadinIcon.TRASH), action -> {
            answersLayout.remove(answerPlace);
            answerSet.remove(newAnswer);
        });
        delete.setWidth("5%");

        answerPlace.add(newAnswer, delete);
        answersLayout.add(answerPlace);
        answerAria.clear();
    }

    private void handleSave() {
        if (!validate()) {
            return;
        }
        currentCard.setQuestion(question.getValue());
        currentCard.setExampleOfUsage(example.getValue());
        currentCard = cardService.saveCard(currentCard, answerSet.stream().map(TextField::getValue).collect(Collectors.toSet()));

        close();
        clear();
    }

    private void handleExit() {
        clear();
        close();
    }

    private void handleCancel() {
        clear();
        question.setValue(currentCard.getQuestion());
        example.setValue(currentCard.getExampleOfUsage() == null ? "" : currentCard.getExampleOfUsage());
        answersLayout.add(answersSet(currentCard));
    }

    public void initiate(Card card) {
        currentCard = card;
        handleCancel();
        open();
    }

    private Collection<Component> answersSet(Card card) {
         return card.getAnswers().stream().map(answer -> {
             var answerPlace = new HorizontalLayout();
             answerPlace.setWidthFull();
             var newAnswer = new TextField();
             newAnswer.setWidth("95%");
             newAnswer.setValue(answer.getAnswerText());

             answerSet.add(newAnswer);

             var delete = new Button(new Icon(VaadinIcon.TRASH), action -> {
                 answersLayout.remove(answerPlace);
                 answerSet.remove(newAnswer);
             });
             delete.setWidth("5%");

             answerPlace.add(newAnswer, delete);
            return answerPlace;
        }).collect(Collectors.toList());
    }

    private boolean validate() {
        var questionValue = question.getValue();
        if (questionValue.trim().equals("")) {
            incorrectCard();
            return false;
        }
        answerSet.removeIf(answer -> answer.getValue().trim().equals(""));
        if (answerSet.isEmpty()) {
            incorrectCard();
            return false;
        }
        if (!questionValue.equals(currentCard.getQuestion()) && cardService.existsByQuestion(questionValue)) {
            nonUniqueQuestion();
            return false;
        }
        return true;
    }

    private void clear() {
        answersLayout.removeAll();
        answerSet.clear();
        question.clear();
        example.clear();
        answerAria.clear();
    }
}
