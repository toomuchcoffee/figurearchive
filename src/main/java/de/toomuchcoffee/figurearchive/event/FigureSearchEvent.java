package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FigureSearchEvent implements PaginationTabs.SearchEvent<FigureService.FigureFilter> {
    private FigureService.FigureFilter filter;
    private int page;
}
