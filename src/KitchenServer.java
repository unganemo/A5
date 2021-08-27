import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class KitchenServer extends AbstractKitchenServer {

    private Controller controller;

    public KitchenServer(Controller controller) {
        this.controller = controller;
        orderMap = new HashMap<>();
        threadPool = Executors.newFixedThreadPool(6);
    }

    //Här skapas trådarna
    @Override
    public CompletableFuture<KitchenStatus> receiveOrder(Order order) throws InterruptedException {
        CompletableFuture<KitchenStatus> future = new CompletableFuture<>();

        try{
            orderMap.put(order.getOrderID(), order);
            future.complete(KitchenStatus.Received);
            threadPool.submit(() -> cook(order));

        } catch (Error e) {
            future.complete(KitchenStatus.Rejected);
        }

        return future;
    }

    @Override
    public CompletableFuture<OrderStatus> checkStatus(String orderID) throws InterruptedException {
        CompletableFuture<OrderStatus> future = new CompletableFuture<>();

        sleepRandom(2000);
        future.complete(orderMap.get(orderID).getStatus());

        return future;
    }

    @Override
    public CompletableFuture<KitchenStatus> serveOrder(String orderID) throws InterruptedException {
        CompletableFuture<KitchenStatus> future = new CompletableFuture<>();
        sleepRandom(2000);
        future.complete(KitchenStatus.Served);
        orderMap.remove(orderID);
        return future;
    }

    @Override
    protected void cook(Order order) {
        try{
            orderMap.get(order.getOrderID()).setStatus(OrderStatus.Received);
            sleepRandom(5000);
            orderMap.get(order.getOrderID()).setStatus(OrderStatus.BeingPrepared);
            sleepRandom(5000);
            orderMap.get(order.getOrderID()).setStatus(OrderStatus.Ready);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void sleepRandom(int length) throws InterruptedException {
        long millis = (long) (Math.random() * length);
        Thread.sleep(millis);
    }
}
