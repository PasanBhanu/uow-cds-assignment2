package config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class DatabaseConfig {

    public static MongoClient mongoClient;

    public static void initializeDatabase() {
        mongoClient =  new MongoClient(new MongoClientURI("mongodb+srv://test:1wN2xK81mnHp9C4B@cluster0.lf8vd.mongodb.net/?retryWrites=true&w=majority"));
    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("inventory");
    }

    public static void disconnectDatabase() {
        mongoClient.close();
    }
}
