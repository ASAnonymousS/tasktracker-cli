package as.learning_project;

import as.learning_project.executor.CommandExecutor;
import as.learning_project.input.AcceptableInputs;
import as.learning_project.input.InputValidator;

public class Main {
    public static void main(String[] args){
        InputValidator inputValidator = new InputValidator(args);
        AcceptableInputs acceptableInputs = inputValidator.getCommandDetails();
        String[] arguments = inputValidator.getArguments();
        String[] optionalArguments = inputValidator.getOptionalArguments();

        CommandExecutor commandExecutor = CommandExecutor.getInstance();
        if(!commandExecutor.executeCommand(acceptableInputs, arguments, optionalArguments)){
            System.out.println("Invalid command");
        }
        commandExecutor.close();
    }
}