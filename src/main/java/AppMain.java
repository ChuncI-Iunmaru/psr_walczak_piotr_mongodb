import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

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

    private static void getByQuery(MongoCollection<Document> collection) {
        System.out.println("Podaj imię - '*' by wyszukiwać wszystkie");
        String name = ConsoleUtils.getText(1);
        System.out.println("Podaj nazwisko - '*' by wyszukiwać wszystkie");
        String surname = ConsoleUtils.getText(1);
        if (name.compareTo("*") == 0 && surname.compareTo("*") == 0) {
            getAllCriminals(collection);
        } else if (name.compareTo("*") != 0 && surname.compareTo("*") == 0) {
            for (Document d: collection.find(eq("name", name))) {
                ConsoleUtils.printCriminalProfile(d);
            }
        } else if (name.compareTo("*") == 0) {
            for (Document d: collection.find(eq("surname", surname))) {
                ConsoleUtils.printCriminalProfile(d);
            }
        } else {
            for (Document d: collection.find(and(eq("surname", surname), eq("name", name)))) {
                ConsoleUtils.printCriminalProfile(d);
            }
        }

    }

    private static void getCrimeStatistics(MongoCollection<Document> collection){
        System.out.println("Przetwarzanie danych z użyciem projection");
        Map<String, Integer> ageBrackets = new HashMap<>();
        ageBrackets.put("1. <20", 0);
        ageBrackets.put("2. 20-30", 0);
        ageBrackets.put("3. 31-50", 0);
        ageBrackets.put("4. 51-65", 0);
        ageBrackets.put("5. 65+", 0);
        int totalCrimes = 0;
        long age;
        int crimes;
        for (Document d : collection.find().projection(include("dob", "crimes"))) {
            age = ConsoleUtils.calculateAge(d.get("dob").toString());
            crimes = d.getList("crimes", String.class).size();
            totalCrimes += crimes;
            if (age < 20) ageBrackets.replace("1. <20", ageBrackets.get("1. <20")+crimes);
            if (age >= 20 && age <= 30) ageBrackets.replace("2. 20-30", ageBrackets.get("2. 20-30")+crimes);
            if (age >= 31 && age <= 50) ageBrackets.replace("3. 31-50", ageBrackets.get("3. 31-50")+crimes);
            if (age >= 51 && age <= 65) ageBrackets.replace("4. 51-65", ageBrackets.get("4. 51-65")+crimes);
            if (age > 65) ageBrackets.replace("5. 65+", ageBrackets.get("5. 65+")+crimes);
        }
        ConsoleUtils.printCrimeGraph(ageBrackets, totalCrimes);
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
                    getByQuery(collection);
                    break;
                case 'o':
                    getCrimeStatistics(collection);
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
