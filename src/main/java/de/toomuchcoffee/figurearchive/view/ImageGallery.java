package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

import static com.vaadin.flow.component.icon.VaadinIcon.MINUS;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

@UIScope
@Tag("div")
@RequiredArgsConstructor
public class ImageGallery extends HorizontalLayout {
    private final int tileSize;
    private final int rowSize;
    private final int columnSize;

    private final Consumer<Photo> consumer;

    public void update(List<Photo> photos) {
        update(photos, 0);
    }

    public void update(List<Photo> photos, int pageNumber) {
        removeAll();
        int pageSize = rowSize * columnSize;

        HorizontalLayout horizontalLayout = newHorizontalLayout();
        VerticalLayout verticalLayout = newVerticalLayout();

        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, photos.size());

        if (pageNumber > 0) {
            horizontalLayout.add(new Button(MINUS.create(), e -> update(photos, pageNumber-1)));
        }

        HorizontalLayout row = null;
        for (int i = startIndex; i < endIndex; i++) {
            if (i % rowSize == 0) {
                row = newHorizontalLayout();
            }
            verticalLayout.add(row);

            Photo photo = photos.get(i);
            row.add(new ImageButton(photo, tileSize, e -> consumer.accept(photo)));
        }
        horizontalLayout.add(verticalLayout);
        if (endIndex < photos.size()) {
            horizontalLayout.add(new Button(PLUS.create(), e -> update(photos, pageNumber+1)));
        }
        add(horizontalLayout);
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
