package as.learning_project.executor;

import as.learning_project.input.AcceptableInputs;
import as.learning_project.task.TaskDBConnector;
import as.learning_project.task.TaskStatus;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class CommandExecutor implements Closeable {
    private static final CommandExecutor instance = new CommandExecutor();
    private final TaskDBConnector taskDBConnector;

    private CommandExecutor() {
        taskDBConnector = TaskDBConnector.getInstance();
    }

    public static CommandExecutor getInstance() {
        return instance;
    }

    public boolean executeCommand(AcceptableInputs command, String[] arguments, String[] optionalArguments) {
        return switch (command.commandLineInputCommand) {
            case "add": yield add(arguments);
            case "delete": yield delete(arguments);
            case "update": yield update(arguments);
            case "mark-in-progress": yield markInProgress(arguments);
            case "mark-done": yield markDone(arguments);
            case "list": yield list(optionalArguments);
            default:
                yield false;
        };
    }

    private boolean add(String[] arguments){
        String taskDescription = String.join(" ", arguments);
        return taskDBConnector.createTask(taskDescription);
    }

    private boolean update(String[] arguments){
        try {
            int taskID = Integer.parseInt(arguments[0]);
            String taskDescription = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
            return taskDBConnector.updateDescription(taskDescription, taskID);
        } catch (NumberFormatException e) {
            System.err.println("Please enter task id");
            return false;
        }
    }

    private boolean delete(String[] arguments){
        try {
            int taskID = Integer.parseInt(arguments[0]);
            return taskDBConnector.deleteTask(taskID);
        } catch (NumberFormatException e){
            System.err.println("Please enter task id");
            return false;
        }
    }

    private boolean markInProgress(String[] arguments){
        try {
            int taskID = Integer.parseInt(arguments[0]);
            return taskDBConnector.updateStatus(TaskStatus.IN_PROGRESS, taskID);
        } catch (NumberFormatException e){
            System.err.println("Please enter task id");
            return false;
        }
    }

    private boolean markDone(String[] arguments){
        try {
            int taskID = Integer.parseInt(arguments[0]);
            return taskDBConnector.updateStatus(TaskStatus.DONE, taskID);
        } catch (NumberFormatException e){
            System.err.println("Please enter task id");
            return false;
        }
    }

    private boolean list(String[] optionalArguments){
        if(optionalArguments == null || optionalArguments.length == 0){
            taskDBConnector.readAllTasks();
            return true;
        }
        if(optionalArguments.length > 1){
            System.err.println("Too many arguments");
            return false;
        }
        String option = optionalArguments[0];
        option = option.toLowerCase();
        return switch (option) {
            case "todo": taskDBConnector.readTodosTasks();
                yield true;
            case "in-progress": taskDBConnector.readInProgressTasks();
                yield true;
            case "done": taskDBConnector.readDoneTasks();
                yield true;
            default:
                System.err.println("Invalid option");
                yield false;
        };
    }

    @Override
    public void close() {
        try {
            taskDBConnector.close();
        } catch (Exception e) {
            System.err.println("Error closing taskDBConnector");
        }
    }
}
