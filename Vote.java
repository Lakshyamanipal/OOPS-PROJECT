import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one successfully cast vote: who cast it, in which poll,
 * for which option, and when.
 *
 * Poll keeps a List<Vote> as a full audit trail, separate from the
 * aggregated HashMap<String,Integer> vote counts it uses for fast
 * result look-ups.
 */
public class Vote {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String voterId;
    private final String pollId;
    private final String selectedOption;
    private final LocalDateTime timestamp;

    public Vote(String voterId, String pollId, String selectedOption) {
        this.voterId = voterId;
        this.pollId = pollId;
        this.selectedOption = selectedOption;
        this.timestamp = LocalDateTime.now();
    }

    public String getVoterId() {
        return voterId;
    }

    public String getPollId() {
        return pollId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public String getFormattedTime() {
        return timestamp.format(FORMATTER);
    }

    @Override
    public String toString() {
        return "Vote[voter=" + voterId + ", poll=" + pollId
                + ", option=" + selectedOption + ", time=" + getFormattedTime() + "]";
    }
}
