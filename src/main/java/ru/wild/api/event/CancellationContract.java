package ru.wild.api.event;

public interface CancellationContract {
   boolean handle();

   void process();
}
