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
  private Context parent;
  public Context(){
    contexts = Maps.newHashMap();
  }

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
    System.out.println("\tRenewing regex");
    System.out.println("\tOld regex: " + combinedRegex);
    System.out.println("\tNew regex: " + RegularExpressionCombiner.combineContexts(contexts));
    combinedRegex = RegularExpressionCombiner.combineContexts(contexts);
  }

  public void addContext(Context c){
    var set = contexts.values().stream().collect(Collectors.toList());
    set.add(c);
    setContexts(set);
  }

  private Map<String, Context> mapRegexList(Collection<Context> contexts) {
    System.out.println("IN THIS METHOD");
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

  public Context getParent() {
    return parent;
  }

  public void setParent(Context parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {
    return "Context{" +
          "name='" + name + '\'' +
          ", type=" + type +
          ", combinedRegex='" + combinedRegex + '\'' +
          '}';
  }
}
