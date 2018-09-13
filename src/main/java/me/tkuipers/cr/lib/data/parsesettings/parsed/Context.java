package me.tkuipers.cr.lib.data.parsesettings.parsed;

import com.google.common.collect.Maps;
import me.tkuipers.cr.lib.data.parsesettings.exceptions.SyntaxParseException;
import me.tkuipers.cr.lib.file.parser.file.regexHandler.RegularExpressionCombiner;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Context implements IContextContainer {
  private String name;
  private Type type;
  private String regex;
  private List<Style> styles;
  private Map<String, Context> contexts;
  private String combinedRegex;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public List<Style> getStyles() {
    return styles;
  }

  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }

  public List<Context> getContexts() {
    return contexts.values().stream().collect(Collectors.toList());
  }

  @Override
  public String getContextsCombinedRegex() {
    return combinedRegex;
  }

  public void setContexts(List<Context> contexts) {
    this.contexts = mapRegexList(contexts);
    combinedRegex = RegularExpressionCombiner.combineContexts(contexts);
  }

  public void addContext(Context c){
    var set = contexts.values().stream().collect(Collectors.toList());
    set.add(c);
    this.contexts = mapRegexList(set);

  }

  private Map<String, Context> mapRegexList(Collection<Context> contexts) {
    Map<String, Context> map = Maps.newHashMap();
    for (var context:contexts) {
      map.put(context.getRegex(), context);

    }
    return map;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
