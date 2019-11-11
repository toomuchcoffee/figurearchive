package de.toomuchcoffee.figurearchive.view.photo;


import com.vaadin.flow.component.combobox.ComboBox;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PhotoTagFilter extends ComboBox<String> {

    public PhotoTagFilter(PhotoRepository photoRepository, ValueChangeListener<ValueChangeEvent<String>> valueChangeListener) {
        setPlaceholder("Filter by Tag");
        addValueChangeListener(valueChangeListener);
        setItems(photoRepository.findAll().stream()
                .map(Photo::getTags)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet())
                .stream()
                .sorted()
                .collect(Collectors.toList()));

    }
}
