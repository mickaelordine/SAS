import businesslogic.CatERing;
import businesslogic.ServiceException;
import businesslogic.TaskException;
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

public class TestCatERing4b {
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

            System.out.println("TEST ELIMINA ASSEGNAMENTO COMPITO");
            Task task = CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks().get(4);
            System.out.println(task.getCookProcedure().toString() + "è completato ? " + task.isDone());
            Cook cook = task.getCooks().get(0);
            CatERing.getInstance().getKitchenTaskManager().deleteAssignedTask(task,cook);
            System.out.println("TEST ELIMINA ASSEGNAMENTO COMPITO correttamente eseguito");

        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (TaskException e) {
            System.out.println("Errore di logica nei Task");
        } catch (ServiceException e) {
            System.out.println("Errore di logica nel servizio");
        }
    }
}
