import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class AppMain {

    public static void main(String[] args) {
        String user = "policjant";
        String password = "policjant";
        String host = "localhost";
        int port = 27017;
        String database = "policyjnaDB";

        String clientURI = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
        MongoClientURI uri = new MongoClientURI(clientURI);

        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase db = mongoClient.getDatabase(database);

        db.getCollection("criminals").drop();

        MongoCollection<Document> collection = db.getCollection("criminals");

        mongoClient.close();
    }
}
