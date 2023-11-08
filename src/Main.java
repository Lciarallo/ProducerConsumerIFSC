import java.util.concurrent.Semaphore;

class ProdutorConsumidor {
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore espacoNoBuffer = new Semaphore(5);
    private final Semaphore itens = new Semaphore(0);

    private final Object[] buffer = new Object[5];
    private int in = 0;
    private int out = 0;

    public Object consumir() throws InterruptedException {
        itens.acquire(); // Aguarde até que haja itens no buffer
        mutex.acquire();

        Object item = buffer[out];
        

        mutex.release();
        espacoNoBuffer.release(); // Sinalize que há espaço disponível no buffer
        
        System.out.println("Consumindo: " + item);
        return item;
    }

    public void produzir(Object item) throws InterruptedException {
        aguardarLocal(); // Aguarde acesso local exclusivo
        espacoNoBuffer.acquire(); // Aguarde até que haja espaço disponível no buffer
        mutex.acquire();

        buffer[in] = item;
        in = (in + 1) % 5;

        System.out.println("Produzindo: " + item);

        mutex.release();
        itens.release(); // Sinalize que há um item no buffer
    }

    private void aguardarLocal() throws InterruptedException {
        // Aguarde até que haja espaço disponível no buffer
        while (espacoNoBuffer.availablePermits() == 0) {
            // O buffer está cheio, aguarde até que haja espaço
            Thread.sleep(100); // Aguarda por um tempo antes de verificar novamente
        }
    }
}

public class Main {
    public static void main(String[] args) {
        final int NUM_PRODUTORES = 3;
        final int NUM_CONSUMIDORES = 1;

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
                for (int j = 0; j < 50; j++) {
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