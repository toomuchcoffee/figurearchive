package de.toomuchcoffee.figurearchive.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllTableRowCounts() {
        List<?> tableNames = entityManager.createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema='public'")
                .getResultList();

        String sql = tableNames.stream()
                .map(table -> "SELECT COUNT(*) FROM " + table)
                .collect(Collectors.joining(" UNION "));

        System.out.println(sql);

        return entityManager.createNativeQuery(sql).getResultList();
    }

    public Long getTotalRowCount() {
        return getAllTableRowCounts().stream()
                .filter(o -> o instanceof BigInteger)
                .map(o -> ((BigInteger) o).longValue())
                .reduce(Long::sum)
                .orElse(0L);
    }
}
