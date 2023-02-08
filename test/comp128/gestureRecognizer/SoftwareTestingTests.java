package comp128.gestureRecognizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import edu.macalester.graphics.Point;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Software Testing Tests MkI
 */
public class SoftwareTestingTests {

    private Recognizer recognizer;
    private Deque<Point> originalPoints;

    private static final int ORIGINAL_N = 20;
    private static final int n = 64;

    @BeforeEach
    public void setup() {
        recognizer = new Recognizer();
        originalPoints = new ArrayDeque<>(ORIGINAL_N);
        for (int i = 0; i < ORIGINAL_N; i++) {
            originalPoints.offerLast(new Point(i, 0));
        }
    }

    /** Passes multiple template names with points (<arbitrary>, <points>)
    to ensure that templates with points are correctly being added to the HashMap without
    removing or adding incorrect data
     */
    @Test
    public void testAddTemplate() {
        recognizer.addTemplate("test1", originalPoints);
        recognizer.addTemplate("test2", originalPoints);

        assertTrue(recognizer.getTemplateList().containsKey("test1"));
        assertTrue(recognizer.getTemplateList().containsKey("test2"));
        assertFalse(recognizer.getTemplateList().containsKey("not a template"));
    }

    /**
     * Tests that ensure testResample runs properly
     */
    @Test
    public void testResample() {
        recognizer.addTemplate("test1", originalPoints);
        recognizer.addTemplate("test2", originalPoints);

        Deque<Point> template = recognizer.resample(originalPoints, n);
        assertEquals(n, template.size());
    }

    @Test
    public void testsResampleSizeCantNotBe64() {
         // Currently this test fails, as we haven't implemented a failsafe to ensure that
         // the Deque always contains 64 points

        recognizer.addTemplate("test1", originalPoints);
        recognizer.addTemplate("test2", originalPoints);

        int k = 60;
        Deque<Point> template1 = recognizer.resample(originalPoints, k);
        assertEquals(64, template1.size());

        k = 67;
        Deque<Point> template2 = recognizer.resample(originalPoints, k);
        assertEquals(64, template2.size());
    }

    /**
     * Tests for rotateByFunction
     */
    @Test
    public void rotatesCorrectly() {
        //TODO: guys doing this one is fucking hard
    }

    /**
     *  TODO: okay the rest of are really mathy
     */

}