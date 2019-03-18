package com.mm.sdkdemo.widget.paint.gestures.creator;

import com.mm.sdkdemo.widget.paint.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
