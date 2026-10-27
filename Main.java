import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Entry point and console user interface for the
 * Secure Online Polling & Result Analysis System.
 *
 * Main only orchestrates menus and input/output; all real logic lives in
 * VotingSystem, Poll, Voter and Vote. Every action that can fail is wrapped
 * so that a VotingException (or an unexpected error) is reported cleanly
 * instead of crashing the program.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final VotingSystem system = new VotingSystem();
    private static final String DATA_FILE = "voting_data.txt";

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" SECURE ONLINE POLLING & RESULT ANALYSIS SYSTEM");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    adminMenu();
                    break;
                case "2":
                    voterMenu();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Admin Login");
        System.out.println("2. Voter Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    // ==================== ADMIN ====================

    private static void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1.  Create Poll");
            System.out.println("2.  Close a Poll");
            System.out.println("3.  Reopen a Poll");
            System.out.println("4.  View All Polls");
            System.out.println("5.  View Poll Results & Winner");
            System.out.println("6.  Search Polls");
            System.out.println("7.  View System Statistics");
            System.out.println("8.  Save Data to File");
            System.out.println("9.  Load Data from File");
            System.out.println("10. Back to Main Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": createPoll(); break;
                    case "2": setPollStatus(false); break;
                    case "3": setPollStatus(true); break;
                    case "4": viewAllPolls(); break;
                    case "5": viewResults(); break;
                    case "6": searchPolls(); break;
                    case "7": System.out.println(system.getSystemStatistics()); break;
                    case "8": saveData(); break;
                    case "9": loadData(); break;
                    case "10": back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (VotingException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private static void createPoll() throws InvalidInputException {
        System.out.print("Enter poll title: ");
        String title = scanner.nextLine();

        int n;
        try {
            System.out.print("How many options? ");
            n = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a whole number for the option count.");
        }

        List<String> options = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.print("  Option " + i + ": ");
            options.add(scanner.nextLine());
        }

        Poll poll = system.createPoll(title, options);
        System.out.println("Poll created successfully! ID = " + poll.getPollId());
    }

    private static void setPollStatus(boolean openStatus) throws PollNotFoundException {
        System.out.print("Enter Poll ID: ");
        String id = scanner.nextLine().trim();
        Poll poll = system.getPoll(id);
        if (openStatus) {
            poll.openPoll();
        } else {
            poll.closePoll();
        }
        System.out.println("Poll \"" + poll.getTitle() + "\" is now "
                + (openStatus ? "OPEN" : "CLOSED") + ".");
    }

    private static void viewAllPolls() {
        Collection<Poll> all = system.getAllPolls();
        if (all.isEmpty()) {
            System.out.println("No polls created yet.");
            return;
        }
        for (Poll p : all) {
            System.out.println(p);
        }
    }

    private static void viewResults() throws PollNotFoundException {
        System.out.print("Enter Poll ID: ");
        String id = scanner.nextLine().trim();
        Poll poll = system.getPoll(id);

        System.out.println("\nResults for \"" + poll.getTitle() + "\":");
        int total = poll.getTotalVotes();
        for (Map.Entry<String, Integer> e : poll.getResults().entrySet()) {
            double pct = total == 0 ? 0.0 : (e.getValue() * 100.0 / total);
            System.out.printf("  %-20s : %d votes (%.1f%%)%n", e.getKey(), e.getValue(), pct);
        }

        List<String> winners = poll.getWinners();
        if (winners.isEmpty()) {
            System.out.println("No votes cast yet.");
        } else if (winners.size() == 1) {
            System.out.println("Winner: " + winners.get(0));
        } else {
            System.out.println("It's a tie between: " + winners);
        }
    }

    private static void searchPolls() {
        System.out.print("Enter a keyword or Poll ID: ");
        String keyword = scanner.nextLine();
        List<Poll> results = system.searchPolls(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching polls found.");
        } else {
            for (Poll p : results) {
                System.out.println(p);
            }
        }
    }

    private static void saveData() {
        try {
            system.saveToFile(DATA_FILE);
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    private static void loadData() {
        try {
            system.loadFromFile(DATA_FILE);
            System.out.println("Data loaded from " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    // ==================== VOTER ====================

    private static void voterMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- VOTER MENU ---");
            System.out.println("1. Register as Voter");
            System.out.println("2. View Open Polls");
            System.out.println("3. Cast a Vote");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": registerVoter(); break;
                    case "2": viewOpenPolls(); break;
                    case "3": castVote(); break;
                    case "4": back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (VotingException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private static void registerVoter() throws InvalidInputException {
        System.out.print("Enter Voter ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        Voter v = system.registerVoter(id, name);
        System.out.println("Registered successfully: " + v);
    }

    private static void viewOpenPolls() {
        boolean any = false;
        for (Poll p : system.getAllPolls()) {
            if (p.isOpen()) {
                System.out.println(p);
                any = true;
            }
        }
        if (!any) {
            System.out.println("No open polls at the moment.");
        }
    }

    private static void castVote() throws VotingException {
        System.out.print("Enter your Voter ID: ");
        String voterId = scanner.nextLine();
        System.out.print("Enter Poll ID: ");
        String pollId = scanner.nextLine();

        Poll poll = system.getPoll(pollId); // throws PollNotFoundException
        System.out.println("Options: " + poll.getOptions());
        System.out.print("Enter your chosen option exactly as shown: ");
        String option = scanner.nextLine();

        system.vote(pollId, voterId, option);
        System.out.println("Vote cast successfully! Thank you, " + voterId + ".");
    }
}
