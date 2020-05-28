package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;

import java.util.Set;

import static com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode.WRAP;

public class PublicFigureViewer extends Dialog {

    public PublicFigureViewer(Figure figure, Set<Photo> photos) {
        FlexLayout wrapper = new FlexLayout();
        wrapper.setWrapMode(WRAP);
        add(wrapper);

        Div attributes = new Div();
        VerticalLayout fields = new VerticalLayout();
        attributes.add(fields);
        attributes.setWidth("282px");

        Label verbatim = new Label(figure.getVerbatim());
        Label productLine = new Label(figure.getProductLine().getDescription() + " (" + figure.getProductLine().name() + ")");
        Label year = new Label(figure.getYear() != null ? String.valueOf(figure.getYear()) : "");
        Label placementNo = new Label(figure.getPlacementNo());
        Label count = new Label(figure.getCount() + " figure(s)");
        fields.add(verbatim, productLine, year, placementNo, count);

        ScrollableLayout scrollableLayout = new ScrollableLayout();
        scrollableLayout.setWidth("282px");
        PublicPhotoGallery publicPhotoGallery = new PublicPhotoGallery(photos);
        scrollableLayout.add(publicPhotoGallery);

        wrapper.add(attributes, scrollableLayout);
    }

}
