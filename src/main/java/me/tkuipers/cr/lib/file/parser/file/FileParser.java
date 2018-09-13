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
        var gapString = new StyledString();
        gapString.setStringValue(lineToParse.substring(prevContext, m.start()));
        gapString.setStyles(currentContext.getStyles());
        strings.add(gapString);
      }

      var s = new StyledString();
      var subString = lineToParse.substring(m.start(), m.end());
      s.setStringValue(subString);
      var styles = determineRegexMatch(subString, parserContext).getStyles();
      s.setStyles(styles);
      strings.add(s);

      prevContext = m.end();
    }
    if(prevContext != lineToParse.length()){
      var gapString = new StyledString();
      gapString.setStringValue(lineToParse.substring(prevContext));
      gapString.setStyles(parserContext.getStyles());
      strings.add(gapString);
    }
    return strings;
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

  public List<StyledString> sdfsdfs(IContextContainer parserContext, String lineToParse){
    var regexToMatch = parserContext.getContextsCombinedRegex();
    var fullPattern = Pattern.compile(regexToMatch);
    var m = fullPattern.matcher(lineToParse);
    List<StyledString> strings = Lists.newArrayList();
    var currentContext = parserContext;
    var prevContext = 0;
    while(m.find()){
      if(prevContext != m.start()){
        var gapString = new StyledString();
        gapString.setStringValue(lineToParse.substring(prevContext, m.start()));
        gapString.setStyles(currentContext.getStyles());
        strings.add(gapString);
      }

      var s = new StyledString();
      s.setStringValue(lineToParse.substring(m.start(), m.end()));
      s.setStyles(currentContext.getStyles());
      strings.add(s);

      prevContext = m.end();
    }
    if(prevContext != lineToParse.length()){
      var gapString = new StyledString();
      gapString.setStringValue(lineToParse.substring(prevContext));
      gapString.setStyles(currentContext.getStyles());
      strings.add(gapString);
    }

    return strings;
  }

  @Override
  public List<StyledString> parseFile(IContextContainer parserContext, File fileToParse) {
    return null;
  }

  public List<StyledString> getParsedData(){
    return null;
  }

}
