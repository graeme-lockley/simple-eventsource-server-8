package za.co.no9.ses8.adaptors.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class H2Test {
    @BeforeEach
    void before() throws IOException, FixtureException {
    }

    @Test
    void aTest() throws IOException, FixtureException {
        Fixtures fixtures =
                Fixtures.process(FixturesInput.fromLocation("resource:initial.yaml"));

        assertEquals(1, 1);
    }
}