Project 2 - CS 4348

# Mar 26 9:00am

Created git repository for the project.

# Apr 13 10:45am

To start the project, I'll import Java concurrency tools like Thread, Semaphore, and Random, and set up shared resources: 
semaphores for the bank door (2 customers max), manager (1 at a time), and safe (2 tellers max). I'll create Teller and 
Customer classes extending Thread, each with a unique ID. Initially, I'll implement a simple interaction where tellers 
announce they're ready, and customers enter the bank and approach an available teller, making sure to use semaphores or 
synchronized blocks to manage access and coordination. I'll begin testing with just one teller and a few customers to 
ensure the basic flow works before adding logic for withdrawals, manager and safe access, and scaling up to the full simulation.

# Apr 13 11:30am

I created a single Java file 'Bank.java' with shared semaphores to control access to the bank door, manager, and safe. 
I added basic 'Teller' and 'Customer' thread classes with unique IDs. In this initial version, one teller announces readiness, 
and a few customers enter the bank, pick an available teller, simulate a brief interaction, and leave. The program ran successfully, 
confirming that the basic thread creation and synchronization logic works as expected. I am done with the first session.

# Apr 13 11:35am

For the next session, I'll implement the full transaction flow between customers and tellers with proper synchronization and logging. 
Customers will randomly choose deposit or withdrawal, and tellers will handle the request, accessing the manager (for withdrawals) and 
the safe with delays. I'll use semaphores or blocking queues to coordinate steps like asking for the transaction, performing it, 
and signaling completion. All actions, especially waits and resource access, will be logged using the required format to match the project's output rules.

# Apr 13 12:40pm

For this session, I implemented the full customer-teller transaction flow with proper synchronization using semaphores and blocking queues. 
Customers randomly choose deposit or withdrawal, enter the bank, select a teller, and complete their transaction. Tellers handle manager 
and safe access with delays, and all actions are logged in the required format, matching the project's output rules. And done with the second session.

# Apr 13 12:50pm

For the final session, I'll scale the simulation to 3 tellers and 50 customers. I'll use a shared counter (protected by a lock) 
to track how many customers have completed their transactions. Once all 50 are done, each teller will print a message indicating 
they are leaving for the day and exit their loop. After all teller threads finish, the main thread will print "The bank closes for the day."
I'll ensure all thread synchronization and logging formats remain consistent with the project requirements.	

# Ape 13 2:00pm

In this final session, I scaled the simulation to 3 tellers and 50 customers, implemented full transaction logic with manager and safe access, and added a shutdown mechanism where tellers exit after serving all customers and the bank prints a closing message. While running the code, I encountered an error because `AtomicInteger` was used to track completed customers but wasn’t imported; this was resolved by adding `import java.util.concurrent.atomic.AtomicInteger` at the top of the file. The test was successful.

# Apr 13 5:00pm

I noticed an issue where the bank was opening before all the tellers were ready to serve. This caused customers to enter the bank and 
try to select a teller who hadn’t yet initialized, leading to unexpected behavior. To fix this, I implemented a synchronization mechanism 
using CountDownLatch. I created a static tellerReadyLatch with a count equal to the number of tellers (NUM_TELLERS). Each teller thread 
calls countDown() after announcing it's ready, and the main thread waits using tellerReadyLatch.await() before allowing any customers to proceed. 
After making this change, the issue was resolved, and the bank now correctly waits until all tellers are ready before opening to customers.
Finally, the bank program is working successfully and ready to submit

# Apr 13 5:15pm

Creating readme.txt file.