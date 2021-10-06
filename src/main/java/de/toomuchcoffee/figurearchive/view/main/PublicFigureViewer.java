package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class PublicFigureViewer extends VerticalLayout {
    public PublicFigureViewer(Figure figure, ProductLine productLine, Set<Photo> photos) {
        add(joinNonNullStrings(", ", figure.getVerbatim(), figure.getPlacementNo()));
        add(new Paragraph(joinNonNullStrings(" ", productLine.getDescription(), figure.getYear())));
        add(new Paragraph(String.format("%s figure(s)", figure.getCount())));

        ScrollableLayout scrollableLayout = new ScrollableLayout();
        scrollableLayout.setWidth("282px");
        PublicPhotoGallery publicPhotoGallery = new PublicPhotoGallery(photos);
        scrollableLayout.add(publicPhotoGallery);
        add(scrollableLayout);

        setAlignItems(Alignment.CENTER);
        setHeightFull();
        setWidthFull();
    }

    private String joinNonNullStrings(String delimiter, Object... strings) {
        return Stream.of(strings)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(joining(delimiter));
    }
}
