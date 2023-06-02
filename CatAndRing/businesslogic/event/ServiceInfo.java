package businesslogic.event;

import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class ServiceInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu menu;
    private String state;
    private SummarySheet summarySheet;

    public int getId() {
        return id;
    }

    public SummarySheet createSummarySheet(){
        this.summarySheet = new SummarySheet(this.menu, this.id);
        return this.summarySheet;
    }

    public SummarySheet getSummarySheet(){
        loadSummarySheet(this);
        return this.summarySheet;
    }

    public String getState(){
        return this.state;
    }

    public void setState(String state){
        this.state = state;
    }

    public Menu getMenu(){
        return this.menu;
    }

    public void setMenu(Menu menu){
        this.menu = menu;
    }

    public ServiceInfo(String name) {
        this.name = name;
        this.state = "programmed";
    }


    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, service_date, time_start, time_end, expected_participants " +
                "FROM Services WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                result.add(serv);
            }
        });

        return result;
    }

    public static SummarySheet loadSummarySheet(ServiceInfo service){
        if(SummarySheet.hasSummarySheet(service)) {
            String query = "SELECT * from summaries WHERE service_id = " + service.getId();
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    SummarySheet result = new SummarySheet();
                    int id = rs.getInt("id");
                    ArrayList<Task> tasks = Task.loadAllTasks(id);
                    for (Task task : tasks) {
                        result.setTask(task);
                    }
                    service.summarySheet = result;
                }
            });


            return service.summarySheet;
        }else{
            return null;
        }
    }



}
