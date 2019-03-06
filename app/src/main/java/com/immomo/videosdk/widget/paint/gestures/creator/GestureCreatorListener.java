package com.immomo.videosdk.widget.paint.gestures.creator;

import com.immomo.videosdk.widget.paint.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
