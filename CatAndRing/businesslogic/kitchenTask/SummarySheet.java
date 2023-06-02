package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.turn.Turn;
import businesslogic.user.Chef;
import businesslogic.user.Cook;
import businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;

public class SummarySheet {
    private int id;
    private int service_id;
    private ArrayList<Task> tasks;
    private ArrayList<KitchenProcedure> newKitchenProcedures; //used when the org want to add something to the menu
    private User owner;



    public SummarySheet(){
        this.tasks = new ArrayList<Task>();
    }

    /*constructor*/
    public SummarySheet(Menu menu, int service_id){
        this.service_id = service_id;
        tasks = new ArrayList<>();
        ArrayList<KitchenProcedure> kpList = new ArrayList<>();
        for(Section section : menu.getSections()){
            for(MenuItem menuItem : section.getItems()){
                kpList.add(menuItem.getItemRecipe());
            }
        }
        for(MenuItem freeItem: menu.getFreeItems()){
            kpList.add(freeItem.getItemRecipe());
        }


        for(KitchenProcedure kp : kpList){
            Task task = new Task(kp);
            tasks.add(task);
        }
        SummarySheet.saveSummarySheet(this);
    }
    /*constructor*/

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTask(Task task) {
        this.tasks.add(task);
    }

    public ArrayList<Task> AddTask(KitchenProcedure kp){
        ArrayList<Task> newTaskList = new ArrayList<>();
        ArrayList<KitchenProcedure> newkps = new ArrayList<>();
        newkps.add(kp);
        //newkps.addAll(kp.getInstruction());

        for(KitchenProcedure newProcedures : newkps){
            newTaskList.add(new Task(newProcedures));
        }
        this.tasks.addAll(newTaskList);
        SummarySheet.saveTask(this,newTaskList.get(0));
        return newTaskList;
    }

    public void deleteTask(Task task){
        saveDeletedTask(task);
        tasks.remove(task);
    }

    public Task sortTask(Task task, int pos){
        Task newTask = new Task(task.getCookProcedure(),task.getCooks(),task.getTurn(),task.getEstimatedTime(),task.getAmount(),task.getId(),task.isDone());
        tasks.remove(task);
        tasks.add(pos,newTask);
        SummarySheet.orderTask();
        return newTask;
    }


    public Task assignTask(Task task, Cook cook, Turn turn, int amount, int estimatedTime) {
        if(amount == 0 && estimatedTime > 0){
            this.tasks.get(this.tasks.indexOf(task)).setEstimatedTime(estimatedTime);
        }else if(estimatedTime == 0 && amount>0){
            this.tasks.get(this.tasks.indexOf(task)).setAmount(amount);
        }
        if(turn != null){
            this.tasks.get(this.tasks.indexOf(task)).setTurn(turn);
        }
        this.tasks.get(this.tasks.indexOf(task)).addCook(cook);
        SummarySheet.saveAssignedTask2(task, cook);
        Task.saveAssignedTask2(task);
        return this.tasks.get(this.tasks.indexOf(task));
    }

    public Task modifyTask(Task task, int amount, int estimatedTime) {
        tasks.remove(task);
        Task newTask = null;
        if(amount == 0 && estimatedTime > 0){
            newTask = new Task(task.getCookProcedure(),task.getCooks(),task.getTurn(),estimatedTime,task.getAmount(),task.getId(), task.isDone());
            tasks.add(newTask);
        }else if(estimatedTime == 0 && amount > 0){
            newTask = new Task(task.getCookProcedure(),task.getCooks(),task.getTurn(),task.getEstimatedTime(),amount,task.getId(),task.isDone());
            tasks.add(newTask);
        }else if(amount > 0 && estimatedTime > 0){
            newTask = new Task(task.getCookProcedure(),task.getCooks(),task.getTurn(),estimatedTime,amount,task.getId(),task.isDone());
            tasks.add(newTask);
        }
        saveEditedTask(newTask);
        return newTask;
    }

    public Task modifyAssigneTask(Task task, Turn turn) {
        task.modifyAssignedTaskTurn(turn);
        Task newTask = new Task(task.getCookProcedure(),task.getCooks(),turn,task.getEstimatedTime(),task.getAmount(),task.getId(),task.isDone());
        return newTask;
    }

    public Task deleteAssignedTask(Task task, Cook cook) {
        Task newTask = task.deleteAssignedTask(cook);
        return newTask;
    }

