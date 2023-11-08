import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class ProdutorConsumidor {
    private final BlockingQueue<Object> buffer = new ArrayBlockingQueue<>(10);

    public void produzir(Object item) throws InterruptedException {
        buffer.put(item); // Adicionar um item ao buffer
        System.out.println("Produzindo: " + item);
    }

    public Object consumir() throws InterruptedException {
        Object item = buffer.take(); // Consumir um item do buffer
        System.out.println("Consumindo: " + item);
        return item;
    }
}

public class Main {
    public static void main(String[] args) {
        final int NUM_PRODUTORES = 3;
        final int NUM_CONSUMIDORES = 3; // Use vários consumidores

        ProdutorConsumidor pc = new ProdutorConsumidor();

        for (int i = 0; i < NUM_PRODUTORES; i++) {
            Thread produtorThread = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        pc.produzir("Item " + j);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            produtorThread.start();
        }

        for (int i = 0; i < NUM_CONSUMIDORES; i++) {
            Thread consumidorThread = new Thread(() -> {
                while (true) {
                    try {
                        pc.consumir();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            consumidorThread.start();
        }
    }
}
