SECURE ONLINE POLLING & RESULT ANALYSIS SYSTEM
================================================

HOW TO COMPILE AND RUN
-----------------------
1. Open a terminal in this folder (all 11 .java files together, no package).
2. Compile:      javac *.java
3. Run:          java Main

There are no external dependencies - only core Java (java.util, java.io, java.time).
Tested with OpenJDK 21, but it will run on any Java 8+ runtime.

FILE OVERVIEW
-------------
Main.java                    - console UI: Admin menu + Voter menu
VotingSystem.java             - manages all polls & voters, search, stats, save/load
Poll.java                     - one poll: options, vote tally, duplicate check, winner
Voter.java                    - a registered voter (id + name)
Vote.java                     - one cast vote, with timestamp (audit trail)
VotingException.java          - abstract parent of all custom exceptions
DuplicateVoteException.java   - voter tried to vote twice in the same poll
PollClosedException.java      - vote attempted on a closed poll
InvalidOptionException.java   - chosen option doesn't exist on the poll
PollNotFoundException.java    - poll ID does not exist
InvalidInputException.java    - empty/invalid fields, unregistered voter, etc.

WHAT TO TRY (for a demo or screenshots)
----------------------------------------
Admin menu:
  1) Create a poll with a title and several options
  2) View All Polls / View Poll Results & Winner
  5) Try closing a poll, then have a voter attempt to vote in it -> PollClosedException
  6) Search polls by keyword or ID
  7) View System Statistics
  8/9) Save to file, then Load - reopen the app and load again to prove persistence

Voter menu:
  1) Register with a Voter ID and name
  3) Cast a vote, then try voting again in the same poll -> DuplicateVoteException
  3) Try entering an option that doesn't exist -> InvalidOptionException

DATA FILE FORMAT
-----------------
Running "Save Data to File" writes a plain-text file (voting_data.txt) like:

  POLLCOUNTER,2
  VOTER,V001,Alice
  POLL,P1,Best Programming Language,true,Java|Python|C++|JavaScript
  VOTE,P1,V001,Java

This is intentionally human-readable (not serialized binary) so it's easy to
open, inspect, and explain in your report/viva.

OOP CONCEPTS DEMONSTRATED (for quick reference)
-------------------------------------------------
- Encapsulation      : private fields + getters everywhere; unmodifiable
                        views returned from Poll (getOptions, getResults, etc.)
- Classes & objects   : Poll, Voter, Vote, VotingSystem
- Constructors        : every class has one; VotingSystem also has an
                         implicit "empty" starting state
- Inheritance         : VotingException -> 5 specific subclasses
- Polymorphism        : Main catches "VotingException" once and handles
                         any of the 5 specific subtypes through it
- HashMap             : Poll.voteCount (option -> count),
                         VotingSystem.polls (id -> Poll),
                         VotingSystem.voters (id -> Voter)
- HashSet             : Poll.votedVoterIds (O(1) duplicate-vote check)
- Collections (List)  : Poll.options, Poll.voteHistory
- Exception handling  : 5 custom checked exceptions + try/catch throughout
                         Main, plus IOException handling for file I/O
