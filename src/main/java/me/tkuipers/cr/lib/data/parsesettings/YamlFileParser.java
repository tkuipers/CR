package me.tkuipers.cr.lib.data.parsesettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRStyle;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Context;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.data.parsesettings.exceptions.SyntaxParseException;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Style;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlFileParser {

  private Settings settings;
  private CRSettings crSettings;
  private ObjectMapper mapper;
  private Map<String, CRContext> contextMap;
  private Map<String, CRStyle> styleMap;

  public YamlFileParser(URL syntaxFile) throws IOException {
    this.mapper = new ObjectMapper(new YAMLFactory());
    this.crSettings = mapper.readValue(syntaxFile, CRSettings.class);
    this.contextMap = Maps.newHashMap();
  }

  public YamlFileParser(CRSettings settings) throws IOException {
    this.crSettings = settings;
    this.contextMap = Maps.newHashMap();
  }

  public boolean shouldParse(String filePath){
    String pathWithDot = "." + FilenameUtils.getExtension(filePath);
    if(filePath.equals(pathWithDot)){
      return false;
    }
    return crSettings.getFileExtensions().contains(pathWithDot);
  }

  public Settings getSettings(){
    return settings;
  }

  public void build() {
    contextMap = populateContextMap(crSettings.getContexts());
    styleMap = populateStyleMap(crSettings.getStyles());
    var settings = new Settings();
    settings.setName(crSettings.getName());
    settings.setFileExtensions(crSettings.getFileExtensions());
    settings.setContexts(buildContexts(crSettings.getContexts()));
    settings.setStyles(styleMap.values().stream().map(m -> buildStyle(m)).collect(Collectors.toList()));
    this.settings = settings;
  }

  private Map<String, CRStyle> populateStyleMap(List<CRStyle> styles) {
    Map<String, CRStyle> map = Maps.newHashMap();
    for(var style : styles){
      if(!map.containsKey(style.getName())){
        map.put(style.getName(), style);
      }
      else{
        throw new SyntaxParseException("Could not parse file as there are multiple styles with the same name: " + style.getName());
      }
    }
    return map;
  }

  private Map<String, CRContext> populateContextMap(List<CRContext> conList) {
    Map<String, CRContext> map = Maps.newHashMap();
    for(var context : conList){
      addToMap(map, context);
    }
    return map;
  }

  private void addToMap(Map<String, CRContext> map, CRContext context) {
    if(!map.containsKey(context.getName())) {
      map.put(context.getName(), context);
      if(context.getContexts().size() != 0){
        map.putAll(populateContextMap(context.getContexts()));
      }
    } else{
      throw new SyntaxParseException("Could not parse file as there are multiple contexts with the same name: " + context.getName());
    }
  }

  private List<Context> buildContexts(List<CRContext> conList) {
    return conList.stream().map(con -> buildContext(con)).collect(Collectors.toList());
  }

  private Context buildContext(CRContext con) {
    var context = new Context();
    context.setName(con.getName());
    context.setRegex(con.getRegex());
    context.setType(con.getType());
    context.setContexts(buildContexts(con));
    context.setStyles(buildStyles(con));
    validateContext(context);
    return context;
  }

  private void validateContext(Context context) {
    if(context.getType() == Type.POP && context.getContexts().size() != 0){
      throw new SyntaxParseException("Cannot have an inner context to a POP context type");
    }
  }

  private List<Style> buildStyles(CRContext con) {
    List<Style> list = Lists.newArrayList();

    for(var crStyle : con.getStyles()){
      if(styleMap.containsKey(crStyle)){
        list.add(buildStyle(styleMap.get(crStyle)));
      }
      else{
        throw new SyntaxParseException("Found unexpected style to include: " + crStyle);
      }
    }

    return list;
  }

  private Style buildStyle(CRStyle crStyle) {
    var style = new Style();
    style.setName(crStyle.getName());
    style.setColor(crStyle.getColor());
    style.setBackgrounColor(crStyle.getBackgrounColor());
    return style;
  }

  private List<Context> buildContexts(CRContext con) {
    List<Context> list = Lists.newArrayList();
    for(var crContext : con.getContexts()){
      list.add(buildContext(crContext));
    }
    for(var include : con.getInclude()){
      if(contextMap.containsKey(include)){
        list.add(buildContext(contextMap.get(include)));
      }
      else{
        throw new SyntaxParseException("Found unexpected context type to include: " + include);
      }
    }
    return list;
  }
}
