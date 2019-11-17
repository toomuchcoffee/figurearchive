package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.view.controls.ImageButton;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@UIScope
@RequiredArgsConstructor
public class PhotoGallery extends ScrollableLayout {
    private final int tileSize;
    private final int rowSize;

    private final Consumer<Photo> consumer;

    public void update(List<Photo> photos) {
        removeAll();

        VerticalLayout col = newVerticalLayout();
        HorizontalLayout row = newHorizontalLayout();
        for (int i = 0; i < photos.size(); i++) {
            if (i % rowSize == 0) {
                row = newHorizontalLayout();
            }
            col.add(row);

            Photo photo = photos.get(i);
            row.add(new ImageButton(photo, tileSize, e -> consumer.accept(photo)));
        }
        add(col);
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
