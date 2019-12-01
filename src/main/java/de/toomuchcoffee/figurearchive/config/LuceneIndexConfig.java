package de.toomuchcoffee.figurearchive.config;

import org.hibernate.search.batchindexing.impl.SimpleIndexingProgressMonitor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class LuceneIndexConfig {

    @Bean
    public FullTextEntityManager fullTextEntityManager(EntityManagerFactory entityManagerFactory, CustomProgressMonitor progressMonitor) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
        fullTextEntityManager.createIndexer()
                .progressMonitor(progressMonitor)
                .start();
        return fullTextEntityManager;
    }

    @Bean
    public CustomProgressMonitor customProgressMonitor() {
        return new CustomProgressMonitor();
    }

    public static class CustomProgressMonitor extends SimpleIndexingProgressMonitor {
        private AtomicInteger count = new AtomicInteger(0);
        private AtomicLong max = new AtomicLong(0);
        private AtomicBoolean done = new AtomicBoolean(false);

        @Override
        public void addToTotalCount(long count) {
            max.getAndAdd(count);
            super.addToTotalCount(count);
        }

        @Override
        public void documentsBuilt(int number) {
            count.getAndAdd(number);
            super.documentsBuilt(number);
        }

        @Override
        public void indexingCompleted() {
            done.getAndSet(true);
            super.indexingCompleted();
        }

        public double getProgress() {
            return (double) count.get() / (double) max.get();
        }

        public boolean isDone() {
           return done.get();
        }
    }

}