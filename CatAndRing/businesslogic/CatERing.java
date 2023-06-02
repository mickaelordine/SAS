package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.kitchenTask.KitchenTaskManager;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.turn.TurnManager;
import businesslogic.user.UserManager;
import persistence.KitchenTaskPersistance;
import persistence.MenuPersistence;
import persistence.PersistenceManager;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private TurnManager turnManager;
    private KitchenTaskManager kitchenTaskManager;

    private MenuPersistence menuPersistence;
    private KitchenTaskPersistance kitchenTaskPersistance;

    private CatERing() {
        kitchenTaskManager = new KitchenTaskManager();
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        turnManager = new TurnManager();
        menuPersistence = new MenuPersistence();
        kitchenTaskPersistance = new KitchenTaskPersistance();
        menuMgr.addEventReceiver(menuPersistence);
        kitchenTaskManager.addEventReceiver(kitchenTaskPersistance);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

    public TurnManager getTurnManager() { return turnManager;}

    public KitchenTaskManager getKitchenTaskManager() {
        return kitchenTaskManager;
    }
}
