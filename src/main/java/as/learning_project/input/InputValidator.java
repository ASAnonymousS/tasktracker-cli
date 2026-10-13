package as.learning_project.input;

public class InputValidator {
    static AcceptableInputs[] acceptableInputs = {AcceptableInputs.ADD, AcceptableInputs.UPDATE, AcceptableInputs.DELETE, AcceptableInputs.MARK_IN_PROGRESS, AcceptableInputs.MARK_DONE, AcceptableInputs.LIST};
    private AcceptableInputs commandDetails;
    private String[] arguments;
    private String[] optionalArguments;

    public InputValidator(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("Please enter a command to be run");
            System.exit(1);
        }

        boolean isCommand = false;
        for(AcceptableInputs acceptableInput : acceptableInputs){
            if(acceptableInput.commandLineInputCommand.equals(args[0]) && !isCommand){
                isCommand = true;
                this.commandDetails = acceptableInput;
                break;
            }
        }
        if(!isCommand){
            System.err.println(args[0] + " is not a valid command");
            System.exit(1);
        }

        if(commandDetails.numberOfArguments != 0){
            try {
                arguments = new String[args.length - 1];
                System.arraycopy(args, 1, arguments, 0, args.length - 1);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        if(commandDetails.numberOfOptionalArguments != 0){
            try {
                optionalArguments = new String[commandDetails.numberOfOptionalArguments];
                System.arraycopy(args, 1 + commandDetails.numberOfArguments, optionalArguments, 0, commandDetails.numberOfOptionalArguments);
            }catch(ArrayIndexOutOfBoundsException e){
                optionalArguments = null;
            }
        }
    }

    public AcceptableInputs getCommandDetails() {
        return commandDetails;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String[] getOptionalArguments() {
        return optionalArguments;
    }
}
