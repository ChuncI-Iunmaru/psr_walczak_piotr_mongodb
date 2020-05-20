import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class AppMain {

    private static void insertCriminal(MongoCollection<Document> collection) {
        Document criminal = new Document();
        System.out.println("\nPodaj imię: ");
        criminal.append("name", ConsoleUtils.getText(1));
        System.out.println("Podaj nazwisko: ");
        criminal.append("surname", ConsoleUtils.getText(1));
        criminal.append("gender", ConsoleUtils.pickGender());
        criminal.append("build", ConsoleUtils.getBuild());
        criminal.append("height", ConsoleUtils.getHeight());
        System.out.println("Podaj cechy szczególne: ");
        criminal.append("characteristics", ConsoleUtils.getListOfTexts(";", 0));
        System.out.println("Podaj przestępstwa: ");
        criminal.append("crimes", ConsoleUtils.getListOfTexts(";", 1));
        System.out.println("Podaj uwagi: ");
        criminal.append("notes", ConsoleUtils.getText(0));
        criminal.append("dob", ConsoleUtils.getFormattedDate());
        collection.insertOne(criminal);
        System.out.println("Dodano przestępcę o id: " + criminal.get("_id"));
    }

    private static void removeCriminal(MongoCollection<Document> collection) {
        System.out.println("Podaj id profilu przestępcy: ");
        String id = ConsoleUtils.getText(1);
        if (collection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount() == 1) {
            System.out.println("Usunięto profil!");
        } else System.out.println("Usunięcie nie powiodło się!");;
    }

    private static void getAllCriminals(MongoCollection<Document> collection) {
        for (Document doc : collection.find()) {
            ConsoleUtils.printCriminalProfile(doc);
        }
    }

    private static void getById(MongoCollection<Document> collection) {
        System.out.println("Podaj id profilu przestępcy: ");
        String id = ConsoleUtils.getText(1);
        Document result = collection.find(eq("_id", new ObjectId(id))).first();
        if (result != null) {
            ConsoleUtils.printCriminalProfile(result);
        } else System.out.println("Nie znaleziono profilu!");
    }

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

        //Zachowaj dane
        //db.getCollection("criminals").drop();

        MongoCollection<Document> collection = db.getCollection("criminals");

        while (true) {
            switch (ConsoleUtils.getMenuOption()) {
                case 'd':
                    insertCriminal(collection);
                    break;
                case 'u':
                    removeCriminal(collection);
                    break;
                case 'a':
                    break;
                case 'i':
                    getById(collection);
                    break;
                case 'w':
                    getAllCriminals(collection);
                    break;
                case 'n':
                    break;
                case 'o':
                    break;
                case 'z':
                    mongoClient.close();
                    return;
                default:
                    System.out.println("Podano nieznaną operację. Spróbuj ponownie.");
            }
        }
    }
}
