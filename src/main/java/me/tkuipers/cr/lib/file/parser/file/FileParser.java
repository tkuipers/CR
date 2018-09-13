package me.tkuipers.cr.lib.file.parser.file;

import com.google.common.collect.Lists;
import jdk.jshell.spi.ExecutionControl;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Context;
import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileParser implements IFileParser {

  private IContextContainer parserContext;
  private File fileToParse;

  public FileParser(IContextContainer parserContext, File fileToParse){
    if(fileToParse.isDirectory()){
      throw new FileParseException("Cannot parse file: " + fileToParse.getAbsolutePath() + " is a directory.");
    }
    this.parserContext = parserContext;
    this.fileToParse = fileToParse;
  }

  @Override
  public List<StyledString> parseLine(IContextContainer parserContext, String lineToParse){
    var regexToMatch = parserContext.getContextsCombinedRegex();
    var fullPattern = Pattern.compile(regexToMatch);
    var m = fullPattern.matcher(lineToParse);
    List<StyledString> strings = Lists.newArrayList();
    var currentContext = parserContext;
    var prevContext = 0;
    while(m.find()){
      if(prevContext != m.start()){
        StyledString gapString = getStyledString(lineToParse, prevContext, m.start(), currentContext);
        strings.add(gapString);
      }
      var subString = lineToParse.substring(m.start(), m.end());
      var context = determineRegexMatch(subString, parserContext);
      var s = getStyledString(lineToParse, m.start(), m.end(), context);

      strings.add(s);

      prevContext = m.end();
    }
    if(prevContext != lineToParse.length()){
      var gapString = getStyledString(lineToParse, prevContext, null, parserContext);
      strings.add(gapString);
    }
    return strings;
  }

  private StyledString getStyledString(String lineToParse, Integer start, Integer end, IContextContainer currentContext) {
    var gapString = new StyledString();
    if(end != null){
      gapString.setStringValue(lineToParse.substring(start, end));
    }
    else{
      gapString.setStringValue(lineToParse.substring(start));
    }
    gapString.setStyles(currentContext.getStyles());
    return gapString;
  }


  private IContextContainer determineRegexMatch(String subString, IContextContainer parserContext) {
    for(var context : parserContext.getContexts()){
      var regex = context.getRegex();
      var pattern = Pattern.compile(regex);
      var m = pattern.matcher(subString);
      if(m.matches()){
        return context;
      }
    }
    throw new RuntimeException("There was a problem");
  }

  @Override
  public List<StyledString> parseFile(IContextContainer parserContext, File fileToParse) {
    return null;
  }

  public List<StyledString> getParsedData(){
    return null;
  }

}