    public Task modifyNote(Task task, String desc) {
        task.modifyNote(desc);
        return task;
    }

    public Task deleteNote(Task task) {
        return task.deleteNote();
    }


    @Override
    public String toString(){
        String result = "";

        result += "il summary sheet contiene i seguenti compiti:\n";
        for(Task task: tasks){
            result += task.getId() + " " + task.getCookProcedure().toString();
            result += "\n";
        }
        return result;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveSummarySheet(SummarySheet summaryS) {
        String SummaryInsert = "INSERT INTO catering.summaries (id, service_id, owner_id) VALUES (?, ?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(SummaryInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summaryS.id);
                ps.setInt(2, summaryS.service_id);
                ps.setInt(3, CatERing.getInstance().getUserManager().getCurrentUser().getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    summaryS.id = rs.getInt(1);
                }
            }
        });
        if (result[0] > 0) { // summary effettivamente inserito
            // salva le features
            tasksToDB(summaryS);

            //loadedSummarys.put(summaryS.id, summaryS); //used to save the object in the user interface
        }
    }

    public static void saveTask(SummarySheet s, Task task){
        String featureInsert = "INSERT INTO catering.tasks (turn_id, amount, estimatedTime, toDo, isDone, note, cookProcedure, summary_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PersistenceManager.executeBatchUpdate(featureInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, -1); //like a null value
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setBoolean(4, true);
                ps.setBoolean(5, false);
                ps.setString(6,"");
                ps.setString(7, task.getCookProcedure().toString());
                ps.setInt(8,s.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (count == 0) {
                    System.out.println("siamo passati di qui");
                    task.setId(rs.getInt(1));
                }
            }
        });
    }

    public static void tasksToDB(SummarySheet s){
        // Save Tasks
        String featureInsert = "INSERT INTO catering.tasks (turn_id, amount, estimatedTime, toDo, isDone, note, cookProcedure, summary_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PersistenceManager.executeBatchUpdate(featureInsert, s.tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, -1); //like a null value
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setBoolean(4, true);
                ps.setBoolean(5, false);
                ps.setString(6,"");
                ps.setString(7,s.tasks.get(batchCount).getCookProcedure().toString());
                ps.setInt(8,s.id);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                int id = rs.getInt(1);
                s.tasks.get(count).setId(id);
            }
        });
    }

    private static void orderTask() {
        //nothing todo, l'ordinamento andrà gestito localmemnte e non sul db, se si vuole però si può eliminare la tupla e reinserirla
    }

    private static void saveAssignedTask(Task task, ArrayList<Cook> cooks) {
        String SummaryInsert = "INSERT INTO catering.task_cook (task_id, cook_id) VALUES (?, ?);";
        for(int i = 0; i<cooks.size(); i++){
            int finalI = i;
            int[] result = PersistenceManager.executeBatchUpdate(SummaryInsert, cooks.size()-1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, task.getId());
                ps.setInt(2, cooks.get(finalI).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    task.setId(rs.getInt(1));
                }
            }
        });
        }
    }

    private static void saveAssignedTask2(Task task, Cook cook) {
        String SummaryInsert = "INSERT INTO catering.task_cook (task_id, cook_id) VALUES (?, ?);";
            int[] result = PersistenceManager.executeBatchUpdate(SummaryInsert, 1, new BatchUpdateHandler() {
                @Override
                public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                    ps.setInt(1, task.getId());
                    ps.setInt(2, cook.getId());
                }

                @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                    // should be only one
                    if (count == 0) {
                        task.setId(rs.getInt(1));
                    }
                }
            });
    }

    public static boolean hasSummarySheet(ServiceInfo service){
        boolean[] foundOne = new boolean[1];

        String query = "SELECT exists(SELECT * FROM summaries WHERE service_id = " + service.getId() + ")";
        PersistenceManager.executeQuery(query, rs -> foundOne[0] = rs.getBoolean(1));

        return foundOne[0];
    }

    public static void saveDeletedTask(Task task){
        String query = "DELETE FROM tasks WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
        query = "DELETE FROM task_cook WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }

    public static void saveEditedTask(Task task){
        String query = "UPDATE tasks SET amount = " + task.getAmount() + ", estimatedTime = "+ task.getEstimatedTime() + " WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }



    // STATIC METHODS FOR PERSISTENCE
}

