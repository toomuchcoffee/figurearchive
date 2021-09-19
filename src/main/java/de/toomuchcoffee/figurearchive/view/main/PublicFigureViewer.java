package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;

import java.util.Set;

public class PublicFigureViewer extends VerticalLayout {
    public PublicFigureViewer(Figure figure, Set<Photo> photos) {
        add(new H3(String.format("%s, %s", figure.getVerbatim(), figure.getPlacementNo())));
        add(new Paragraph(String.format("%s, %s", figure.getProductLine(), figure.getYear())));
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
}
