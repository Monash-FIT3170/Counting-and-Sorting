package com.business.application.components.avataritem;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AvatarItem extends Composite<HorizontalLayout> implements HasSize {

    private Span heading = new Span();

    private Span description = new Span();

    public AvatarItem() {

        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        description.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size",
                "var(--lumo-font-size-s)");

        VerticalLayout column = new VerticalLayout(heading, description);
        column.setPadding(false);
        column.setSpacing(false);

        getContent().add(column);
        getContent().getStyle().set("line-height", "var(--lumo-line-height-m)");
    }

    public AvatarItem(String heading, String description, Avatar avatar) {
        this();
        setHeading(heading);
        setDescription(description);
        setAvatar(avatar);
    }

    public void setHeading(String text) {
        heading.setText(text);
    }

    public void setDescription(String text) {
        description.setText(text);
    }

    public void setAvatar(Avatar avatar) {
        if (getContent().getComponentAt(0) instanceof Avatar existing) {
            existing.removeFromParent();
        }
        getContent().addComponentAsFirst(avatar);
    }

}
