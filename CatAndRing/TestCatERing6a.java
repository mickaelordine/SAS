import businesslogic.CatERing;
import businesslogic.ServiceException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import businesslogic.user.Cook;
import javafx.collections.ObservableList;

public class TestCatERing6a {
    public static void main(String[] args) {
        try {
            /* System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();*/
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            ObservableList<EventInfo> ev = CatERing.getInstance().getEventManager().getEventInfo();
            EventInfo event = ev.get(0);
            System.out.println("GET SERVICE");
            ServiceInfo service = ev.get(0).getServices().get(1);
            System.out.println("OPEN FOGLIO RIEPILOGATIVO DEL SERVIZIO " + service.toString());
            CatERing.getInstance().getKitchenTaskManager().openSummarySheet(service, event);
            System.out.println(CatERing.getInstance().getKitchenTaskManager().getCurrentS().toString());

            System.out.println("TEST MODIFICA NOTA DI UN COMPITO");
            Task task = CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks().get(0);
            String desc = "nota aggiornata di nuovo";
            CatERing.getInstance().getKitchenTaskManager().modifyNote(task,desc);
            System.out.println("TEST MODIFICA NOTA DI UN COMPITO correttamente eseguito");

        } catch (UseCaseLogicException | ServiceException ex) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
