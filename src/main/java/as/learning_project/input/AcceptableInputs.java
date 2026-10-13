package as.learning_project.input;

public enum AcceptableInputs {
    ADD("add", 1, 0),
    UPDATE("update", 2, 0),
    DELETE("delete", 1, 0),
    MARK_IN_PROGRESS("mark-in-progress", 1, 0),
    MARK_DONE("mark-done", 1, 0),
    LIST("list", 0, 1);

    public final String commandLineInputCommand;
    public final int numberOfArguments;
    public final int numberOfOptionalArguments;

    AcceptableInputs(String commandLineInputCommand, int numberOfArguments, int numberOfOptionalArguments) {
        this.commandLineInputCommand = commandLineInputCommand;
        this.numberOfArguments = numberOfArguments;
        this.numberOfOptionalArguments = numberOfOptionalArguments;
    }
}
