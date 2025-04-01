package com.example.english_learning.view.vaadin.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class ChangeMainTabEvent extends ComponentEvent<Component> {
    public ChangeMainTabEvent(Component source) {
        super(source, false);
    }
}
