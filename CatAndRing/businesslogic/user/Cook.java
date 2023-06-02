package businesslogic.user;

import businesslogic.kitchenTask.Task;
import businesslogic.turn.KitchenTurn;
import businesslogic.turn.Turn;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Cook {
    private ArrayList<Turn> turns;
    private String name;
    private int id;


    public Cook(String name, int id){
        this.name = name;
        this.id = id;
        this.turns = new ArrayList<>();
    }

    public void setTurns(ArrayList<Turn> turns){
        this.turns.addAll(turns);
    }

    public ArrayList<Turn> getTurns(){
        return turns;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        String res = name + " con Id = " + id;
        return res;
    }

    //static method for DB


    public static ArrayList<Cook> loadALlCooks() {
        ArrayList<Cook> cooks = new ArrayList<Cook>();
        String query = "SELECT * FROM cooks";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Cook cook = new Cook(name,id);
                cooks.add(cook);
            }
        });
        return cooks;
    }

    public static ArrayList<Cook> loadALlCooksWithThisTask(Task task) {
        ArrayList<Cook> cooks = new ArrayList<Cook>();
        ArrayList<Cook> cooks_choosen = new ArrayList<>();
        ArrayList<Turn> turns_choosen = new ArrayList<>();
        String query = "SELECT * FROM cooks";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Cook cook = new Cook(name,id);
                cooks.add(cook);
            }
        });
        for (Cook cook : cooks) {
            query = "SELECT * FROM task_cook WHERE task_id = " + task.getId() + " AND cook_id = " + cook.getId();
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int id = rs.getInt("cook_id");
                    Cook cook1 = new Cook(cook.getName(), id);
                    cooks_choosen.add(cook1);
                }
            });
        }
        for(Cook cook : cooks_choosen){
            cook.turns = KitchenTurn.loadALlTurns();
        }
        for (Cook cook : cooks_choosen){
            for(Turn turn: cook.turns){
                query = "SELECT * FROM turn_cook WHERE cook_id = " + cook.getId() + " AND turn_id = " + turn.getId();
                PersistenceManager.executeQuery(query, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        int id = rs.getInt("turn_id");
                        KitchenTurn turn1 = new KitchenTurn(id,turn.isExhausted(),turn.getStartTime(),turn.getEndTime());
                        turns_choosen.add(turn1);
                    }
                });
            }
        }
        for(Cook cook : cooks_choosen){
            cook.turns = turns_choosen;
        }

        return cooks_choosen;
    }


}
