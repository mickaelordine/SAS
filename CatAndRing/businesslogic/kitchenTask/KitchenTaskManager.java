package businesslogic.kitchenTask;

import businesslogic.*;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.MenuEventReceiver;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.turn.Turn;
import businesslogic.turn.TurnManager;
import businesslogic.user.Cook;
import businesslogic.user.User;
import persistence.KitchenTaskPersistance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class KitchenTaskManager {
    private ArrayList<KitchenTaskEventReceiver> eventReceivers;
    private SummarySheet currentS;

    public SummarySheet getCurrentS() {
        return currentS;
    }

    public KitchenTaskManager(){
        eventReceivers = new ArrayList<>();
    }

    /*notify*/
    private void notifySheetCreated(SummarySheet summaryS){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateSheetCreated(summaryS);
        }
    }

    private void notifySheetDeleted(SummarySheet summaryS){

    }

    private void notifySheetOpen(SummarySheet summaryS){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateSheetOpen(summaryS);
        }
    }

    private void notifyAddedTask(ArrayList<Task> tasks){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateAddedTask(tasks);
        }
    }

    private void notifyDeletedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateDeletedTask(task);
        }
    }

    private void notifySortedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateSortedTask(task);
        }
    }

    private void notifyAddedNote(Task task){

    }

    private void notifyDeletedNote(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateDeletedNote(task);
        }
    }

    private void notifyEditedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateEditedTask(task);
        }
    }

    private void notifyEditedNote(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateModifiedNote(task);
        }
    }

    private void notifyAssignedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateAssignedTask(task);
        }
    }

    private void notifyExhaustedTurn(Turn turn){

    }

    private void notifyEditedAssignedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateModifiedAssignedTask(task);
        }
    }

    private void notifyDeletedAssignedTask(Task task){
        for (KitchenTaskEventReceiver ktListener : this.eventReceivers) {
            ktListener.updateDeletedAssignedTask(task);
        }
    }
    /*notify*/

    public SummarySheet createSummarySheet(EventInfo event, ServiceInfo service) throws UseCaseLogicException, EventException, ServiceException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || !service.getMenu().isOwner(user)){
            throw new UseCaseLogicException();
        }
        if(!service.getState().equals("programmed") || service.getMenu() == null || SummarySheet.hasSummarySheet(service)){
            throw new ServiceException();
        }
        this.currentS = service.createSummarySheet();
        notifySheetCreated(this.currentS);
        return this.currentS;
    }

    public ArrayList<Task> addTask(KitchenProcedure newkp){
        ArrayList<Task> newTaskList = currentS.AddTask(newkp);
        notifyAddedTask(newTaskList);
        return newTaskList;
    }

    public Task sortTask(Task task, int pos){
        currentS.sortTask(task,pos);
        notifySortedTask(task);
        return task;
    }

    public Task assignTask(Task task, Cook cook, Turn turn, int amount, int estimatedTime) throws TurnException {
        boolean flag = false;
        if(turn == null || turn.isExhausted()){
            throw new TurnException();
        }
        for (Turn t : cook.getTurns()){
            if(t.getId() == turn.getId()){ //
                flag = true;
            }
        }
        if(!flag){
            System.out.println("flag è rimasta false");
            throw new TurnException();
        }
        //TODO controlla il perchè non funzione questo controllo, probabilmente c'è da calcolare meglio la questione del tempo
        if((estimatedTime != 0 && turn.TimeLeft() < estimatedTime)){ //|| (LocalTime.now().getHour() - turn.getStartTime()/60)>0
            throw new TurnException();
        }

        Task newTask = currentS.assignTask(task,cook,turn,amount,estimatedTime);
        notifyAssignedTask(newTask);
        return newTask;
    }

    public Turn setTurnExhausted(Turn turn, boolean flag){
        TurnManager turnManager = CatERing.getInstance().getTurnManager();
        turn.setTurnExhausted(flag);
        turnManager.notifyExhaustedTurn(turn);
        return turn;
    }

    public Task addNote(Task task, String desc){
        task.setNote(desc);
        return task;
    }

    public SummarySheet openSummarySheet(ServiceInfo service, EventInfo event) throws UseCaseLogicException, ServiceException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || event.getOrganizer().getId() != user.getId()){
            throw new UseCaseLogicException();
        }
        if(service.getSummarySheet() == null){
            throw new ServiceException();
        }
        this.currentS = service.getSummarySheet();
        notifySheetOpen(this.currentS);
        return this.currentS;
    }


    public void deleteTask(Task task) throws TaskException {
        if(task.isDone()){
            throw new TaskException();
        }
        currentS.deleteTask(task);
        notifyDeletedTask(task);
    }

    public void modifyTask(Task task, int amount, int estimatedTime) throws TurnException {
        if(task.getTurn() != null && estimatedTime != 0 && task.getTurn().TimeLeft()<estimatedTime){
            throw new TurnException();
        }
        Task newTask = new Task();
        currentS.modifyTask(task,amount,estimatedTime);
        notifyEditedTask(newTask);
    }

    public void modifyAssignedTask(Task task, Turn turn) throws TurnException, TaskException {

        if(turn.isExhausted() || task.getEstimatedTime() > turn.TimeLeft()){
            throw new TurnException();
        }
        if(task.isDone()){
            throw new TaskException();
        }
        Task newTask = currentS.modifyAssigneTask(task,turn);
        notifyEditedAssignedTask(newTask);
    }

    public void deleteAssignedTask(Task task, Cook cook) throws TaskException {
        if(task.isDone()){
            throw new TaskException();
        }
        Task newTask = currentS.deleteAssignedTask(task,cook);
        notifyDeletedAssignedTask(newTask);
    }

    public void modifyNote(Task task, String desc) {
        Task newTask = currentS.modifyNote(task,desc);
        notifyEditedNote(newTask);
    }

    public void deleteNote(Task task) {
        Task newTask = currentS.deleteNote(task);
        notifyDeletedNote(newTask);
    }

    public void addEventReceiver(KitchenTaskEventReceiver rec) {
        eventReceivers.add(rec);
    }
}
