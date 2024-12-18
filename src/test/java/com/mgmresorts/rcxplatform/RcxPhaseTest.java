package com.mgmresorts.rcxplatform;

import com.mgmresorts.common.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RcxPhaseTest {
    @InjectMocks
    @Spy
    private RcxPhase rcxPhase;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    protected void getStage_happyPath_returnsEnum() {
        assertEquals(ParallelMode.PHASE_1_OFF, rcxPhase.getPhase(1));
        assertEquals(ParallelMode.PHASE_2_BUFFER, rcxPhase.getPhase(2));
        assertEquals(ParallelMode.PHASE_3_PARALLEL_NON_RCX, rcxPhase.getPhase(3));
        assertEquals(ParallelMode.PHASE_4_PARALLEL_RCX, rcxPhase.getPhase(4));
        assertEquals(ParallelMode.PHASE_5_FINAL, rcxPhase.getPhase(5));
    }

    @Test // (expected = IllegalArgumentException.class)
    protected void getStage_InvalidValues_returnsOff() {
        assertEquals(ParallelMode.PHASE_1_OFF, rcxPhase.getPhase(0));
        assertEquals(ParallelMode.PHASE_1_OFF, rcxPhase.getPhase(6));
        assertEquals(ParallelMode.PHASE_1_OFF, rcxPhase.getPhase(7));
    }

    @Test
    protected void getCurrentPhase_validDates_returnsStage1() throws Exception {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(10),
                getRelativeInstant(20),
                getRelativeInstant(30),
                getRelativeInstant(40)
        };
        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_validDates_returnsStage2() throws Exception {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-10),
                getRelativeInstant(10),
                getRelativeInstant(20),
                getRelativeInstant(30)
        };
        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_2_BUFFER, actual);
    }

    @Test
    protected void getCurrentPhase_validDates_returnsStage3() throws Exception {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-20),
                getRelativeInstant(-10),
                getRelativeInstant(10),
                getRelativeInstant(20)
        };
        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_3_PARALLEL_NON_RCX, actual);
    }

    @Test
    protected void getCurrentPhase_validDates_returnsStage4() throws Exception {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-30),
                getRelativeInstant(-20),
                getRelativeInstant(-10),
                getRelativeInstant(10),
        };
        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_4_PARALLEL_RCX, actual);
    }

    @Test
    protected void getCurrentPhase_validDates_returnsStage5() throws Exception {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-30),
                getRelativeInstant(-20),
                getRelativeInstant(-10),
                getRelativeInstant(-1),
        };
        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_5_FINAL, actual);
    }

    protected Instant getRelativeInstant(int numberOfDays) {
        return Instant.now().plus(numberOfDays, ChronoUnit.DAYS);
    }

    @Test
    protected void getCurrentPhase_invalidStage2Future_returnPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                Instant.MIN,
                getRelativeInstant(10),
                getRelativeInstant(25),
                getRelativeInstant(30),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage3Past_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-5),
                getRelativeInstant(-20),
                getRelativeInstant(-10),
                getRelativeInstant(10),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage3Future_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(5),
                getRelativeInstant(2),
                getRelativeInstant(10),
                getRelativeInstant(20),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage4Past_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-5),
                getRelativeInstant(-2),
                getRelativeInstant(-10),
                getRelativeInstant(10),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage4Future_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(5),
                getRelativeInstant(8),
                getRelativeInstant(2),
                getRelativeInstant(20),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage5Past_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(-5),
                getRelativeInstant(-2),
                getRelativeInstant(-1),
                getRelativeInstant(-2),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getCurrentPhase_invalidStage5Future_returnsPhase1() throws AppException {
        Instant[] startTimes = new Instant[] {
                getRelativeInstant(5),
                getRelativeInstant(8),
                getRelativeInstant(10),
                getRelativeInstant(2),
        };

        ParallelMode actual = rcxPhase.getCurrentPhase(startTimes, Instant.now());
        assertEquals(ParallelMode.PHASE_1_OFF, actual);
    }

    @Test
    protected void getRcxPrimary_allPhases() {
        for (ParallelMode phase: ParallelMode.values()) {
            boolean rcxPrimary = rcxPhase.getRcxPrimary(phase);

            boolean expectedValue = false;
            if (phase == ParallelMode.PHASE_4_PARALLEL_RCX || phase == ParallelMode.PHASE_5_FINAL) {
                expectedValue = true;
            }

            assertEquals(expectedValue, rcxPrimary);
        }
    }
}
