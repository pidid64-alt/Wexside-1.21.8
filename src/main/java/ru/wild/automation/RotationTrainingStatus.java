package ru.wild.automation;

public record RotationTrainingStatus(
   String text, boolean training, boolean loadingModel, long queuedRecords, long writtenRecords, long droppedRecords, long updatedAtMs
) {
   public static RotationTrainingStatus idle() {
      return new RotationTrainingStatus("AI idle", false, false, 0L, 0L, 0L, System.currentTimeMillis());
   }
}
