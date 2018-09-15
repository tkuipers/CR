package me.tkuipers.cr.lib.file.parser.file;

import com.google.common.collect.Lists;
import jdk.jshell.spi.ExecutionControl;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Context;
import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
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
    var currentString = lineToParse;
    var stack = new ArrayDeque<IContextContainer>();
    stack.push(parserContext);
    var regexToMatch = parserContext.getContextsCombinedRegex();
    var fullPattern = Pattern.compile(regexToMatch);
    var m = fullPattern.matcher(currentString);
    List<StyledString> strings = Lists.newArrayList();
    var prevContext = 0;
    while(m.find()){
      if(prevContext != m.start()){
        StyledString gapString = getStyledString(currentString, prevContext, m.start(), stack.peek());
        strings.add(gapString);
      }
      var subString = currentString.substring(m.start(), m.end());
      var context = determineRegexMatch(subString, stack.peek());
      if(context != null){
        stack.push(context);
      }

      StyledString s = null;
      if(context != null){
         s = getStyledString(currentString, m.start(), m.end(), context);
        stack.pop();
        if(context.getType() == Type.INLINE_PUSH){
          stack.push(context);
        }
        if(context.getType() == Type.POP){
          stack.pop();
        }
        prevContext = m.end();
      }else{
        var gapString = getStyledString(currentString, prevContext, null, stack.peek());
        strings.add(gapString);
        prevContext = currentString.length();
      }
      if(s != null){
        strings.add(s);
      }
    }
    if(prevContext != currentString.length()){
      var gapString = getStyledString(currentString, prevContext, null, stack.peek());
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
    return null;
  }

  @Override
  public List<StyledString> parseFile(IContextContainer parserContext, File fileToParse) {
    return null;
  }

  public List<StyledString> getParsedData(){
    return null;
  }

}
