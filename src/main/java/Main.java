import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String PROMPT = "$ ";
    
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        
        String path = null;
        Scanner scanner = new Scanner(System.in);
        List<String> builtins = commands();
        
        while(true) {
            System.out.print(PROMPT);
            String input = scanner.nextLine();
            String[] str = input.split(" ");
            String command = str[0];
            String parameter = getParameter(str);

            switch (command) {
                case "exit":
                if (parameter.equals("0")) {
                    System.exit(0);
                  } else {
                    System.out.println(input + ": command not found");
                  }
                    break;
                case "echo":
                  System.out.println(parameter);
                    break;
                case "type":
                    if (parameter.equals(builtins.get(0)) ||
                        parameter.equals(builtins.get(1)) ||
                         parameter.equals(builtins.get(2))) {
                         System.out.println(parameter + " is a shell builtin");
                     } else {
                        path = getPath(parameter);
                        if (path != null) {
                            System.out.println(parameter + " is " + path);
                        } else {
                                System.out.println(parameter + ": not found");
                        }
                     }
                default:
                 System.out.println(input + ": command not found");
            }

        } 
        
    }

    private static String getPath(String parameter) {

        for (String path : System.getenv("PATH").split(":")) {
            Path fullPath = Path.of(path, parameter);
            if (Files.isRegularFile(fullPath)) {
                return fullPath.toString();
            }
        }
        return null;
    }

       private static List<String> commands() {
        List<String> commands = new ArrayList<>();
        commands.add("exit");
        commands.add("echo");
        commands.add("type");
        return commands;
    }

    private static String getParameter(String[] str) {
        String parameter = "";
        if (str.length > 2) {
            for (int i = 1; i < str.length; i++) {
                if (i < str.length - 1) {
                    parameter += str[i] + " ";
                } else {
                    parameter += str[i];
                }

            }
        } else if (str.length > 1) {
            parameter = str[1];
        }
        return parameter;
    }
}
