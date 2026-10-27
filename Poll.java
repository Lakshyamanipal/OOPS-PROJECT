import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a single poll: a title, a fixed list of options, an
 * open/closed status, and the votes cast so far.
 *
 * Demonstrates:
 *  - ENCAPSULATION: internal collections are never handed out directly,
 *    only as unmodifiable views, so callers can't corrupt Poll's state.
 *  - HashMap:  voteCount maps each option -> number of votes (O(1) tally).
 *  - HashSet:  votedVoterIds tracks who has already voted (O(1) duplicate check).
 *  - Collections (List): options and voteHistory.
 *  - Custom exceptions for every invalid-vote scenario.
 */
public class Poll {
    private final String pollId;
    private final String title;
    private final List<String> options;
    private final Map<String, Integer> voteCount;   // option -> vote count
    private final Set<String> votedVoterIds;         // voter IDs who already voted
    private final List<Vote> voteHistory;            // full audit trail of votes
    private boolean open;

    public Poll(String pollId, String title, List<String> options) {
        this.pollId = pollId;
        this.title = title;
        this.options = new ArrayList<>(options);

        this.voteCount = new HashMap<>();
        for (String option : options) {
            voteCount.put(option, 0);
        }

        this.votedVoterIds = new HashSet<>();
        this.voteHistory = new ArrayList<>();
        this.open = true; // polls are open by default when created
    }

    // ---------- Getters (read-only access = encapsulation) ----------

    public String getPollId() {
        return pollId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public boolean isOpen() {
        return open;
    }

    public Map<String, Integer> getResults() {
        return Collections.unmodifiableMap(voteCount);
    }

    public List<Vote> getVoteHistory() {
        return Collections.unmodifiableList(voteHistory);
    }

    public int getTotalVotes() {
        return votedVoterIds.size();
    }

    public boolean hasVoted(String voterId) {
        return votedVoterIds.contains(voterId);
    }

    // ---------- State-changing methods ----------

    public void closePoll() {
        this.open = false;
    }

    public void openPoll() {
        this.open = true;
    }

    /**
     * Attempts to cast one vote in this poll.
     *
     * @throws PollClosedException    if the poll has been closed by the admin
     * @throws InvalidOptionException if selectedOption is not one of this poll's options
     * @throws DuplicateVoteException if voterId has already voted in this poll
     */
    public void castVote(String voterId, String selectedOption) throws VotingException {
        if (!open) {
            throw new PollClosedException("Poll '" + title + "' is closed. Voting is not allowed.");
        }
        if (!options.contains(selectedOption)) {
            throw new InvalidOptionException(
                    "'" + selectedOption + "' is not a valid option for poll '" + title + "'. "
                            + "Valid options: " + options);
        }
        if (votedVoterIds.contains(voterId)) {
            throw new DuplicateVoteException(
                    "Voter '" + voterId + "' has already voted in poll '" + title + "'.");
        }

        votedVoterIds.add(voterId);
        voteCount.put(selectedOption, voteCount.get(selectedOption) + 1);
        voteHistory.add(new Vote(voterId, pollId, selectedOption));
    }

    /**
     * Returns the winning option(s). More than one name is returned in
     * the event of a tie. An empty list means no votes have been cast yet.
     */
    public List<String> getWinners() {
        int max = Collections.max(voteCount.values());
        List<String> winners = new ArrayList<>();
        if (max == 0) {
            return winners; // no votes cast yet
        }
        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() == max) {
                winners.add(entry.getKey());
            }
        }
        return winners;
    }

    @Override
    public String toString() {
        return "Poll[" + pollId + "] \"" + title + "\" - " + (open ? "OPEN" : "CLOSED")
                + " - options=" + options + " - totalVotes=" + getTotalVotes();
    }
}
