import businesslogic.*;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import businesslogic.turn.KitchenTurn;
import businesslogic.turn.Turn;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class TestCatERing4a {
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

            System.out.println("TEST MODIFICA ASSEGNAMENTO COMPITO CON UN NUOVO TURNO");

            Task task = CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks().get(3);
            System.out.println(task.getCookProcedure().toString());
            ArrayList<Turn> turns = KitchenTurn.loadALlTurns();
            System.out.println("ORA SI EFFETTUA NEL TURNO " + turns.get(0).toString());
            CatERing.getInstance().getKitchenTaskManager().modifyAssignedTask(task,turns.get(1));

            System.out.println("TEST MODIFICA ASSEGNAMENTO COMPITO CON UN NUOVO TURNO COMPLETATO CORRETTAMENTE");

        } catch (UseCaseLogicException | ServiceException ex) {
            System.out.println("Errore di logica nello use case");
        } catch (TurnException e) {
            System.out.println("Errore di logica dei turni");
        } catch (TaskException e) {
            System.out.println("Errore di logica dei task");
        }
    }
}
