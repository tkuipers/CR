package me.tkuipers.cr.lib.file.parser.exceptions;

import me.tkuipers.cr.lib.file.parser.file.FileParser;

public class FileParseException extends RuntimeException {
  public FileParseException(String message){
    super(message);
  }
}
