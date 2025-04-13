import java.util.*;
import java.util.concurrent.*;

public class Bank {

    static final int NUM_TELLERS = 1;
    static final int NUM_CUSTOMERS = 3;

    static Semaphore doorSemaphore = new Semaphore(2);
    static Semaphore managerSemaphore = new Semaphore(1);
    static Semaphore safeSemaphore = new Semaphore(2);
    static volatile boolean bankOpen = false;

    static BlockingQueue<Teller> readyTellers = new LinkedBlockingQueue<>();
    static BlockingQueue<CustomerInteraction> customerQueue = new LinkedBlockingQueue<>();

    enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_TELLERS; i++) {
            new Teller(i).start();
        }

        bankOpen = true;

        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            new Customer(i).start();
        }
    }

    static class CustomerInteraction {
        int customerId;
        int tellerId;
        TransactionType type;
        Semaphore transactionRequested = new Semaphore(0);
        Semaphore transactionGiven = new Semaphore(0);
        Semaphore transactionDone = new Semaphore(0);
        Semaphore customerLeft = new Semaphore(0);

        public CustomerInteraction(int customerId, int tellerId, TransactionType type) {
            this.customerId = customerId;
            this.tellerId = tellerId;
            this.type = type;
        }
    }

    static class Teller extends Thread {
        int id;

        public Teller(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {
                    System.out.println("Teller " + id + " []: ready to serve");
                    System.out.println("Teller " + id + " []: waiting for a customer");

                    readyTellers.put(this);

                    CustomerInteraction interaction = customerQueue.take();
                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: serving a customer");

                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: asks for transaction");
                    interaction.transactionRequested.release();

                    interaction.transactionGiven.acquire();
                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: handling " + interaction.type.toString().toLowerCase() + " transaction");

                    if (interaction.type == TransactionType.WITHDRAWAL) {
                        System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: going to the manager");
                        managerSemaphore.acquire();
                        System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: getting manager's permission");
                        Thread.sleep(new Random().nextInt(26) + 5);
                        System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: got manager's permission");
                        managerSemaphore.release();
                    }

                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: going to safe");
                    safeSemaphore.acquire();
                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: enter safe");
                    Thread.sleep(new Random().nextInt(41) + 10);
                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: leaving safe");

                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: finishes " + interaction.type.toString().toLowerCase() + " transaction.");
                    System.out.println("Teller " + id + " [Customer " + interaction.customerId + "]: wait for customer to leave.");

                    interaction.transactionDone.release();
                    interaction.customerLeft.acquire();

                    safeSemaphore.release();
                }
            } catch (InterruptedException e) {
                System.out.println("Teller " + id + ": interrupted.");
            }
        }
    }

    static class Customer extends Thread {
        int id;
        TransactionType type = new Random().nextBoolean() ? TransactionType.DEPOSIT : TransactionType.WITHDRAWAL;

        public Customer(int id) {
            this.id = id;
        }

        public void run() {
            try {
                System.out.println("Customer " + id + " []: wants to perform a " + type.toString().toLowerCase() + " transaction");
                Thread.sleep(new Random().nextInt(101));

                while (!bankOpen) Thread.yield();

                System.out.println("Customer " + id + " []: going to bank.");
                doorSemaphore.acquire();

                System.out.println("Customer " + id + " []: entering bank.");
                System.out.println("Customer " + id + " []: getting in line.");
                System.out.println("Customer " + id + " []: selecting a teller.");

                Teller teller = readyTellers.take();

                System.out.println("Customer " + id + " [Teller " + teller.id + "]: selects teller");
                System.out.println("Customer " + id + " [Teller " + teller.id + "] introduces itself");

                CustomerInteraction interaction = new CustomerInteraction(id, teller.id, type);
                customerQueue.put(interaction);

                interaction.transactionRequested.acquire();

                System.out.println("Customer " + id + " [Teller " + teller.id + "]: asks for " + type.toString().toLowerCase() + " transaction");
                interaction.transactionGiven.release();

                interaction.transactionDone.acquire();

                System.out.println("Customer " + id + " [Teller " + teller.id + "]: leaves teller");
                System.out.println("Customer " + id + " []: goes to door");
                System.out.println("Customer " + id + " []: leaves the bank");

                interaction.customerLeft.release();
                doorSemaphore.release();
            } catch (InterruptedException e) {
                System.out.println("Customer " + id + ": interrupted.");
            }
        }
    }
}
