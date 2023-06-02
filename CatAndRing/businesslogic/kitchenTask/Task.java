package businesslogic.kitchenTask;

import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.turn.KitchenTurn;
import businesslogic.turn.Turn;
import businesslogic.user.Cook;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Task {

    private int amount;
    private int estimatedTime;
    private boolean toDo;
    private boolean isDone;
    private String note;
    private Turn turn;
    private ArrayList<Cook> cooks;
    private KitchenProcedure cookProcedure;
    private int id;

    /*contructors*/
    public Task(KitchenProcedure kp){
        this.amount = 0;
        this.estimatedTime = 0;
        this.cooks = new ArrayList<Cook>();
        this.isDone = false;
        this.turn = new KitchenTurn();
        this.cookProcedure = kp;
    }

    public Task(KitchenProcedure kp, ArrayList<Cook> cooks, Turn turn, int estimatedTime, int amount, int id, boolean isDone){
        this.amount = amount;
        this.estimatedTime = estimatedTime;
        this.cooks = cooks;
        this.isDone = isDone;
        this.turn = turn;
        this.cookProcedure = kp;
        this.id = id;
    }

    public Task() {

    }

    /*contructors*/

    public Task deleteNote(){
        this.note = "";
        saveModifiedNote(this);
        return this;
    }

    public Task modifyNote(String desc){
        this.note = desc;
        saveModifiedNote(this);
        return this;
    }



    public Task assignTask(ArrayList<Cook> cooks, Turn turn, int amount, int estimatedTime){
        this.cooks = cooks;
        this.turn = turn;
        this.amount = amount;
        this.estimatedTime = estimatedTime;

        return this;
    }

    public Task deleteAssignedTask(Cook cook){
        saveDeletedAssignedTask(cook, this);
        this.cooks.remove(cook);
        return this;
    }

    public Task modifyAssignedTaskTurn(Turn turn){
        cooks = Cook.loadALlCooksWithThisTask(this);
        this.turn = turn;
        saveAssignedTask(this);
        cooks.removeIf(cook -> !cook.getTurns().contains(turn));
        return this;
    }




    /*
    * getter
    * */
    public int getAmount() {
        return amount;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public boolean isToDo() {
        return toDo;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getNote() {
        return note;
    }

    public Turn getTurn() {
        return turn;
    }

    public ArrayList<Cook> getCooks() {
        return cooks;
    }

    public KitchenProcedure getCookProcedure() {
        return cookProcedure;
    }

    /*
     * getter
     * */

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setToDo(boolean toDo) {
        this.toDo = toDo;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setNote(String note) {
        this.note = note;
        Task.saveNote(note, this);
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public void addCooks(ArrayList<Cook> cooks) {
        this.cooks.addAll(cooks);
    }

    public void addCook(Cook cook) {
        this.cooks.add(cook);
    }

    public void setCookProcedure(KitchenProcedure cookProcedure) {
        this.cookProcedure = cookProcedure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void saveNote(String note, Task task){
        String query = "UPDATE tasks SET note = '" + note + "' WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }

    public static ArrayList<Task> loadAllTasks(int id){
        ArrayList<Task> tasks = new ArrayList<Task>();
            String query = "SELECT * FROM tasks WHERE summary_id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String cp = rs.getString("cookProcedure");
                int estimatedTime = rs.getInt("estimatedTime");
                int amount = rs.getInt("amount");
                int id = rs.getInt("task_id");
                boolean done = rs.getBoolean("isDone");
                int turn_id = rs.getInt("turn_id");
                KitchenProcedure kp = new Recipe(cp);
                Turn turn1 = new KitchenTurn(turn_id,false, 0, 0);
                Task task = new Task(kp,null,turn1,estimatedTime,amount,id, done);
                tasks.add(task);
            }
        });
        for (Task task: tasks){
            task.cooks = Cook.loadALlCooksWithThisTask(task);
            for(Cook cook: task.cooks){
                cook.getTurns().addAll(KitchenTurn.loadALlTurnsOfThisCook2(cook));
            }
        }
        return tasks;
    }

    public static Task loadSingleTask(Task task){
        final int[] estimatedTime = new int[1];
        final int[] amount = new int[1];
        final int[] id = new int[1];
        final String[] cp = new String[30];
        final boolean[] flag = new boolean[1];
        String query = "SELECT * FROM tasks where task_id = " + "'" + task.getId() +"'";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cp[0] = rs.getString("cookProcedure");
                estimatedTime[0] = rs.getInt("estimatedTime");
                amount[0] = rs.getInt("amount");
                id[0] = rs.getInt("task_id");
                flag[0] = rs.getBoolean("isDone");
            }
        });
        KitchenProcedure kp = new Recipe(cp[0]);
        Task newtask = new Task(kp,null,null, estimatedTime[0], amount[0], id[0], flag[0]);
        return newtask;
    }

    public static void saveAssignedTask(Task task){
        for(Cook cook: task.cooks){
            if(!cook.getTurns().contains(task.turn)){
                String query = "DELETE FROM task_cook WHERE task_id = " + task.getId() + " AND cook_id = " + cook.getId();
                PersistenceManager.executeUpdate(query);
            }
        }
        String query = "UPDATE tasks SET turn_id = " + task.getTurn() + " WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }

    public static void saveAssignedTask2(Task task){
        String query = "UPDATE tasks SET turn_id = " + task.getTurn() + " WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }

    public static void saveDeletedAssignedTask(Cook cook, Task task){
        String query = "DELETE FROM task_cook WHERE task_id = " + task.getId() + " AND cook_id = " + cook.getId();
        PersistenceManager.executeUpdate(query);
    }

    private static void saveModifiedNote(Task task) {
        String query = "UPDATE tasks SET note = '" + task.getNote() + "' WHERE task_id = " + task.getId();
        PersistenceManager.executeUpdate(query);
    }
}
