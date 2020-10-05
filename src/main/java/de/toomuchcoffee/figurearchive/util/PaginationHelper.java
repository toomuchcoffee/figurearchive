package de.toomuchcoffee.figurearchive.util;

import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginationHelper {
    public static <T> List<T> paginate(List<T> list, Pageable pageable) {
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), list.size());
        return list.subList(startIndex, endIndex);
    }
}
