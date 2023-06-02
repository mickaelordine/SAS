package businesslogic.kitchenTask;

import java.util.ArrayList;

public interface KitchenTaskEventReceiver {

    public void updateSheetCreated(SummarySheet summaryS);

    public void updateSheetDeleted(SummarySheet summaryS);

    public void updateSheetOpen(SummarySheet summaryS);

    public void updateAddedTask(ArrayList<Task> task);

    public void updateDeletedTask(Task task);

    public void updateDeletedAssignedTask(Task task);

    public void updateDeletedNote(Task task);

    public void updateEditedTask(Task task);

    public void updateAssignedTask(Task task);

    public void updateSortedTask(Task task);

    public void updateModifiedNote(Task task);

    public void updateAddedNote(Task task);

    public void updateModifiedAssignedTask(Task task);
}
