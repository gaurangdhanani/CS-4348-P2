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