package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@UIScope
@Tag("div")
@RequiredArgsConstructor
public class ImageGallery extends HorizontalLayout {
    private final int tileSize;
    private final int rowSize;
    private final int columnSize;

    private final Consumer<Photo> consumer;

    public void update(List<Photo> photos) {
        removeAll();
        int maxSize = rowSize * columnSize;

        VerticalLayout container = newVerticalLayout();
        add(container);

        int pageSize = Math.min(photos.size(), maxSize);

        HorizontalLayout row = null;
        for (int i = 0; i < pageSize; i++) {
            if (i % rowSize == 0) {
                row = newHorizontalLayout();
            }
            container.add(row);

            Photo photo = photos.get(i);
            row.add(new ImageButton(photo, tileSize, e -> consumer.accept(photo)));
        }

        if (maxSize < photos.size()) {
            add(new Label("More..."));
        }
    }

    private HorizontalLayout newHorizontalLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private VerticalLayout newVerticalLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

}
