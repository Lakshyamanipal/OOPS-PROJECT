/**
 * Thrown when someone tries to vote in a poll that the admin has closed.
 */
public class PollClosedException extends VotingException {
    public PollClosedException(String message) {
        super(message);
    }
}
