package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.view.controls.ImageButton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

import static com.vaadin.flow.component.icon.VaadinIcon.ARROW_BACKWARD;
import static com.vaadin.flow.component.icon.VaadinIcon.ARROW_FORWARD;

@UIScope
@RequiredArgsConstructor
public class ImageGallery extends HorizontalLayout {
    private final int tileSize;
    private final int rowSize;
    private final int columnSize;

    private final Consumer<Photo> consumer;

    public void update(List<Photo> photos) {
        update(photos, 0);
    }

    private void update(List<Photo> photos, int pageNumber) {
        int pageSize = rowSize * columnSize;
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, photos.size());

        removeAll();

        if (pageNumber > 0) {
            add(new Button(ARROW_BACKWARD.create(), e -> update(photos, pageNumber - 1)));
        }

        VerticalLayout col = newVerticalLayout();
        HorizontalLayout row = newHorizontalLayout();
        for (int i = startIndex; i < endIndex; i++) {
            if (i % rowSize == 0) {
                row = newHorizontalLayout();
            }
            col.add(row);

            Photo photo = photos.get(i);
            row.add(new ImageButton(photo, tileSize, e -> consumer.accept(photo)));
        }
        add(col);

        if (endIndex < photos.size()) {
            add(new Button(ARROW_FORWARD.create(), e -> update(photos, pageNumber + 1)));
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
