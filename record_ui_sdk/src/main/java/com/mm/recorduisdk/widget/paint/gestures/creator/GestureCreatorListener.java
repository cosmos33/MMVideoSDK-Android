package com.mm.recorduisdk.widget.paint.gestures.creator;

import com.mm.recorduisdk.widget.paint.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
