/**
 * Thrown for general invalid input that doesn't fit a more specific
 * exception: empty titles, fewer than two options, duplicate option
 * names, illegal characters, an unregistered voter trying to vote, etc.
 */
public class InvalidInputException extends VotingException {
    public InvalidInputException(String message) {
        super(message);
    }
}
