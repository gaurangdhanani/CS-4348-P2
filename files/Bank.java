import java.util.*;
import java.util.concurrent.*;

public class Bank {

    // Shared semaphores
    static Semaphore doorSemaphore = new Semaphore(2);     // Only 2 customers in the bank
    static Semaphore managerSemaphore = new Semaphore(1);  // Only 1 teller can talk to manager
    static Semaphore safeSemaphore = new Semaphore(2);     // Only 2 tellers in the safe

    // Flag to indicate when bank is open
    static volatile boolean bankOpen = false;

    // Queue to store available tellers
    static BlockingQueue<Teller> readyTellers = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        int numTellers = 1;  // Start with 1 teller for testing
        int numCustomers = 3;

        // Start teller threads
        for (int i = 0; i < numTellers; i++) {
            Teller teller = new Teller(i);
            teller.start();
        }

        // Open the bank
        bankOpen = true;

        // Start customer threads
        for (int i = 0; i < numCustomers; i++) {
            Customer customer = new Customer(i);
            customer.start();
        }
    }

    // Simple Teller class
    static class Teller extends Thread {
        int id;

        public Teller(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println("Teller " + id + ": ready to serve.");
            try {
                readyTellers.put(this);  // Indicate this teller is ready
                while (true) {
                    // Just idle here for now — we’ll expand this later
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Teller " + id + ": interrupted.");
            }
        }
    }

    // Simple Customer class
    static class Customer extends Thread {
        int id;
        Random rand = new Random();

        public Customer(int id) {
            this.id = id;
        }

        public void run() {
            try {
                // Simulate arrival delay
                Thread.sleep(rand.nextInt(100));

                // Wait until bank is open
                while (!bankOpen) Thread.yield();

                // Try to enter the bank
                doorSemaphore.acquire();
                System.out.println("Customer " + id + ": enters the bank.");

                // Get a teller
                Teller teller = readyTellers.take();
                System.out.println("Customer " + id + " [Teller " + teller.id + "]: selects teller");

                // For now just finish interaction immediately
                System.out.println("Customer " + id + " [Teller " + teller.id + "]: done, leaving bank.");
                readyTellers.put(teller);  // Return teller to queue

                // Exit the bank
                doorSemaphore.release();

            } catch (InterruptedException e) {
                System.out.println("Customer " + id + ": interrupted.");
            }
        }
    }
}
