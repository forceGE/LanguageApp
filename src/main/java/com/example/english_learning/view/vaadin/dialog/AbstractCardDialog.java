package com.example.english_learning.view.vaadin.dialog;

import com.example.english_learning.domain.Answer;
import com.example.english_learning.domain.Card;
import com.example.english_learning.service.CardService;
import com.example.english_learning.utils.CommonUtils;
import com.example.english_learning.view.vaadin.LearningView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Set;

public abstract class AbstractCardDialog extends Dialog {
    protected Card currentCard = null;
    protected final CardService cardService;
    protected final LearningView learningView;
    protected final Button delete = new Button("Delete", e -> handleDelete());
    protected final Button close = new Button("Close", e -> handleClose());
    protected final Button submit = new Button("Submit", e -> handleSubmit());
    protected final Button next = new Button("Next card", e -> handleNext());
    protected final Span success = new Span(createIcon(VaadinIcon.CHECK), new Span("SUCCESS!!"));
    protected final Span wrong = new Span(createIcon(VaadinIcon.CLOSE_SMALL), new Span("WRONG!!"));
    protected Boolean isActive = false;

    public AbstractCardDialog(CardService cardService, LearningView learningView) {
        super();
        this.learningView = learningView;
        this.cardService = cardService;
        setMaxWidth("30%");
        setWidthFull();

        wrong.getElement().getThemeList().add("badge error");
        wrong.setWidthFull();

        success.getElement().getThemeList().add("badge success");
        success.setWidthFull();

        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        submit.setVisible(true);
        next.setVisible(false);
        buildFooter();
        close();
    }

    public void initiate(Card card) {
        this.currentCard = card;
        success.setVisible(false);
        wrong.setVisible(false);
        submit.setVisible(true);
        next.setVisible(false);

        isActive = true;
    };

    abstract void handleSubmit();
    abstract void handleNext();

    private void handleClose() {
        close();
        currentCard = null;
    }

    private void handleDelete() {
        var confirm = new ConfirmDialog(
                "Delete this card?",
                "If you delete the card, it will be impossible to restore it",
                "Delete",
                event -> {
                    cardService.deleteById(currentCard.getId());
                    event.getSource().close();
                    close();
                    learningView.showRandomCard();
        });
        confirm.setCancelable(true);
        confirm.addCancelListener(event -> confirm.close());
        confirm.setConfirmButtonTheme("error primary");
        confirm.open();
    }

    protected boolean isValidTextAnswer(String answer, Set<Answer> variants) {
        return variants.stream()
                .map(Answer::getAnswerText)
                .anyMatch(variant -> CommonUtils.equalsByLevenshtein(variant, answer));
    }

    protected void correctAnswerAction() {
        currentCard.setCorrectAnswerCount(currentCard.getCorrectAnswerCount() + 1);
        success.setVisible(true);
        wrong.setVisible(false);
        submit.setVisible(false);
        next.setVisible(true);
        cardService.saveCard(currentCard);
    }

    private void buildFooter() {
        var footer = getFooter();
        footer.add(delete, close, submit, next);
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

    @Override
    public void close() {
        super.close();
        isActive = false;
    }
}
