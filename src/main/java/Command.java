import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Command {
    private List<String> commands = null;
    private String dir = null;
    public Command() {
    }

    public String getDir() {
        this.dir = Path.of("").toAbsolutePath().toString();
        return dir;
    }
    public List<String> getCommands() {
        return commands();
    }

    public void echo(String parameter) {
        System.out.println(parameter);
    }

    public void exit(String parameter, String input) {
        if (parameter.equals("0")) {
            System.exit(0);
        } else {
            System.out.println(input + ": command not found");
        }
    }

    public void type(String parameter, List<String> builtins) {
        if (parameter.equals(builtins.get(0)) ||
                parameter.equals(builtins.get(1)) ||
                parameter.equals(builtins.get(2)) ||
                parameter.equals(builtins.get(3))) {
            System.out.println(parameter + " is a shell builtin");
        } else {
            String path = getPath(parameter);
            if (path != null) {
                System.out.println(parameter + " is " + path);
            } else {
                System.out.println(parameter + ": not found");
            }
        }
    }

    public void pwd() {
        System.out.println(dir);
    }

    public void cd(String parameter) {
        if (Files.isDirectory(Path.of(parameter))) {
            this.dir = parameter;
          } else {
            System.out.println("cd: " + parameter + ": No such file or directory");
        }
    }

    public void execute(String command, String parameter) throws Exception {
        String path = getPath(command);
        if (path != null) {
            String[] fullPath = new String[] { command, parameter };
            Process process = Runtime.getRuntime().exec(fullPath);
            process.getInputStream().transferTo(System.out);
        } else {
            System.out.println(command + ": command not found");
        }
    }

    private List<String> commands() {
        this.commands = new ArrayList<>();
        commands.add("exit");
        commands.add("echo");
        commands.add("type");
        commands.add("pwd");
        return commands;
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

}
