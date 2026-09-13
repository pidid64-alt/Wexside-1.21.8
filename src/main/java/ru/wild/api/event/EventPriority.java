package ru.wild.api.event;

public class EventPriority {
   public static final byte instance = 0;
   public static final byte data = 1;
   public static final byte context = 2;
   public static final byte config = 3;
   public static final byte state = 4;
   public static final byte[] cache = new byte[]{0, 1, 2, 3, 4};
}
