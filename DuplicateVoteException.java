/**
 * Thrown when a voter who has already voted in a poll tries to vote in it again.
 * This is the exception that enforces the "one voter, one vote per poll" rule.
 */
public class DuplicateVoteException extends VotingException {
    public DuplicateVoteException(String message) {
        super(message);
    }
}
