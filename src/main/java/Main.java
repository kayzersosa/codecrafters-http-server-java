import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String PROMPT = "$ ";
    
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        Scanner scanner = new Scanner(System.in);
        Command cmd = new Command();
        List<String> builtins = cmd.getCommands();
      
        while(true) {
            System.out.print(PROMPT);
            String input = scanner.nextLine();
            String[] str = input.split(" ");
            String command = str[0];
            String parameter = getParameter(str);

            switch (command) {
                case "exit":
                    cmd.exit(parameter, input);
                    break;
                case "echo":
                    cmd.echo(parameter);
                    break;
                case "type":
                     cmd.type(parameter, builtins);
                     break;  
                case "pwd":
                    cmd.pwd();
                    break; 
                case "cd":
                    cmd.cd(parameter);
                    break;
                default:
                   if(!parameter.equals("")) {
                       cmd.execute(command, parameter);
                    } else {
                      System.out.println(input + ": command not found");
                    }
            }

        } 
        
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
