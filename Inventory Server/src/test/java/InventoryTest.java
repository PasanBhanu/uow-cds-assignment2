import config.DatabaseConfig;
import model.Item;
import model.Order;
import org.junit.jupiter.api.Test;
import service.InventoryService;

public class InventoryTest {

    @Test
    public void testCreateItem() {
        DatabaseConfig.initializeDatabase();

        Item item = new Item();
        item.setItemId("IT001");
        item.setItemName("Red Boxes");
        item.setCount(5000);

        InventoryService service = new InventoryService();
        service.createItem(item);
    }

    @Test
    public void testProcessOrder() {
        DatabaseConfig.initializeDatabase();

        Order order = new Order();
        order.setOrderNumber("ORD001");
        order.setItemId("IT001");
        order.setCount(1);

        InventoryService service = new InventoryService();
        service.processOrder(order);
    }
}
