import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.*;

class BoundedBuffer<T> {
    private final Queue<T> values = new ArrayDeque<>();
    private final int capacity;

    BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    synchronized void put(T value) throws InterruptedException {
        while (values.size() == capacity) {
            wait();
        }
        values.add(value);
        notifyAll();
    }

    synchronized T take() throws InterruptedException {
        while (values.isEmpty()) {
            wait();
        }
        T value = values.remove();
        notifyAll();
        return value;
    }
}

public class MiniBank {
    private static String prompt(Scanner scanner, String message, String defaultValue) {
        System.out.print(message + " (default '" + defaultValue + "'): ");
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            return line.isEmpty() ? defaultValue : line;
        }
        System.out.println(defaultValue);
        return defaultValue;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Practical 10: ThreadPools (ExecutorService) & BoundedBuffer (Producer-Consumer) ===");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("\n--- Part 1: Thread Pool Transaction Processing ---");
            int taskCount = Integer.parseInt(prompt(scanner, "Enter number of concurrent transactions", "6"));
            taskCount = Math.clamp(taskCount, 1, 20);

            ExecutorService pool = Executors.newFixedThreadPool(3);
            CountDownLatch done = new CountDownLatch(taskCount);

            System.out.println("Submitting " + taskCount + " tasks to FixedThreadPool(3)...");
            for (int i = 1; i <= taskCount; i++) {
                final int id = i;
                pool.submit(() -> {
                    System.out.println("  [" + Thread.currentThread().getName() + "] Executing transaction #" + id);
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    done.countDown();
                });
            }

            done.await();
            pool.shutdown();
            if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
            System.out.println("All thread pool transactions finished.");


            System.out.println("\n--- Part 2: BoundedBuffer Queue (Producer / Consumer) ---");
            int bufferCap = Integer.parseInt(prompt(scanner, "Enter buffer capacity", "2"));
            int itemCount = Integer.parseInt(prompt(scanner, "Enter items to produce/consume", "4"));

            BoundedBuffer<Integer> buffer = new BoundedBuffer<>(bufferCap);

            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= itemCount; i++) {
                        buffer.put(i);
                        System.out.println("  [Producer] Put item #" + i + " into buffer");
                        Thread.sleep(30);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 1; i <= itemCount; i++) {
                        int val = buffer.take();
                        System.out.println("  [Consumer] Took item #" + val + " from buffer");
                        Thread.sleep(60);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            producer.start();
            consumer.start();
            producer.join();
            consumer.join();

            System.out.println("\nProducer-Consumer execution completed successfully.");
        }
    }
}
