/**
 * Thrown when a voter selects an option that is not one of the poll's
 * defined choices (e.g. a typo, or an option that was never added).
 */
public class InvalidOptionException extends VotingException {
    public InvalidOptionException(String message) {
        super(message);
    }
}
