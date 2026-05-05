package domain;

import enums.OperationTimelineType;

import java.time.LocalDateTime;
import java.util.Objects;


public class OperationTimeline {

    private LocalDateTime timestamp;
    private OperationTimelineType type;
    private String description;

    public OperationTimeline() {}

    // Getters and Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public OperationTimelineType getType() { return type; }
    public void setType(OperationTimelineType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Utility methods
    public boolean isCriticalEvent() {
        return type == OperationTimelineType.CASUALTY ||
                type == OperationTimelineType.ENGAGEMENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationTimeline)) return false;
        OperationTimeline that = (OperationTimeline) o;
        return Objects.equals(timestamp, that.timestamp) &&
                type == that.type &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, type, description);
    }

    @Override
    public String toString() {
        return "OperationTimeline{" +
                "timestamp=" + timestamp +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}