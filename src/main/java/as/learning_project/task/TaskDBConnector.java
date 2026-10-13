package as.learning_project.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskDBConnector implements AutoCloseable {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static TaskDBConnector instance;
    private static TaskDB taskDB;
    private static int universalTaskID = 0;
    private int currentTaskID;
    TaskDTO taskDTO;

    private TaskDBConnector(String dbPath) {
        taskDB = TaskDB.loadOrCreate(dbPath);
        universalTaskID = taskDB.tasks.size();
    }

    public static TaskDBConnector getInstance(String dbPath) {
        instance = new TaskDBConnector(dbPath);
        return instance;
    }

    private int getTaskID() {
        return taskDTO.getTaskID();
    }

    private String getDescription() {
        return taskDTO.getDescription();
    }

    private TaskStatus getStatus() {
        return taskDTO.getStatus();
    }

    private String getCreatedAt() {
        return taskDTO.getCreatedAt().format(formatter);
    }

    private String getUpdatedAt() {
        return taskDTO.getUpdatedAt().format(formatter);
    }

    public static TaskDBConnector getInstance() {
        return getInstance("");
    }

    public boolean createTask(String taskDescription) {
        try {
            LocalDateTime now = LocalDateTime.now();
            TaskDTO taskDTO = new TaskDTO(++universalTaskID, taskDescription, TaskStatus.TODO, now, now);
            this.taskDTO = taskDTO;
            taskDB.tasks.add(taskDTO);
            taskDB.tasksMap.get("TODO").add(universalTaskID);
            return true;
        } catch (Exception e) {
            System.err.println("Task could not be created");
            System.err.println(e.getMessage());
            return false;
        }
    }

    public void readAllTasks() {
        for(int i = 0; i < universalTaskID; i++) {
            readPerTask(i+1);
        }
    }

    public void readTodosTasks() {
        for(int taskID : taskDB.tasksMap.get("TODO")){
            readPerTask(taskID+1);
        }
    }

    public void readInProgressTasks() {
        for(int taskID : taskDB.tasksMap.get("IN_PROGRESS")){
            readPerTask(taskID+1);
        }
    }

    public void readDoneTasks() {
        for(int taskID : taskDB.tasksMap.get("DONE")){
            readPerTask(taskID+1);
        }
    }

    public void readPerTask(int taskID) {
        if(0 < taskID && taskID <= universalTaskID) {
            this.currentTaskID = taskID-1;
            this.taskDTO = taskDB.tasks.get(currentTaskID);
            if(taskDTO != null) {
                System.out.println("Task Details:");
                System.out.println("Task ID: " + getTaskID());
                System.out.println("Task Description: " + getDescription());
                System.out.println("Task Status: " + getStatus());
                System.out.println("Task Created At: " + getCreatedAt());
                System.out.println("Task Updated At: " + getUpdatedAt());
                System.out.println();
            }
            else  {
                System.err.println("Task ID - " + (currentTaskID+1) + " is deleted.");
            }
        }
        else {
            System.err.println("Invalid Task ID");
        }
    }

    public boolean updateDescription(String newDescription, int taskID) {
        if(0 < taskID && taskID <= universalTaskID) {
            this.currentTaskID = taskID-1;
            LocalDateTime currentTime = LocalDateTime.now();
            taskDTO =  taskDB.tasks.get(currentTaskID);
            if(taskDTO != null) {
                if (taskDTO.getUpdatedAt().isBefore(currentTime)) {
                    this.taskDTO.setDescription(newDescription);
                    System.out.println("Task Description updated successfully");
                    taskDTO.setUpdatedAt(currentTime);
                    return true;
                } else {
                    System.err.println("Task Status cannot be updated due to time not synchronized");
                }
            }
            else  {
                System.err.println("Task is deleted cannot be updated");
            }
        }
        else {
            System.err.println("Invalid Task ID");
        }
        return false;
    }

//    public void updateStatus(TaskStatus newStatus) {
//        this.updateStatus(newStatus, currentTaskID);
//    }

    public boolean updateStatus(TaskStatus newStatus, int taskID) {
        if(0 < taskID && taskID <= universalTaskID) {
            this.currentTaskID = taskID - 1;
            LocalDateTime currentTime = LocalDateTime.now();
            taskDTO =  taskDB.tasks.get(currentTaskID);
            if(taskDTO != null) {
                if (taskDTO.getUpdatedAt().isBefore(currentTime)) {
                    boolean statusChange = false;
                    if (this.taskDTO.getStatus().equals(TaskStatus.TODO)) {
                        if (newStatus.equals(TaskStatus.DONE) || newStatus.equals(TaskStatus.IN_PROGRESS)) {
                            this.taskDTO.setStatus(newStatus);
                            statusChange = true;
                        }
                    } else if (this.taskDTO.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                        if (newStatus.equals(TaskStatus.DONE)) {
                            this.taskDTO.setStatus(newStatus);
                            statusChange = true;
                        }
                    } else {
                        if (newStatus.equals(TaskStatus.IN_PROGRESS)) {
                            this.taskDTO.setStatus(newStatus);
                            statusChange = true;
                        }
                    }
                    if (statusChange) {
                        taskDB.tasksMap.get(taskDTO.getStatus().name()).remove(Integer.valueOf(currentTaskID));
                        taskDB.tasksMap.get(newStatus.name()).add(currentTaskID);
                        System.out.println("Task Status updated successfully");
                        taskDTO.setUpdatedAt(currentTime);
                        return true;
                    } else {
                        System.err.println("Invalid task status updation.");
                    }
                } else {
                    System.err.println("Task Status cannot be updated due to time not synchronized");
                }
            }
            else  {
                System.err.println("Task is deleted cannot be updated");
            }
        }
        else {
            System.err.println("Invalid Task ID");
        }
        return false;
    }

    public boolean deleteTask(int taskID) {
        if(0 < taskID && taskID <= universalTaskID) {
            this.currentTaskID = taskID - 1;
            if (taskDB.tasks.get(currentTaskID) != null){
                try {
                    taskDB.tasks.set(currentTaskID, null);
                    System.out.println("Task Deleted successfully");
                    return true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Invalid Task ID");
                }
            }
            else {
                System.err.println("Task ID " + currentTaskID + " is already deleted.");
            }
        }
        else {
            System.err.println("Invalid Task ID");
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        taskDB.close();
    }
}
