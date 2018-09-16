package me.tkuipers.cr.lib.file.parser.file;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.StyledString;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;

import java.io.File;
import java.util.ArrayDeque;
import java.util.List;
import java.util.regex.Matcher;
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
    ArrayDeque<IContextContainer> stack = initializeStack(parserContext);
    var m = buildNewMatcher(currentString, stack);
    List<StyledString> strings = Lists.newArrayList();
    var lastMatchEndMarker = 0;
    while(m.find()) {
      addGapStringToList(currentString, stack, m, strings, lastMatchEndMarker);
      var stringToApplyTo = currentString.substring(m.start(), m.end());
      IContextContainer context = getContextForSubString(stringToApplyTo, stack);

      if(context != null) {
        currentString = currentString.substring(m.end());
        strings.add(applyStylesToString(stringToApplyTo, context, true));
        performStackOperations(stack, context);

        m = buildNewMatcher(currentString, stack);
      } else{
        break;
      }
    }
    addRemainingString(currentString, stack, strings);
    return strings;
  }

  private ArrayDeque<IContextContainer> initializeStack(IContextContainer parserContext) {
    var stack = new ArrayDeque<IContextContainer>();
    stack.push(parserContext);
    return stack;
  }

  private Matcher buildNewMatcher(String currentString, ArrayDeque<IContextContainer> stack) {
    String regexToMatch;
    Pattern fullPattern;
    Matcher m;
    regexToMatch = stack.peek().getContextsCombinedRegex();
    fullPattern = Pattern.compile(regexToMatch);
    m = fullPattern.matcher(currentString);
    return m;
  }

  private void addGapStringToList(String currentString, ArrayDeque<IContextContainer> stack, Matcher m, List<StyledString> strings, int lastMatchEndMarker) {
    if (lastMatchEndMarker != m.start()) {
      StyledString gapString = applyStylesToString(currentString.substring(lastMatchEndMarker, m.start()), stack.peek(), false);
      strings.add(gapString);
    }
  }

  private IContextContainer getContextForSubString(String subString, ArrayDeque<IContextContainer> stack) {
    return determineRegexMatch(subString, stack.peek());
  }

  private StyledString applyStylesToString(String stringToApplyTo, IContextContainer context, boolean isActualMatch) {
    return buildStyledString(stringToApplyTo, context, isActualMatch);
  }

  private void performStackOperations(ArrayDeque<IContextContainer> stack, IContextContainer context) {
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
      case INLINE_PUSH:
      case MAIN:
        stack.push(context);
        break;

      default:
        throw new FileParseException("An unexpected error occured");
    }
  }

  private void addRemainingString(String currentString, ArrayDeque<IContextContainer> stack, List<StyledString> strings) {
    if(currentString.length() != 0){
      strings.add(applyStylesToString(currentString, stack.peek(), false));
    }
  }

  private StyledString buildStyledString(String s, IContextContainer currentContext, boolean isActualMatch) {
    var gapString = new StyledString();
    gapString.setStringValue(s);
    if(currentContext.getType() != Type.INLINE_PUSH || isActualMatch || currentContext.getInheritedStyles().size() == 0) {
      gapString.setStyles(currentContext.getStyles());
    }
    else{
      gapString.setStyles(currentContext.getInheritedStyles());
    }
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
