package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FigureSearchResultEvent implements PaginationTabs.SearchResultEvent<FigureService.FigureFilter> {
    private final long count;
    private final long owned;
    private final int page;
    private final int size;
    private final FigureService.FigureFilter filter;
}
