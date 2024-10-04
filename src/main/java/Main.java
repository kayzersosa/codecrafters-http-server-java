import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String COMMAND_NOT_FOUND = ": command not found";
    private static final String PROMPT = "$ ";
    private static final String EXIT = "exit 0";
    private static final String ECHO = "echo";
    private static final String TYPE = "type";
    private static final String[] TYPECOMMAND = { "type echo", "type exit", "type cat", "type type" };

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
                  System.out.println(getEchoMessage(input));
                case "type":
                    if (command.equals(builtins.get(0)) ||
                        command.equals(builtins.get(1)) ||
                        command.equals(builtins.get(2))) {
                         System.out.println(command + " is a shell builtin");
                     } else {
                        System.out.println(command + ": not found");
                        path = getPath(command);
                        if (path != null) {
                            System.out.println(command + " is " + path);
                        } else {
                                System.out.println(command + ": not found");
                        }
                     }
                default:
                 System.out.println(input + ": command not found");
            }

        } 

    }

    private static Boolean isEcho(String input) {
        if (input.length() <= 4) {
            return false;
        }
        if (ECHO.equals(input.substring(0, 4)) && input.charAt(4) == ' ') {
            return true;
        }
        return false;
    }

    private static String getEchoMessage(String input) {
        if (input.length() > 4) {
            return input.substring(5);
        }
        return ECHO;
    }

    private static Boolean isType(String input) {

        try {
            if (input.substring(0, 4).equals("type") && input.charAt(4) == ' ') {
                return true;
            }

            if (input.equals(TYPECOMMAND[0]) || input.equals(TYPECOMMAND[1]) || input.equals(TYPECOMMAND[2])
                    || input.equals(TYPECOMMAND[3])) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static String getTypeMessage(String input) {

        if (input.equals(TYPECOMMAND[0])) {
            return "echo is a shell builtin";
        } else if (input.equals(TYPECOMMAND[1])) {
            return "exit is a shell builtin";
        } else if (input.equals(TYPECOMMAND[2])) {
            return "cat is /bin/cat";
        } else if (input.equals(TYPECOMMAND[3])) {
            return "type is a shell builtin";
        }
        return input.substring(5) + ": not found";
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

    private static void getTypePath(String input, String path) {
        if (path != null) {
            System.out.println(input.substring(5) + " is " + path);
        } else {
            System.out.println(input.substring(5) + ": not found");
        }
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
