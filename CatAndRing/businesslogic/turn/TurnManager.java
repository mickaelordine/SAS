package businesslogic.turn;

import businesslogic.menu.MenuEventReceiver;

import java.util.ArrayList;

public class TurnManager {
    private ArrayList<TurnEventReceiver> eventReceivers;
    public TurnManager(){
        eventReceivers = new ArrayList<>();
    }

    public void notifyExhaustedTurn(Turn turn) {
        for(TurnEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateExhaustedTurn(turn);
        }
    }
}
