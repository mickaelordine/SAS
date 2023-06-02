import businesslogic.CatERing;
import businesslogic.ServiceException;
import businesslogic.TaskException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import javafx.collections.ObservableList;

public class TestCatERing2a {
    public static void main(String[] args) {
        try {
            /* System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();*/
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            //DA QUI IN POI IL MIO TEST
            ObservableList<EventInfo> ev = CatERing.getInstance().getEventManager().getEventInfo();
            EventInfo event = ev.get(0);
            System.out.println("GET SERVICE");
            ServiceInfo service = ev.get(0).getServices().get(1);

            System.out.println("OPEN FOGLIO RIEPILOGATIVO DEL SERVIZIO -> " + service.toString());
            CatERing.getInstance().getKitchenTaskManager().openSummarySheet(service, event);
            System.out.println(CatERing.getInstance().getKitchenTaskManager().getCurrentS().toString());
            System.out.println("\nTEST DELETE TASK\n");
            CatERing.getInstance().getKitchenTaskManager().deleteTask(CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks().get(0));

            System.out.println(CatERing.getInstance().getKitchenTaskManager().getCurrentS().toString());

        } catch (UseCaseLogicException | TaskException | ServiceException ex) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
