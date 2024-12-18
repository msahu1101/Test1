package com.mgmresorts.rcxplatform;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Instant;

@Named
@Singleton
public class RcxPhase {
    private final int offset = 2;
    private final int totalPhases = 5;
    private final Logger logger = Logger.get(RcxPhase.class);

    public ParallelMode getCurrentPhase() throws AppException {
        Instant[] startTimes = getStartTimes();
        return getCurrentPhase(startTimes, Instant.now());
    }

    protected Instant[] getStartTimes() {
        Instant[] startTimes = new Instant[totalPhases - 1];
        String currentStageDateTimeString;
        for (int i = offset; i <= offset + startTimes.length - 1; i++) {
            currentStageDateTimeString = Runtime.get().getConfiguration("rcx.platform.phase" + i + ".startTimeUtc");
            startTimes[i - offset] = Instant.parse(currentStageDateTimeString);
        }
        return startTimes;
    }

    public ParallelMode getCurrentPhase(Instant[] startTimes, Instant currentTime) throws AppException {
        Instant previousPhaseStartTime = Instant.MIN;
        ParallelMode currentPhase = ParallelMode.PHASE_1_OFF;

        for (int i = offset; i < startTimes.length + offset; i++) {
            Instant currentPhaseStartTime = startTimes[i - offset];

            if (currentPhaseStartTime.isAfter(previousPhaseStartTime)) {
                if (currentTime.isAfter(currentPhaseStartTime)) {
                    currentPhase = getPhase(i);
                }
            } else {
                logger.error("[Rcx] StartTime for Stage '{}' is <= startTime for the previous stage.  Current: [{}];  Previous:[{}]",
                        i, currentPhaseStartTime, previousPhaseStartTime);
                String errorMessage = "Invalid date value: Phase '" + i + "' [" + currentPhaseStartTime + "] is <= previous stage "
                        + (i - 1) + " [" + previousPhaseStartTime + "].";
                // throw new IllegalArgumentException(errorMessage);
                logger.error("[Rcx] Default to PHASE_1_OFF:  [{}]", errorMessage);
                currentPhase = ParallelMode.PHASE_1_OFF;        // for safety, default to OFF.
                break;
            }
            previousPhaseStartTime = currentPhaseStartTime;
        }
        return currentPhase;
    }

    protected ParallelMode getPhase(int stage) {
        switch (stage) {
            case 2:
                return ParallelMode.PHASE_2_BUFFER;
            case 3:
                return ParallelMode.PHASE_3_PARALLEL_NON_RCX;
            case 4:
                return ParallelMode.PHASE_4_PARALLEL_RCX;
            case 5:
                return ParallelMode.PHASE_5_FINAL;
            case 1:
            default:
                return ParallelMode.PHASE_1_OFF;
        }
    }

    protected boolean getRcxPrimary(ParallelMode phase) {
        switch (phase) {
            case PHASE_4_PARALLEL_RCX:
            case PHASE_5_FINAL:
                return true;
            case PHASE_1_OFF:
            case PHASE_2_BUFFER:
            case PHASE_3_PARALLEL_NON_RCX:
            default:
                return false;
        }
    }
}
