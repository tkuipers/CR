package me.tkuipers.cr.lib.file.parser.exceptions;

public class InvalidFolderException extends RuntimeException {
  public InvalidFolderException(String path, String context, String problem){
    super("Could not use " + path + " as " + context + "\n\tProblems: " + problem);
  }
}
