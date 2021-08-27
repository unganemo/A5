import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderClient extends AbstractOrderClient {

    private Controller controller;
    private KitchenServer kitchen;
    private Order order;

    public OrderClient(Controller controller, KitchenServer kitchen){
        this.controller = controller;
        this.kitchen = kitchen;
    }

    @Override
    public void submitOrder() {
        order = controller.getOrder();
        try {
            CompletableFuture<KitchenStatus> future = kitchen.receiveOrder(order);
            String status = future.get().name();
            controller.setStatus(status);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        startPollingServer(order.getOrderID());
    }

    @Override
    protected void startPollingServer(String orderId) {
        pollingTimer = new Timer();
        pollingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                while(true) {
                    try {
                        CompletableFuture<OrderStatus> status = kitchen.checkStatus(orderId);
                        controller.setStatus(status.get().name());
                        if (status.get().equals(OrderStatus.Ready)) {
                            pickUpOrder(orderId);
                            pollingTimer.cancel();
                            break;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 2000);
    }

    @Override
    protected void pickUpOrder(String orderId) {
        try {
            CompletableFuture<KitchenStatus> order = kitchen.serveOrder(orderId);
            String status = order.get().name();
            controller.setStatus(status);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); //hehe
        }
    }
}
