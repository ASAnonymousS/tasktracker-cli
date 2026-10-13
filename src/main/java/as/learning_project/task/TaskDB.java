package as.learning_project.task;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskDB implements AutoCloseable{
    ArrayList<TaskDTO> tasks;
    HashMap<String, ArrayList<Integer>> tasksMap;
    String dbPath;

    public TaskDB() {
        this.tasks = new ArrayList<>();
        this.tasksMap = new HashMap<>();
        this.tasksMap.put("TODO", new ArrayList<>());
        this.tasksMap.put("IN_PROGRESS", new ArrayList<>());
        this.tasksMap.put("DONE", new ArrayList<>());
    }

    private TaskDB(String dbPath) {
        this();
        this.dbPath = dbPath;
    }

    // 2. Public getters and setters required for Jackson serialization/deserialization
    public ArrayList<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public HashMap<String, ArrayList<Integer>> getTasksMap() {
        return tasksMap;
    }

    public void setTasksMap(HashMap<String, ArrayList<Integer>> tasksMap) {
        this.tasksMap = tasksMap;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public static TaskDB loadOrCreate(String dbPath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        if(dbPath == null || dbPath.equalsIgnoreCase("")) {
            String separator = String.valueOf(File.separatorChar);
            dbPath = Paths.get("")
                    .toAbsolutePath()
                    .toString()
                    .concat(separator)
                    .concat("db.json");
        }
        File file = new File(dbPath);
        TaskDB taskDB;
        if(!file.exists()) {
            try{
                file.createNewFile();
                taskDB = new TaskDB(dbPath);
                mapper.writeValue(file, taskDB);
                return taskDB;
            } catch (IOException e){
                System.err.println("The database file could not be created.");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        else if(file.length() == 0) {
            try {
                taskDB = new TaskDB(dbPath);
                mapper.writeValue(file, taskDB);
                return taskDB;
            } catch(IOException e)
            {
                System.err.println("The database file is corrupted.");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        try {
            return mapper.readValue(file, TaskDB.class);
        }
        catch (IOException e) {
            System.err.println("The database file could not be read.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private void save() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        File file = new File(dbPath);
        try {
            mapper.writeValue(file, this);
        }catch (IOException e){
            System.err.println("The database file could not be written.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void close(){
        save();
    }
}
