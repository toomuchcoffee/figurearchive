package de.toomuchcoffee.figurearchive.view.photo;


import com.vaadin.flow.component.combobox.ComboBox;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class PhotoTagFilter extends ComboBox<String> {

    public PhotoTagFilter(PhotoService photoService, ValueChangeListener<ValueChangeEvent<String>> valueChangeListener) {
        setPlaceholder("Filter by Tag");
        setClearButtonVisible(true);
        addValueChangeListener(valueChangeListener);
        setItems(photoService.findAll().stream()
                .map(Photo::getTags)
                .flatMap(Arrays::stream)
                .map(String::toLowerCase)
                .collect(toSet())
                .stream()
                .sorted()
                .collect(toList()));

    }
}
