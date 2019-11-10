package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper;

import java.util.Arrays;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.stream.Collectors.toList;

public class PhotoGrid extends Grid<Photo> {

    public PhotoGrid(
            ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> photoDataProvider,
            ConfigProperties properties,
            ValueChangeListener<ValueChangeEvent<Photo>> valueChangeListener) {
        super(Photo.class);
        asSingleSelect().addValueChangeListener(valueChangeListener);
        setDataProvider(photoDataProvider);
        setPageSize(properties.getPhotos().getPageSize());
        setColumns("postId");
        addComponentColumn(photo -> new Image(getImageUrl(photo, 250), "N/A"))
                .setHeader("Image");
        addComponentColumn(photo -> new UnorderedList(photo.getUrls().stream()
                .map(url -> url.getWidth() + "x" + url.getHeight())
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Available Sizes (WxH)");
        addComponentColumn(photo -> new UnorderedList(Arrays.stream(photo.getTags())
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Tags");
        addComponentColumn(photo -> new UnorderedList(photo.getFigures().stream()
                .map(FigureDisplayNameHelper::getDisplayName)
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Figures");
    }

}
