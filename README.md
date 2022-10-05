# RAFT Octo project

This pet project is a RAFT consensus implementation 
aiming at encreasing our comprenhension some core concepts about 
consensus in distributed environment.

## Running localy

### Prerequisit

Open JDK 17 is required.
Access to your console
Unix like environment.

### Running

Build with gradle

```bash
./gradlew build
```

Run the program

```bash
java -jar build/libs/RAFT-1.0-SNAPSHOT.jar

# Your should see following output, representing cluster state
┌───────────────┬───────────────┬───────────────┬───────────────┬──────────────┐
│    Leader     │     Node      │ Latest entry  │  Entry count  │ Current term │
├───────────────┼───────────────┼───────────────┼───────────────┼──────────────┤
│               │284720968      │95e70FCI2B     │8              │2008          │
├───────────────┼───────────────┼───────────────┼───────────────┼──────────────┤
│->             │189568618      │95e70FCI2B     │10             │2008          │
├───────────────┼───────────────┼───────────────┼───────────────┼──────────────┤
│               │793589513      │95e70FCI2B     │9              │2008          │
└───────────────┴───────────────┴───────────────┴───────────────┴──────────────┘
```
