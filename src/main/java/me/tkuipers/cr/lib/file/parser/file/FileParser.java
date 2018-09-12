package me.tkuipers.cr.lib.file.parser.file;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;

import java.io.File;
import java.util.List;

public class FileParser implements IFileParser {

  private IContextContainer parserContext;
  private File fileToParse;

  public FileParser(IContextContainer parserContext, File fileToParse){
    if(fileToParse.isDirectory()){
      throw new FileParseException("Cannot parse file: " + fileToParse.getAbsolutePath() + " is a diectory.");
    }
    this.parserContext = parserContext;
    this.fileToParse = fileToParse;
  }

  public List<StyledString> getParsedData(){
    return null;
  }
}
