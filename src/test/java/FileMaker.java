import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class FileMaker {
    private static final String[] SETS = {
            "RTX 2070",
            "RTX 2070 super",
            "RTX 2080 super",
            "RTX 2080ti",
            "RTX 2060 super",
            "RTX 2060",
    };
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var input = scanner.nextInt();
        var random = new Random();
        try(var writer = new BufferedWriter(new FileWriter(new File(System.getProperty("user.home"), "input.txt")))) {
            for(int x = 0; x < input; x++) {
                writer.write(SETS[random.nextInt(SETS.length)]);
                writer.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
