package service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import config.DatabaseConfig;
import model.Item;
import model.Order;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    public Map<String, String> createItem(Item item) {
        MongoDatabase database = DatabaseConfig.getDatabase();
        MongoCollection<Document> collection = database.getCollection("items");
        Document document = new Document();
        document.put("itemId", item.getItemId());
        document.put("itemName", item.getItemName());
        document.put("count", item.getCount());
        collection.insertOne(document);

        Map<String, String> response = new HashMap<>();
        response.put("status", "200");
        response.put("message", "Item created successfully.");
        return response;
    }

    public synchronized Map<String, String> processOrder(Order order) {
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
                Map<String, String> response = new HashMap<>();
                response.put("status", "200");
                response.put("message", "Order processed successfully.");
                return response;
            } else {
                // Inventory Not Enough
                Map<String, String> response = new HashMap<>();
                response.put("status", "403");
                response.put("message", "Order processed failed. Inventory not sufficient.");
                return response;
            }
        } else {
            // Invalid Item
            Map<String, String> response = new HashMap<>();
            response.put("status", "404");
            response.put("message", "Item not found.");
            return response;
        }
    }

    public synchronized Map<String, String> updateInventory(Item item) {
        MongoDatabase database = DatabaseConfig.getDatabase();
        MongoCollection<Document> collection = database.getCollection("items");
        Document query = new Document();
        query.put("itemId", item.getItemId());
        Document dbItem = collection.find(query).first();

        if (dbItem != null) {
            // Update Inventory
            Integer itemCount = (Integer) dbItem.get("count");
            itemCount += item.getCount();
            collection.updateOne(Filters.eq("itemId", item.getItemId()), Updates.set("count", itemCount));

            Map<String, String> response = new HashMap<>();
            response.put("status", "200");
            response.put("message", "Inventory updated successfully.");
            return response;
        } else {
            // Invalid Item
            Map<String, String> response = new HashMap<>();
            response.put("status", "404");
            response.put("message", "Item not found.");
            return response;
        }
    }

}
