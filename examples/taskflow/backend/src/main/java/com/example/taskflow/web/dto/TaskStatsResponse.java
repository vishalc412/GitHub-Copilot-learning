package com.example.taskflow.web.dto;

/**
 * Aggregate counts for the dashboard header.
 *
 * @param total      all tasks
 * @param todo       tasks not yet started
 * @param inProgress tasks currently being worked
 * @param done       completed tasks
 * @param completionRate fraction done, 0.0–1.0, rounded to two decimals
 */
public record TaskStatsResponse(
        long total,
        long todo,
        long inProgress,
        long done,
        double completionRate
) {

    /**
     * Builds a stats snapshot, deriving the completion rate safely.
     *
     * @param todo       count in TODO
     * @param inProgress count in IN_PROGRESS
     * @param done       count in DONE
     * @return populated stats, with a 0.0 rate when there are no tasks
     */
    public static TaskStatsResponse of(long todo, long inProgress, long done) {
        long total = todo + inProgress + done;
        double rate = total == 0 ? 0.0 : Math.round((double) done / total * 100.0) / 100.0;
        return new TaskStatsResponse(total, todo, inProgress, done, rate);
    }
}
