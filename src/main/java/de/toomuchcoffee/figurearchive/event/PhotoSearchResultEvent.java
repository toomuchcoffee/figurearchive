package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PhotoSearchResultEvent implements PaginationTabs.SearchResultEvent<String> {
    private final long count;
    private final int page;
    private final int size;
    private final String filter;
}
