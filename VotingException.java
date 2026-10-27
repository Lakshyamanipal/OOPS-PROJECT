/**
 * Abstract base class for every custom exception in the Secure Online
 * Polling & Result Analysis System.
 *
 * Having one common parent lets the menu code catch a single type
 * (VotingException) when it only cares that "something about the poll
 * request was invalid", while still letting the system throw specific,
 * descriptive subclasses (DuplicateVoteException, PollClosedException,
 * etc.) wherever the precise reason matters.
 *
 * This is a deliberate demonstration of exception-class inheritance.
 */
public abstract class VotingException extends Exception {
    public VotingException(String message) {
        super(message);
    }
}
