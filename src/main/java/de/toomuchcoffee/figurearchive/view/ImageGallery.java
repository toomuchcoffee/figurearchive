package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.collect.Sets.newHashSet;

@UIScope
@Tag("div")
@RequiredArgsConstructor
public class ImageGallery extends VerticalLayout {
    private final int tileSize;
    private final int rowSize;
    private final int columnSize;

    private final Consumer<Set<Photo>> consumer;

    public void update(List<Photo> photos) {
        removeAll();

//        int startIndex = 0;
//        int offset = startIndex * MAX_PAGE_SIZE;
//        List<Photo> page = imageUrls.subList(offset, offset + MAX_PAGE_SIZE);

        int pageSize = Math.min(photos.size(), rowSize * columnSize);

        HorizontalLayout row = new HorizontalLayout();
        for (int i = 0; i < pageSize; i++) {
            if (i % rowSize == 0) {
                row = new HorizontalLayout();
            }
            add(row);

            Photo photo = photos.get(i);
            row.add(new ImageButton(photo, tileSize, e -> consumer.accept(newHashSet(photo))));
        }
    }

}
