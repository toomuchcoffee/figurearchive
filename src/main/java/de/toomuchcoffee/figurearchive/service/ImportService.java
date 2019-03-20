package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ImportService {

    @Autowired
    private final FigureRepository figureRepository;

    @Transactional
    public void importCsv(byte[] data) throws IOException {
        CSVParser parser = new CSVParser(
                new InputStreamReader(new ByteArrayInputStream(data)),
                CSVFormat.DEFAULT
                        .withHeader()
                        .withDelimiter(';')
                        .withIgnoreSurroundingSpaces()
                        .withIgnoreEmptyLines()
                        .withAllowMissingColumnNames());

        List<Figure> figures = parser.getRecords().stream()
                .filter(this::isNotEmpty)
                .map(this::toFigure)
                .collect(toList());
        figureRepository.saveAll(figures);
    }

    private Figure toFigure(CSVRecord record) {
        Figure figure = new Figure();
        figure.setVerbatim(record.get("verbatim"));
        figure.setProductLine(ProductLine.valueOf(record.get("productLine")));
        figure.setPlacementNo(record.get("placementNo"));
        figure.setYear(Short.valueOf(record.get("year")));
        return figure;
    }

    private boolean isNotEmpty(CSVRecord record) {
        return stream(record.toMap().values().stream().mapToInt(s -> s.trim().length()).toArray()).sum() > 0;
    }

}