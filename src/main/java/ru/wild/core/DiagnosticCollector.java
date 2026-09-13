package ru.wild.core;

import ru.wild.util.io.DiagnosticRecordWriter;

public interface DiagnosticCollector {
   int handle();

   void handle(DiagnosticRecordWriter var1);
}
