package com.example.english_learning.view.vaadin.notification;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonNotificationUtils {
    public static void nonUniqueQuestion() {
        Notification
                .show("You cannot add cards with a non-unique question!",
                        3000,
                        Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static void incorrectCard() {
        Notification
                .show("You cannot add a card without a question or an answer.",
                        3000,
                        Notification.Position.TOP_CENTER
                )
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
