package de.toomuchcoffee.figurearchive.service;

import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class PermutationServiceTest {

    private PermutationService permutationService = new PermutationService();

    @Test
    public void testGetVerbatimPermutations() throws Exception {
        Set<String> permutations = permutationService.getPermutations("Imperial TIE Fighter Pilot");
        assertThat(newHashSet(
                "imperial tie fighter pilot",
                "imperial tie fighter",
                         "tie fighter pilot",
                "imperial tie",
                         "tie fighter",
                             "fighter pilot",
                "imperial",
                         "tie",
                             "fighter",
                                     "pilot",
                "imperialtiefighterpilot",
                "imperialtiefighter",
                        "tiefighterpilot",
                "imperialtie",
                        "tiefighter",
                           "fighterpilot",
                "imperial",
                        "tie",
                           "fighter",
                                  "pilot"
        )).isEqualTo(permutations);
    }

    @Test
    public void testGetVerbatimPermutationsWithParenthesis() throws Exception {
        Set<String> permutations = permutationService.getPermutations("(Twin Pod) Cloud Car Pilot");
        assertThat(permutations.containsAll(newHashSet("cloudcar", "cloudcarpilot"))).isTrue();
    }

}