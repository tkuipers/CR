package me.tkuipers.cr.lib.file.parser.file;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;

import java.io.File;
import java.util.ArrayDeque;
import java.util.List;
import java.util.regex.Pattern;


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
    var regexToMatch = stack.peek().getContextsCombinedRegex();
    var fullPattern = Pattern.compile(regexToMatch);
    var m = fullPattern.matcher(currentString);
    List<StyledString> strings = Lists.newArrayList();
    var lastMatchEndMarker = 0;
    while(m.find()) {
      //build the previous string style first
      if (lastMatchEndMarker != m.start()) {
        StyledString gapString = buildStyledString(currentString.substring(lastMatchEndMarker, m.start()), stack.peek());
        strings.add(gapString);
      }

      //determine the match context
      var subString = currentString.substring(m.start(), m.end());
      var context = determineRegexMatch(subString, stack.peek());

      if(context != null) {
        var stringToApplyTo = currentString.substring(m.start(), m.end());
        //trim the string to its remaining size
        currentString = currentString.substring(m.end());
        //apply style to match
        strings.add(buildStyledString(stringToApplyTo, context));

        //build the next style string based ont he context
        switch (context.getType()) {
          case PATTERN:
            break;
          case POP:
            if (stack.peek().getType() != Type.MAIN) {
              stack.pop();
              break;
            }
            throw new FileParseException("Cannot POP off of main context");
          case MULTILINE_PUSH:
            //should be the same behaviour as INLINE_PUSH
          case INLINE_PUSH:
            stack.push(context);
            break;
          //push this context on stack and apply it in the future
          //rebuild matcher
          default:
            //throw an exception here, this should never happen
            throw new FileParseException("An unexpected error occured");
        }

        regexToMatch = stack.peek().getContextsCombinedRegex();
        fullPattern = Pattern.compile(regexToMatch);
        m = fullPattern.matcher(currentString);
      } else{
        break;
      }
    }
    //add all of the leftover string
    if(currentString.length() != 0){
      strings.add(buildStyledString(currentString, stack.peek()));
    }


    return strings;
  }

  private StyledString buildStyledString(String s, IContextContainer currentContext) {
    var gapString = new StyledString();
    gapString.setStringValue(s);
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
