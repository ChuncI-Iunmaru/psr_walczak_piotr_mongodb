import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ConsoleUtils {
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static char getMenuOption() {
        System.out.println("\n1.[d]odaj przestępcę" +
                "\n2.[u]suń przestępcę" +
                "\n3.[a]ktualizuj przestępcę" +
                "\n4.pobierz po [i]dentyfikatorze" +
                "\n5.pobierz [w]szystkich" +
                "\n6.pobierz po imieniu i/lub [n]azwisku" +
                "\n7.[o]blicz statystki przestepstw" +
                "\n8.[z]akoncz");
        while (true) {
            try {
                System.out.print("Podaj operację: ");
                return scanner.nextLine().toLowerCase().charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                scanner.nextLine();
                System.out.println("Podano nieprawidłową operację.");
            }
        }
    }

    static String getFormattedDate() {

        System.out.println("Podaj date urodzenia w formacie DD-MM-YYYY");
        while (true) {
            try {
                String line = scanner.nextLine();
                LocalDate date = LocalDate.parse(line, format);
                return format.format(date);
            } catch (DateTimeParseException e) {
                System.out.println("Podaj prawidłową datę!");
            }
        }
    }

    static long calculateAge(String dob) {
        return java.time.temporal.ChronoUnit.YEARS.between(LocalDate.parse(dob, format), LocalDate.now());
    }

    static String getText(int minLength) {
        String tmp = "";
        do {
            tmp = scanner.nextLine();
            if (tmp.length() < minLength) System.out.println("Podaj minimum " + minLength + " znakow!");
        } while (tmp.length() < minLength);
        return tmp;
    }

    static String pickGender() {
        System.out.println("Wybierz płeć: ");
        int gender = 0;
        System.out.println("1.Mężczyzna\n2.Kobieta");
        while (gender < 1 || gender > 2) {
            try {
                gender = scanner.nextInt();
                scanner.nextLine();
                if (gender < 1 || gender > 2)  System.out.println("Podaj prawidłową wartość 1 lub 2!");
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Podaj prawidłową wartość 1 lub 2!");
            }
        }
        return (gender == 1)? "M" : "K";
    }

    static String getBuild() {
        System.out.println("Wybierz budowe ciała: ");
        int build = 0;
        Map<Integer, String> builds = new HashMap<>();
        builds.put(1, "szczupła");
        builds.put(2, "przeciętna");
        builds.put(3, "muskularna");
        builds.put(4, "otyła");
        for (int i: builds.keySet()) {
            System.out.println(i+". "+builds.get(i));
        }
        while (build < 1 || build > 4) {
            try {
                build = scanner.nextInt();
                scanner.nextLine();
                if (build < 1 || build > 4) System.out.println("Podaj prawidłową wartość 1-4!");
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Podaj prawidłową wartość 1-4!");
            }
        }
        return builds.get(build);
    }

    static int getHeight() {
        System.out.println("Podaj wzrost w cm:");
        int height = -1;
        while (height < 50 || height > 300) {
            try {
                height = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Podaj prawidłową wartość wzrostu!");
            }
        }
        return height;
    }

    static List<String> getListOfTexts(String delimiter, int minSize) {
        List<String> results;
        do {
            System.out.println("Podaj wartosci rozdzielajac '"+delimiter+"' ");
            String line = (minSize != 0) ? getText(1) : getText(0);
            results = Arrays.asList(line.split(delimiter));
            if (results.size() < minSize) System.out.println("Podaj przynajmniej " + minSize + " wartosci!");
        } while (results.size() < minSize);
        return results;
    }

    static void printCriminalProfile(Document d) {
        System.out.format("\n%-63s\n", "ID: " + d.get("_id"));
        System.out.format(String.format("%63s\n", "-").replace(' ', '-'));
        System.out.format("%-30s | %-30s\n", d.get("name") + " " + d.get("surname"), d.get("height") + " cm");
        System.out.format("%-30s | %-30s\n", "Płeć: " + d.get("gender"), "Budowa: " + d.get("build"));
        System.out.format("%-30s | %-30s\n", "Ur. " + d.get("dob") + " (" + calculateAge(d.get("dob").toString()) + ")", " ");
        System.out.format(String.format("%23s", "-").replace(' ', '-') + "Cechy szczególne" + String.format("%24s\n", "-").replace(' ', '-'));
        for (String s : d.getList("characteristics", String.class, Collections.emptyList())) {
            System.out.format("*%-63s\n", s);
        }
        System.out.format(String.format("%25s", "-").replace(' ', '-') + "Przestępstwa" + String.format("%26s\n", "-").replace(' ', '-'));
        for (String s : d.getList("crimes", String.class, Collections.emptyList())) {
            System.out.format("*%-63s\n", s);
        }
        System.out.format(String.format("%63s\n", "-").replace(' ', '-'));
        System.out.format("%-63s\n", d.get("notes"));
    }

}