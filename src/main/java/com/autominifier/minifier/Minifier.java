package com.autominifier.minifier;

import com.autominifier.model.FilePathWrapper;

public interface Minifier {

   void compress(FilePathWrapper fileWrapper) throws Exception;
}
