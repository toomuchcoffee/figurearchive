package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoSearchEvent implements PaginationTabs.SearchEvent<String> {
    private String filter;
    private int page;
}
