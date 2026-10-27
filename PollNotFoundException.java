/**
 * Thrown when a requested poll ID does not exist in the system.
 */
public class PollNotFoundException extends VotingException {
    public PollNotFoundException(String message) {
        super(message);
    }
}
