package com.autominifier.util;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class AnimatedGif extends Animation {

   public AnimatedGif(String filename, double durationMs) {
      GifDecoder decoder = new GifDecoder();
      decoder.read(filename);
      Image[] sequence = new Image[decoder.getFrameCount()];
      for (int i = 0; i < decoder.getFrameCount(); i++) {
         WritableImage wimg = null;
         BufferedImage bimg = decoder.getFrame(i);
         sequence[i] = SwingFXUtils.toFXImage(bimg, wimg);
      }

      super.init(sequence, durationMs);
   }
}