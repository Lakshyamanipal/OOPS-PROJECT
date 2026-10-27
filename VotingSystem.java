import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The central manager of the whole application. Main talks only to
 * VotingSystem; VotingSystem talks to Poll, Voter and Vote.
 *
 * Demonstrates HashMap (polls, voters), Collections (List, Set), and
 * ties the custom-exception hierarchy together.
 */
public class VotingSystem {
    private final Map<String, Poll> polls;     // pollId -> Poll
    private final Map<String, Voter> voters;   // lower-cased voterId -> Voter
    private int pollCounter;

    public VotingSystem() {
        this.polls = new HashMap<>();
        this.voters = new HashMap<>();
        this.pollCounter = 0;
    }

    // ================= Poll management =================

    public Poll createPoll(String title, List<String> options) throws InvalidInputException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Poll title cannot be empty.");
        }
        if (title.contains(",")) {
            throw new InvalidInputException("Poll title cannot contain a comma (',').");
        }
        if (options == null || options.size() < 2) {
            throw new InvalidInputException("A poll needs at least two options.");
        }

        Set<String> uniquenessCheck = new HashSet<>();
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                throw new InvalidInputException("Poll options cannot be empty.");
            }
            if (option.contains(",") || option.contains("|")) {
                throw new InvalidInputException("Options cannot contain ',' or '|' characters.");
            }
            if (!uniquenessCheck.add(option.trim().toLowerCase())) {
                throw new InvalidInputException("Duplicate option detected: '" + option + "'.");
            }
        }

        pollCounter++;
        String pollId = "P" + pollCounter;
        List<String> cleanOptions = new ArrayList<>();
        for (String option : options) {
            cleanOptions.add(option.trim());
        }

        Poll poll = new Poll(pollId, title.trim(), cleanOptions);
        polls.put(pollId, poll);
        return poll;
    }

    public Poll getPoll(String pollId) throws PollNotFoundException {
        if (pollId == null) {
            throw new PollNotFoundException("Poll ID cannot be empty.");
        }
        Poll poll = polls.get(pollId.trim());
        if (poll == null) {
            throw new PollNotFoundException("No poll found with ID '" + pollId + "'.");
        }
        return poll;
    }

    public Collection<Poll> getAllPolls() {
        return polls.values();
    }

    public List<Poll> searchPolls(String keyword) {
        List<Poll> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        String lower = keyword.trim().toLowerCase();
        for (Poll poll : polls.values()) {
            if (poll.getTitle().toLowerCase().contains(lower)
                    || poll.getPollId().equalsIgnoreCase(keyword.trim())) {
                results.add(poll);
            }
        }
        return results;
    }

    // ================= Voter management =================

    public Voter registerVoter(String voterId, String name) throws InvalidInputException {
        if (voterId == null || voterId.trim().isEmpty()) {
            throw new InvalidInputException("Voter ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Voter name cannot be empty.");
        }
        if (voterId.contains(",") || name.contains(",")) {
            throw new InvalidInputException("Voter ID and name cannot contain commas.");
        }

        Voter voter = new Voter(voterId.trim(), name.trim());
        voters.put(voter.getVoterId().toLowerCase(), voter);
        return voter;
    }

    public boolean isVoterRegistered(String voterId) {
        return voterId != null && voters.containsKey(voterId.trim().toLowerCase());
    }

    public Voter getVoter(String voterId) {
        return voterId == null ? null : voters.get(voterId.trim().toLowerCase());
    }

    public Collection<Voter> getAllVoters() {
        return voters.values();
    }

    // ================= Voting =================

    public void vote(String pollId, String voterId, String option) throws VotingException {
        if (!isVoterRegistered(voterId)) {
            throw new InvalidInputException(
                    "Voter '" + voterId + "' is not registered. Please register first.");
        }
        Poll poll = getPoll(pollId);
        poll.castVote(voterId.trim(), option.trim());
    }

    // ================= Statistics =================

    public String getSystemStatistics() {
        int openCount = 0;
        int totalVotes = 0;
        Poll busiest = null;

        for (Poll p : polls.values()) {
            if (p.isOpen()) {
                openCount++;
            }
            totalVotes += p.getTotalVotes();
            if (busiest == null || p.getTotalVotes() > busiest.getTotalVotes()) {
                busiest = p;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== SYSTEM STATISTICS =====\n");
        sb.append("Total polls created : ").append(polls.size()).append("\n");
        sb.append("Open polls          : ").append(openCount).append("\n");
        sb.append("Closed polls        : ").append(polls.size() - openCount).append("\n");
        sb.append("Registered voters   : ").append(voters.size()).append("\n");
        sb.append("Total votes cast    : ").append(totalVotes).append("\n");
        if (busiest != null && busiest.getTotalVotes() > 0) {
            sb.append("Most active poll    : \"").append(busiest.getTitle())
              .append("\" (").append(busiest.getTotalVotes()).append(" votes)\n");
        }
        return sb.toString();
    }

    // ================= File persistence =================
    // Simple, human-readable, comma-delimited text format:
    //   POLLCOUNTER,<n>
    //   VOTER,<id>,<name>
    //   POLL,<id>,<title>,<isOpen>,<opt1>|<opt2>|...
    //   VOTE,<pollId>,<voterId>,<option>

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("POLLCOUNTER," + pollCounter);

            for (Voter voter : voters.values()) {
                writer.println("VOTER," + voter.getVoterId() + "," + voter.getName());
            }

            for (Poll poll : polls.values()) {
                writer.println("POLL," + poll.getPollId() + "," + poll.getTitle() + ","
                        + poll.isOpen() + "," + String.join("|", poll.getOptions()));
                for (Vote v : poll.getVoteHistory()) {
                    writer.println("VOTE," + v.getPollId() + "," + v.getVoterId() + ","
                            + v.getSelectedOption());
                }
            }
        }
    }

    /**
     * Loads polls, voters and votes back from a file saved by saveToFile().
     *
     * Votes are replayed through Poll.castVote() rather than written
     * directly into the vote-count map. This rebuilds the HashSet of
     * "who already voted" correctly, and it re-uses (and therefore
     * re-demonstrates) the very same validation and exception logic
     * used for live voting. Every poll is kept OPEN during replay so
     * that historical votes on a poll that was later closed are still
     * accepted; the saved open/closed status is applied only afterwards.
     */
    public void loadFromFile(String filename) throws IOException {
        Map<String, Voter> loadedVoters = new HashMap<>();
        Map<String, Poll> loadedPolls = new HashMap<>();
        Map<String, Boolean> closedFlags = new HashMap<>();
        List<String[]> voteRecords = new ArrayList<>();
        int counter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                switch (parts[0]) {
                    case "POLLCOUNTER":
                        counter = Integer.parseInt(parts[1]);
                        break;
                    case "VOTER": {
                        Voter voter = new Voter(parts[1], parts[2]);
                        loadedVoters.put(voter.getVoterId().toLowerCase(), voter);
                        break;
                    }
                    case "POLL": {
                        String id = parts[1];
                        String title = parts[2];
                        boolean isOpen = Boolean.parseBoolean(parts[3]);
                        List<String> options = Arrays.asList(parts[4].split("\\|"));
                        Poll poll = new Poll(id, title, options); // created OPEN
                        loadedPolls.put(id, poll);
                        closedFlags.put(id, !isOpen);
                        break;
                    }
                    case "VOTE":
                        voteRecords.add(new String[]{parts[1], parts[2], parts[3]});
                        break;
                    default:
                        // unknown line type - ignore for forward compatibility
                        break;
                }
            }
        }

        for (String[] rec : voteRecords) {
            Poll poll = loadedPolls.get(rec[0]);
            if (poll == null) {
                continue;
            }
            try {
                poll.castVote(rec[1], rec[2]);
            } catch (VotingException e) {
                System.out.println("Warning: skipped an invalid saved vote (" + e.getMessage() + ")");
            }
        }

        for (Map.Entry<String, Boolean> entry : closedFlags.entrySet()) {
            if (entry.getValue()) {
                loadedPolls.get(entry.getKey()).closePoll();
            }
        }

        this.pollCounter = counter;
        this.voters.clear();
        this.voters.putAll(loadedVoters);
        this.polls.clear();
        this.polls.putAll(loadedPolls);
    }
}
