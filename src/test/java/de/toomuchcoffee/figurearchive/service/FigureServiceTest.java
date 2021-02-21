package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.vaadin.spring.events.EventBus.UIEventBus;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static de.toomuchcoffee.figurearchive.entity.ProductLine.KENNER;
import static de.toomuchcoffee.figurearchive.entity.ProductLine.POTF2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FigureServiceTest {
    @Mock
    private FigureRepository figureRepository;

    @Mock
    private UIEventBus eventBus;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private FullTextEntityManager fullTextEntityManager;

    private static final List<Figure> FIGURES = newArrayList(
            new Figure(0L, "foo", KENNER, null, null, 0),
            new Figure(1L, "foo", POTF2, null, null, 0)
    );

    private FigureService figureService;

    @Before
    public void setUp() throws Exception {
        figureService = new FigureService(figureRepository, eventBus, fullTextEntityManager);
        when(figureRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(FIGURES));
        when(figureRepository.findByProductLine(any(ProductLine.class), any(PageRequest.class))).thenReturn(new PageImpl<>(FIGURES));
        when(fullTextEntityManager.createFullTextQuery(any(Query.class), any()).getResultList()).thenReturn(FIGURES);
    }

    @Test
    public void findWithFilterPropertiesNull() {
        List<Figure> figures = figureService.findFigures(0, 1, new FigureFilter());

        assertThat(figures).hasSize(2);

        verify(figureRepository).count();
        verify(figureRepository).findAll(any(PageRequest.class));
        verifyNoInteractions(fullTextEntityManager.createFullTextQuery(any(Query.class), any()));
        verify(eventBus).publish(eq(figureService), any(FigureSearchResultEvent.class));
    }

    @Test
    public void findWithFilterByVerbatimOnly() {
        figureService.findFigures(0, 1, new FigureFilter("foo", null));

        assertThat(FIGURES).hasSize(2);
        verifyNoInteractions(figureRepository);
        verify(fullTextEntityManager).createFullTextQuery(any(Query.class), eq(Figure.class));
        verify(eventBus).publish(eq(figureService), any(FigureSearchResultEvent.class));
    }

    @Test
    public void findWithFilterByProductLineOnly() {
        List<Figure> figures = figureService.findFigures(0, 1, new FigureFilter(null, KENNER));

        assertThat(figures).hasSize(2);
        verify(figureRepository).countByProductLine(any(ProductLine.class));
        verify(figureRepository).findByProductLine(any(ProductLine.class), any(PageRequest.class));
        verifyNoInteractions(fullTextEntityManager.createFullTextQuery(any(Query.class), any()));
        verify(eventBus).publish(eq(figureService), any(FigureSearchResultEvent.class));
    }

    @Test
    public void findWithFilterByVerbatimAndProductLine() {
        List<Figure> figures = figureService.findFigures(0, 1, new FigureFilter("foo", KENNER));

        assertThat(figures).hasSize(1);
        verifyNoInteractions(figureRepository);
        verify(fullTextEntityManager).createFullTextQuery(any(Query.class), eq(Figure.class));
        verify(eventBus).publish(eq(figureService), any(FigureSearchResultEvent.class));
    }
}