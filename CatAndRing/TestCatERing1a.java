import businesslogic.CatERing;
import businesslogic.EventException;
import businesslogic.ServiceException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuException;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import businesslogic.user.Cook;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.Map;

public class TestCatERing1a {
    public static void main(String[] args) {
        try {
            /* System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();*/
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());



            //load service
            //load event

            //DA QUI IN POI PARTE IL MIO TEST SULL'APERTURA DI UN SUMMARY SHEET

            ObservableList<EventInfo> ev = CatERing.getInstance().getEventManager().getEventInfo();
            EventInfo event = ev.get(0);
            System.out.println("GET SERVICE");
            ServiceInfo service = ev.get(0).getServices().get(1);

            System.out.println("OPEN FOGLIO RIEPILOGATIVO DEL SERVIZIO " + service.toString());
            CatERing.getInstance().getKitchenTaskManager().openSummarySheet(service, event);
            System.out.println(CatERing.getInstance().getKitchenTaskManager().getCurrentS().toString());

            System.out.println("i cuochi che fanno le task sono");
            for(Task task : CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks()){
                System.out.println(task.getCooks().toString() + " che svolge la task Num. " + task.getId());
                if(task.getTurn().getId() != -1){
                    System.out.println("nel turno ID. " + task.getTurn().getId());
                }
            }



        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (ServiceException e){
            System.out.println("Errore di logica del Servizio");
        }
    }
}
