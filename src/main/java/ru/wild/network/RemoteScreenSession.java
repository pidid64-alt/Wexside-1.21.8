package ru.wild.network;

import java.util.List;
import java.util.UUID;
import ru.wild.render.WorldRenderContext;

public interface RemoteScreenSession {
   void handle(List<PartyRoster.Point3d> var1);

   RemoteInputSink handle(UUID var1);

   RemoteFrameBuffer process(UUID var1);

   void handle(WorldRenderContext var1, RemoteCameraState var2, PartyRoster.Point3d var3, long var4, int var6);

   void handle();
}
