package service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import config.DatabaseConfig;
import model.Item;
import model.Order;
import org.bson.Document;
import org.bson.types.ObjectId;

public class InventoryService {
    public void createItem(Item item) {
        MongoDatabase database = DatabaseConfig.getDatabase();
        MongoCollection<Document> collection = database.getCollection("items");
        Document document = new Document();
        document.put("itemId", item.getItemId());
        document.put("itemName", item.getItemName());
        document.put("count", item.getCount());
        collection.insertOne(document);
    }

    public synchronized void processOrder(Order order) {
        MongoDatabase database = DatabaseConfig.getDatabase();
        MongoCollection<Document> collection = database.getCollection("items");
        Document query = new Document();
        query.put("itemId", order.getItemId());
        Document item = collection.find(query).first();

        if (item != null) {
            Integer itemCount = (Integer) item.get("count");
            if (itemCount >= order.getCount()) {
                itemCount -= order.getCount();

                // Save Item
                collection.updateOne(Filters.eq("itemId", order.getItemId()), Updates.set("count", itemCount));
            } else {
                // Inventory Not Enough
            }
        } else {
            // Invalid Item
        }
    }

}
