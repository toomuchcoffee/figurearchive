package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PermutationService {

    public Set<String> getPermutations(String string) {
        HashSet<String> permutations = Sets.newHashSet();

        String[] tokens = string.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("\\s+", " ").toLowerCase().split(" ");

        for (int x = 0; x < tokens.length; x++) {
            for (int y = 0; y < tokens.length - x; y++) {
                permutations.add(concatTokens(tokens, x, x+y, " "));
                permutations.add(concatTokens(tokens, x, x+y, ""));
            }
        }
        return permutations;
    }

    private String concatTokens(String[] tokens, int start, int end, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; i++) {
            if (sb.length() > 0) sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

}
