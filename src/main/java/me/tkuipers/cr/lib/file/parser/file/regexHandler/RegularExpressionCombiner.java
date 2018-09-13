package me.tkuipers.cr.lib.file.parser.file.regexHandler;

import me.tkuipers.cr.lib.data.parsesettings.parsed.Context;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RegularExpressionCombiner {

  public static String combineRegularExpresssions(Collection<String> expressionsToCombine){
    var out = new StringBuilder();
    var iter = expressionsToCombine.iterator();
    while(iter.hasNext()){
      var str = iter.next();
      out.append("(" + str.replaceAll("(^|[^\\\\])\\((?!\\?)", "$1(?:") + ")");
      if(iter.hasNext()){
        out.append("|");
      }
    }
    return out.toString();
  }

  public static String combineContexts(List<Context> contexts) {
    return combineRegularExpresssions(contexts.stream().map(m -> m.getRegex()).collect(Collectors.toList()));
  }
}
