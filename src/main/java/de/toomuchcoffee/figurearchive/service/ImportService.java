package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ImportService {
    private final FigureRepository figureRepository;

    @Transactional
    public List<Figure> importCsv(byte[] data) throws IOException, CsvImportException {
        CSVParser parser = new CSVParser(
                new InputStreamReader(new ByteArrayInputStream(data)),
                CSVFormat.DEFAULT
                        .withHeader()
                        .withDelimiter(';')
                        .withIgnoreSurroundingSpaces()
                        .withIgnoreEmptyLines()
                        .withAllowMissingColumnNames());

        List<Figure> figures = new ArrayList<>();
        for (CSVRecord record : parser.getRecords()) {
            if (isNotEmpty(record)) {
                Figure figure = toFigure(record);
                figures.add(figure);
            }
        }
        figureRepository.saveAll(figures);
        return figures;
    }

    private Figure toFigure(CSVRecord record) throws CsvImportException {
        try {
            Figure figure = new Figure();
            ParsedCsvRecord parsedCsvRecord = new ParsedCsvRecord(record);
            BeanUtils.copyProperties(parsedCsvRecord, figure);
            return figure;
        } catch (Exception e) {
            throw new CsvImportException(String.format("Could not parse record: %s; %s", record, e.getMessage()));
        }
    }

    @Getter
    private static class ParsedCsvRecord {
        private final String verbatim;
        private final String productLine;
        private String placementNo;
        private Short year;

        ParsedCsvRecord(CSVRecord record) {
            this.verbatim = record.get("verbatim");
            this.productLine = record.get("productLine");
            if (record.isMapped("placementNo")) {
                this.placementNo = record.get("placementNo");
            }
            if (record.isMapped("year")) {
                String year = record.get("year");
                if (isNotBlank(year)) {
                    this.year = Short.parseShort(year);
                }
            }
        }
    }

    private boolean isNotEmpty(CSVRecord record) {
        return stream(record.toMap().values().stream().mapToInt(s -> s.trim().length()).toArray()).sum() > 0;
    }

    public static class CsvImportException extends Exception {
        CsvImportException(String message) {
            super(message);
        }
    }

}