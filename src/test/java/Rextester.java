import java.util.Scanner;

class Rextester {
    public static void main(String[] args){
        var scanner = new Scanner(System.in);
        var input = scanner.next();
        if(input.endsWith("a") || input.endsWith("e")){
            System.out.println("Nome di femmina");
        }else if(input.endsWith("o")){
            System.out.println("Nome di maschio");
        }else {
            System.out.println("Mi arrendo!");
        }
    }
}