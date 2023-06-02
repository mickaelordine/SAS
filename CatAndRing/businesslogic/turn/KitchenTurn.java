package businesslogic.turn;

import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.Cook;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class KitchenTurn extends Turn{
    private int id;
    private boolean isExhausted;
    private int startTime;
    private int endTime;

    public KitchenTurn(int id, boolean flag, int start, int end){
        this.id = id;
        this.isExhausted = flag;
        this.startTime = start;
        this.endTime = end;
    }

    public KitchenTurn(){

    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean isExhausted(){
        return this.isExhausted;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getEndTime() {
        return endTime;
    }

    @Override
    public int TimeLeft() {
        return endTime-startTime;
    }

    @Override
    public void setTurnExhausted(boolean flag) {
        KitchenTurn.saveExhaustedTurn(flag,this);
        this.isExhausted = flag;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String toString(){
        String res = "" + id;
        return res;
    }


    public boolean contains(Turn turn){
        return this.getId() == turn.getId();
    }


    // STATICS METHOD FOR DB

    public static ArrayList<Turn> loadALlTurns() {
        ArrayList<Turn> turns = new ArrayList<Turn>();
        String query = "SELECT * FROM turns";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                boolean flag = rs.getBoolean("isExhausted");
                int start = rs.getInt("startTime");
                int end = rs.getInt("endTime");
                KitchenTurn turn = new KitchenTurn(id,flag,start,end);
                turns.add(turn);
            }
        });
        return turns;
    }

    public static ArrayList<Turn> loadALlTurnsOfThisCook(Cook cook) {
        ArrayList<Turn> turns = new ArrayList<Turn>();
        ArrayList<Turn> turns_choosen = new ArrayList<Turn>();
        String query = "SELECT * FROM turns ";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                boolean flag = rs.getBoolean("isExhausted");
                int start = rs.getInt("startTime");
                int end = rs.getInt("endTime");
                KitchenTurn turn = new KitchenTurn(id,flag,start,end);
                turns.add(turn);
            }
        });
        for(Turn turn : turns){
            query = "SELECT * FROM turn_cook WHERE cook_id = " + cook.getId();
            System.out.println("cook id = " + cook.getId());
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int id = rs.getInt("turn_id");

                    KitchenTurn turn1 = new KitchenTurn(id,turn.isExhausted(),turn.getStartTime(),turn.getEndTime());
                    turns_choosen.add(turn1);
                }
            });
        }
        System.out.println("ChoosenTurn = " + turns_choosen.toString());
        return turns_choosen;
    }

    public static ArrayList<Turn> loadALlTurnsOfThisCook2(Cook cook) {
        ArrayList<Turn> turns = new ArrayList<Turn>();
        ArrayList<Turn> turns_choosen = new ArrayList<Turn>();
        String query = "SELECT * FROM turn_cook WHERE cook_id = " + cook.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("turn_id");
                KitchenTurn turn1 = new KitchenTurn(id,false,0,0);
                turns_choosen.add(turn1);
            }
        });
        for(Turn turn : turns_choosen){
            query = "SELECT * FROM turns WHERE id = " + turn.getId();
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    int id = rs.getInt("id");
                    boolean flag = rs.getBoolean("isExhausted");
                    int start = rs.getInt("startTime");
                    int end = rs.getInt("endTime");
                    KitchenTurn turn = new KitchenTurn(id,flag,start,end);
                    turns.add(turn);
                }
            });
        }
        return turns;
    }

    public static void saveExhaustedTurn(boolean flag, Turn turn){
        String query = "UPDATE turns SET isExhausted = " + flag + " WHERE id = " + turn.getId();
        PersistenceManager.executeUpdate(query);
    }

    // STATICS METHOD FOR DB
}
