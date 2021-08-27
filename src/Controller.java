import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Controller {

    private GenericRestaurantForm view;

    private KitchenServer kitchen;
    private OrderClient client;
    private Order order;

    public Controller(){
        view = new GenericRestaurantForm(this);
        view.Start();

        kitchen = new KitchenServer(this);
        client = new OrderClient(this, kitchen);

        
    }

    public void submitOrder() {
        client.submitOrder();
    }

    public void setOrder(ArrayList<OrderItem> items){
        order = new Order();

        for(OrderItem oi : items) {
            order.addOrderItem(oi);
        }
    }

    public Order getOrder() {
        return order;
    }


    public void setStatus(String s) {
        view.setStatus(s);
    }
}
