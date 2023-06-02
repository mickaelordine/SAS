package persistence;

import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;

import java.util.ArrayList;

public class KitchenTaskPersistance implements KitchenTaskEventReceiver {
    @Override
    public void updateSheetCreated(SummarySheet summaryS) {
    }

    @Override
    public void updateSheetDeleted(SummarySheet summaryS) {

    }

    @Override
    public void updateSheetOpen(SummarySheet summaryS) {

    }

    @Override
    public void updateAddedTask(ArrayList<Task> task) {

    }

    @Override
    public void updateDeletedTask(Task task) {

    }

    @Override
    public void updateDeletedAssignedTask(Task task) {

    }

    @Override
    public void updateDeletedNote(Task task) {

    }

    @Override
    public void updateEditedTask(Task task) {

    }

    @Override
    public void updateAssignedTask(Task task) {

    }

    @Override
    public void updateSortedTask(Task task) {

    }

    @Override
    public void updateModifiedNote(Task task) {

    }

    @Override
    public void updateAddedNote(Task task) {

    }

    @Override
    public void updateModifiedAssignedTask(Task task) {

    }
}
