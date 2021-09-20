package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.service.ProductLineService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Set;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class FiguresShuffle extends VerticalLayout {

    private final FigureService figureService;
    private final PhotoService photoService;
    private final ProductLineService productLineService;

    @PostConstruct
    public void init() {
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        shuffleFigures();
    }

    private void shuffleFigures() {
        figureService.getRandomFigure()
                .ifPresentOrElse(
                        figure -> {
                            ProductLine productLine = productLineService.findByCode(figure.getProductLine()).orElse(null);
                            Set<Photo> photos = photoService.findByFigure(figure);
                            update(figure, productLine, photos);
                            add(new Button("Shuffle", e -> shuffleFigures()));
                            Notification.show("Random figure is: " + figure.getVerbatim(), 3000, Notification.Position.TOP_CENTER);
                        },
                        this::removeAll
                );
    }

    private void update(Figure figure, ProductLine productLine, Set<Photo> photos) {
        removeAll();
        add(new PublicFigureViewer(figure, productLine, photos));
        setAlignItems(Alignment.CENTER);
        setHeightFull();
    }

}
