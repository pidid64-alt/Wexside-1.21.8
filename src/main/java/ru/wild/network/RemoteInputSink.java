package ru.wild.network;

public interface RemoteInputSink {
   void moveCursor(float var1, float var2);

   void press(float var1, float var2, int var3);

   void release(float var1, float var2, int var3);

   void scroll(float var1, float var2, double var3);

   void keyPress(int var1, int var2, int var3);

   void keyRelease(int var1, int var2, int var3);

   void type(char var1, int var2);
}
