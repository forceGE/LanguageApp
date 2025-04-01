package com.example.english_learning.view.vaadin;

import com.example.english_learning.domain.Card;
import com.example.english_learning.domain.CardType;
import com.example.english_learning.service.CardService;
import com.example.english_learning.view.vaadin.dialog.AbstractCardDialog;
import com.example.english_learning.view.vaadin.dialog.LanguageCardDialog;
import com.example.english_learning.view.vaadin.dialog.TestCardDialog;
import com.example.english_learning.view.vaadin.dialog.TextCardDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.Map;

import static com.example.english_learning.domain.CardType.*;

@PageTitle("Go learning!!")
@Route(value = "learning", layout = MainLayout.class)
@RouteAlias(value = "learning", layout = MainLayout.class)
public class LearningView extends HorizontalLayout {

    private final CardService cardService;
    private final Map<CardType, AbstractCardDialog> dailogCardMap;
    private final Button startLearningButton = new Button("START LEARNING!!", e -> showRandomCard());
    private final VerticalLayout zeroCardLayout = buildCommonLayout("You don't have any cards yet!");
    private final VerticalLayout zeroActualCardLayout = buildCommonLayout("You don't have any unlearned cards!");

    public LearningView(CardService cardService) {
        this.cardService = cardService;

        this.dailogCardMap = Map.of(
                LANGUAGE, new LanguageCardDialog(cardService, this),
                TEXT, new TextCardDialog(cardService, this),
                TEST, new TestCardDialog(cardService, this)
        );

        startLearningButton.addThemeVariants(
                ButtonVariant.LUMO_LARGE,
                ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY
        );
        zeroCardLayout.setVisible(false);
        zeroActualCardLayout.setVisible(false);
        add(zeroCardLayout, zeroActualCardLayout);

        updateStatusPage();
    }

    private void updateStatusPage() {
        this.getChildren().forEach(x -> x.setVisible(false));
        if (cardService.countAll() == 0) {
            zeroCardLayout.setVisible(true);
            zeroActualCardLayout.setVisible(false);
        } else if (cardService.countActualCard() == 0) {
            zeroCardLayout.setVisible(false);
            zeroActualCardLayout.setVisible(true);
        } else {
            var layout = new VerticalLayout();
            layout.setSizeFull();
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.setAlignItems(Alignment.CENTER);
            layout.add(startLearningButton);
            add(layout);
        }
    }

    public void showRandomCard() {
        Card currentCard = cardService.getRandomCard();

        if (currentCard == null) {
            updateStatusPage();
            return;
        }
        var cardType = currentCard.getCardType();

        var dialog = dailogCardMap.get(cardType);
        dialog.initiate(currentCard);
        dialog.open();
    }

    private VerticalLayout buildCommonLayout(String h2) {
        var layout = new VerticalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);
        var text = new H2(h2);
        layout.add(text);
        return layout;
    }
}