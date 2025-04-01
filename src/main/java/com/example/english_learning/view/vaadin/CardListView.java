package com.example.english_learning.view.vaadin;

import com.example.english_learning.service.CardService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.english_learning.domain.CardType.TEST;
import static com.example.english_learning.domain.CardType.getByPreview;
import static com.example.english_learning.view.vaadin.notification.CommonNotificationUtils.incorrectCard;
import static com.example.english_learning.view.vaadin.notification.CommonNotificationUtils.nonUniqueQuestion;

@PageTitle("Card List")
@Route(value = "card-list", layout = MainLayout.class)
@Uses(Icon.class)
public class CardListView extends Composite<VerticalLayout> {

    private final VerticalLayout verticalContainer = new VerticalLayout();
    private final Set<TextField> answerSet = new HashSet<>();
    private final TextField questionAria = new TextField("Write question");
    private final TextField answerAria = new TextField("Write answer variant");
    private final Button createCardButton = new Button("Add new Card", e -> createCard());
    private final Button clearButton = new Button("Clear", e -> clear());
    private final RadioButtonGroup<String> cardTypeRadio = new RadioButtonGroup<>();
    private final TextArea exampleAria = new TextArea("Example of usage");
    private final CardService service;

    public CardListView(CardService service) {
        this.service = service;
        createCardButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        createCardButton.addClickShortcut(Key.TAB);
        clearButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        var exampleLayout = new VerticalLayout();
        exampleAria.setWidthFull();
        exampleLayout.add(exampleAria);

        var main = new VerticalLayout();
        main.setMaxWidth("30%");
        main.add(layout(), cardTypeRadio(), exampleLayout, buttonsLayout());

        getContent().add(main);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private VerticalLayout layout() {
        var title = new H3("Create new card");
        questionAria.setAutofocus(true);
        questionAria.setWidthFull();
        verticalContainer.add(title, questionAria);

        var horizontal = new HorizontalLayout();
        horizontal.setAlignItems(FlexComponent.Alignment.END);
        horizontal.setWidthFull();
        answerAria.setWidth("70%");
        questionAria.addKeyPressListener(Key.ENTER, e -> answerAria.focus());

        var addAnswer = new Button("Add answer variant", e -> {
            if (answerAria.getValue().equals("")) return;

            var answerPlace = new HorizontalLayout();
            answerPlace.setWidthFull();
            var newAnswer = new TextField();
            newAnswer.setWidth("95%");

            var delete = new Button(new Icon(VaadinIcon.TRASH), action -> {
                verticalContainer.remove(answerPlace);
                answerSet.remove(newAnswer);
            });
            delete.setWidth("5%");

            answerPlace.add(newAnswer, delete);

            newAnswer.setWidthFull();
            newAnswer.setValue(answerAria.getValue());

            verticalContainer.add(answerPlace);
            answerSet.add(newAnswer);
            answerAria.clear();
            createCardButton.focus();
        });
        addAnswer.getStyle().set("marginTop", "10px");
        addAnswer.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        answerAria.addKeyPressListener(Key.ENTER, e -> {
            addAnswer.click();
            answerAria.focus();
        });
        addAnswer.setWidth("30%");
        horizontal.add(answerAria, addAnswer);
        verticalContainer.add(horizontal);
        return verticalContainer;
    }

    private RadioButtonGroup<String> cardTypeRadio() {
        cardTypeRadio.setLabel("Card type");
        cardTypeRadio.setItems("Language", "Text answer");
//        cardTypeRadio.setItems("Language", "Text answer", "Test");
        cardTypeRadio.setValue("Language");
        cardTypeRadio.getElement().getStyle().set("margin-left", "15px");
        cardTypeRadio.addValueChangeListener(event -> {
            if (event.getValue().equals("Language")) {
                exampleAria.setVisible(true);
                return;
            }
            exampleAria.clear();
            exampleAria.setVisible(false);
        });
        return cardTypeRadio;
    }

    private HorizontalLayout buttonsLayout() {
        var layout = new HorizontalLayout();
        layout.setWidth("30%");
        layout.getElement().getStyle().set("margin-left", "15px");
        layout.getElement().getStyle().set("margin-top", "15px");
        layout.add(createCardButton, clearButton);
        return layout;
    }

    private void createCard() {
        var question = questionAria.getValue().trim();
        var answers = answerSet.stream()
                .map(answer -> answer.getValue().trim())
                .filter(value -> !value.equals(""))
                .collect(Collectors.toSet());
        if (!answerAria.getValue().equals("")) {
            answers.add(answerAria.getValue());
        }
        if (question.equals("") || answers.isEmpty()) {
            incorrectCard();
            if (questionAria.getValue().equals("")) questionAria.focus();
            else answerAria.focus();
            return;
        }
        if (getByPreview(cardTypeRadio.getValue()) == TEST && answers.size() == 1) {
            incorrectTestCard();
            return;
        }
        save(question, answers);
        questionAria.focus();
    }

    private void clear() {
        answerSet.forEach(x -> verticalContainer.remove(x.getParent().orElse(null)));
        answerSet.clear();
        answerAria.clear();
        questionAria.clear();
        exampleAria.clear();
    }

    private void save(String question, Set<String> answers) {
        try {
            String example = null;
            if (!exampleAria.getValue().trim().equals("")) {
                example = exampleAria.getValue();
            }
            service.saveNewCard(question, answers, getByPreview(cardTypeRadio.getValue()), example);
            cardAdded();
            clear();
        } catch (DataIntegrityViolationException ex) {
            nonUniqueQuestion();
        }
    }

    private void incorrectTestCard() {
        Notification
                .show("A card with the type 'Test' cannot have only one answer option.",
                        3000,
                        Notification.Position.TOP_CENTER
                )
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void cardAdded() {
        Notification
                .show("New card added", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}