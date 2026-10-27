import java.util.Objects;

/**
 * Represents a person registered to vote in the system.
 *
 * Demonstrates ENCAPSULATION: fields are private and final, and are only
 * ever exposed through getters. Once a Voter object is built, it cannot
 * be mutated from outside the class.
 */
public class Voter {
    private final String voterId;
    private final String name;

    public Voter(String voterId, String name) {
        this.voterId = voterId;
        this.name = name;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getName() {
        return name;
    }

    /**
     * Two voters are considered the same if their ID matches, regardless
     * of letter case. This backs correct behaviour when Voter objects are
     * used as, or looked up via, HashMap keys.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voter)) return false;
        Voter other = (Voter) o;
        return voterId.equalsIgnoreCase(other.voterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId.toLowerCase());
    }

    @Override
    public String toString() {
        return "Voter{id='" + voterId + "', name='" + name + "'}";
    }
}
