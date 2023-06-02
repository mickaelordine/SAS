import businesslogic.*;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;
import businesslogic.menu.Menu;
import businesslogic.menu.Section;
import businesslogic.recipe.Recipe;
import businesslogic.turn.KitchenTurn;
import businesslogic.turn.Turn;
import businesslogic.user.Cook;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TestCatERing {
    public static void main(String[] args) {
        try {
            System.out.println("TEST DATABASE CONNECTION");
            PersistenceManager.testSQLConnection();
            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            System.out.println("\nTEST CREATE MENU");
            Menu m = CatERing.getInstance().getMenuManager().createMenu("Menu Pinco Pallino");

            System.out.println("\nTEST DEFINE SECTION");
            Section antipasti = CatERing.getInstance().getMenuManager().defineSection("Antipasti");
            Section primi = CatERing.getInstance().getMenuManager().defineSection("Primi");
            Section secondi = CatERing.getInstance().getMenuManager().defineSection("Secondi");
            System.out.println(m.testString());

            System.out.println("\nTEST GET EVENT INFO");
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            for (EventInfo e: events) {
                System.out.println(e);
                for (ServiceInfo s: e.getServices()) {
                    System.out.println("\t" + s);
                }
            }
            System.out.println("");

            System.out.println("\nTEST GET RECIPES AND INSERT ITEM IN SECTION");
            ObservableList<Recipe> recipes = CatERing.getInstance().getRecipeManager().getRecipes();
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(0), antipasti);
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(1), antipasti);
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(2), antipasti);
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(6), secondi);
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(7), secondi);
            System.out.println(m.testString());

            System.out.println("\nTEST INSERT FREE ITEM");
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(3));
            CatERing.getInstance().getMenuManager().insertItem(recipes.get(4));
            System.out.println(m.testString());

            System.out.println("\nTEST EDIT FEATURES");
            Map<String, Boolean> f = CatERing.getInstance().getMenuManager().getCurrentMenu().getFeatures();
            String[] fNames = f.keySet().toArray(new String[0]);
            boolean[] vals = new boolean[fNames.length];
            Arrays.fill(vals, true);
            CatERing.getInstance().getMenuManager().setAdditionalFeatures(fNames, vals);
            System.out.println(m.testString());

            System.out.println("\nTEST EDIT TITLE");
            CatERing.getInstance().getMenuManager().changeTitle("Titolo Nuovo");
            System.out.println(m.testString());

            System.out.println("\nTEST PUBLISH");
            CatERing.getInstance().getMenuManager().publish();
            System.out.println(m.testString());

            /*!!!!!!!!!!DA QUI IN POI E' IL MIO TEST!!!!!!!!!!!!!!!*/

            System.out.println("MIO TEST DA QUI IN POI");

            System.out.println("passo 1\n");
            System.out.println("GET EVENT");
            ObservableList<EventInfo> ev = CatERing.getInstance().getEventManager().getEventInfo();
            EventInfo event = ev.get(0);
            System.out.println("GET SERVICE");
            ServiceInfo service = ev.get(0).getServices().get(1);
            service.setMenu(m);
            service.setState("programmed");
            System.out.println("\nTEST CREATE SUMMARY SHEET");
            SummarySheet s = CatERing.getInstance().getKitchenTaskManager().createSummarySheet(event,service);
            System.out.println(s.toString());

            System.out.println("\npasso 1 finito");

            System.out.println("\npasso 2: AGGIUNGI COMPITO\n");
            CatERing.getInstance().getKitchenTaskManager().addTask(new Recipe("croissant"));
            System.out.println("ricetta croissant aggiunta correttamente come compito sul DB in fondo alla lista dei task\n");
            System.out.println("\npasso 2 finito\n");

            System.out.println("\npasso 3\n");
            System.out.println("Riordinando il croissant in posizione 1\n");
            CatERing.getInstance().getKitchenTaskManager().sortTask(CatERing.getInstance().getKitchenTaskManager().getCurrentS().getTasks().get(s.getTasks().size()-1),1);
            System.out.println("Croissant ordinato in posizione 1\n");
            System.out.println(CatERing.getInstance().getKitchenTaskManager().getCurrentS());

            System.out.println("\npasso 3 finito\n");

            System.out.println("\npasso 4, assegnamento compito a due chef, mario e luigi...\n");
            ArrayList<Cook> cooks = new ArrayList<>(Cook.loadALlCooks());
            ArrayList<Turn> turns = new ArrayList<>(KitchenTurn.loadALlTurnsOfThisCook2(cooks.get(0)));
            cooks.get(0).setTurns(turns);
            ArrayList<Turn> turns1 = new ArrayList<>(KitchenTurn.loadALlTurnsOfThisCook2(cooks.get(1)));
            cooks.get(1).setTurns(turns1);
            SummarySheet currentS = CatERing.getInstance().getKitchenTaskManager().getCurrentS();
            System.out.println("il cuoco " + cooks.get(0).toString() + "contiene i turni " + cooks.get(0).getTurns().toString());
            System.out.println("il cuoco " + cooks.get(1).toString() + "contiene i turni " + cooks.get(1).getTurns().toString());
            CatERing.getInstance().getKitchenTaskManager().assignTask(currentS.getTasks().get(0),cooks.get(0), cooks.get(0).getTurns().get(0), 30, 60);
            CatERing.getInstance().getKitchenTaskManager().assignTask(currentS.getTasks().get(3),cooks.get(1), cooks.get(1).getTurns().get(1), 30, 60);
            /*System.out.println("cuoco " + cooks.get(0).toString() + " asseganto al compito " + currentS.getTasks().get(0).getId() + " nel turno " + turns.get(0).getId());
            System.out.println("cuoco " + cooks.get(1).toString() + " asseganto al compito " + currentS.getTasks().get(3).getId() + " nel turno " + turns1.get(1).getId());
            */System.out.println("\npasso 4 finito\n");

            System.out.println("\n passo 5: segna turno saturo");
            CatERing.getInstance().getKitchenTaskManager().setTurnExhausted(turns.get(0), true);
            System.out.println("\n passo 5 finito");
            CatERing.getInstance().getKitchenTaskManager().setTurnExhausted(turns.get(0), false);

            System.out.println("\n passo 6: aggiugni nota al compito");
            CatERing.getInstance().getKitchenTaskManager().addNote(Task.loadSingleTask(currentS.getTasks().get(0)), "mi raccomando, fatelo in modo impeccabile, il vostro Chef");
            System.out.println("\n passo 6: finito");


        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (EventException e){
            System.out.println("Errore di logica degli eventi");
        } catch (TurnException e){
            System.out.println("Errore di logica dei turni");
        } catch (ServiceException e) {
            System.out.println("Errore di logica del servizio");
        }
    }
}
