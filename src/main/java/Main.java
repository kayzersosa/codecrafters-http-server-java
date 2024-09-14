import java.util.Scanner;

public class Main {

    private static final String COMMAND_NOT_FOUND = ": command not found";    
    private static final String PROMPT = "$ ";  
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
         System.out.print("$ ");
        
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        do {
            System.out.println(input + COMMAND_NOT_FOUND);
            System.out.print(PROMPT);
            input = scanner.nextLine();
        } while (!input.matches("")); 

        scanner.close();
    }
}
