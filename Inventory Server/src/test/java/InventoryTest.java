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

        DatabaseConfig.disconnectDatabase();
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

        DatabaseConfig.disconnectDatabase();
    }

    @Test
    public void testUpdateItemInventory() {
        DatabaseConfig.initializeDatabase();

        Item item = new Item();
        item.setItemId("IT001");
        item.setCount(100);

        InventoryService service = new InventoryService();
        service.updateInventory(item);

        DatabaseConfig.disconnectDatabase();
    }
}
