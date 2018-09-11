package me.tkuipers.cr.test.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.tkuipers.cr.lib.YamlFileParser;
import me.tkuipers.cr.lib.data.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsed.Type;
import me.tkuipers.cr.test.utils.TokenizerTestUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class YamlFileParserTests {

  @Test
  public void testSimpleSituation() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/SimpleSyntaxFile.yml"));

    var settings = parser.parseToSettings();

    assertEquals("json", settings.getName());
    assertEquals(1, settings.getFileExtensions().size());
    assertEquals(".json", settings.getFileExtensions().get(0));
    assertEquals(1, settings.getContexts().size());
    var context = settings.getContexts().get(0);
    assertEquals("main", context.getName());
    assertEquals(Type.PATTERN, context.getType());
    assertEquals("\\{", context.getRegex());
    assertEquals(1, context.getStyles().size());
    var style = context.getStyles().get(0);
    assertEquals("mainStyle", style.getName());
    assertEquals("#000000", style.getColor());
    assertEquals("#000001", style.getBackgrounColor());
    assertEquals(0, context.getContexts().size());
  }

  @Test
  public void testParseFileWithMultipleContexts() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/MultipleContextExample.yml"));

    var settings = parser.parseToSettings();

    var context = settings.getContexts().get(0);
    var innerContext = context.getContexts().get(0);
    assertEquals("childContext", innerContext.getName());
    assertEquals(Type.POP, innerContext.getType());
    assertEquals("\\}", innerContext.getRegex());
    assertEquals(1, innerContext.getStyles().size());
    assertEquals("mainStyle", innerContext.getStyles().get(0).getName());
    assertEquals(0, innerContext.getContexts().size());
  }

  @Test
  public void testInclude() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/IncludeExample.yml"));
    var settings = parser.parseToSettings();

    var context = settings.getContexts().stream().filter(m -> m.getName().equals("childContext2")).findFirst().orElseThrow();
    assertEquals("childContext2", context.getName());
    assertEquals(Type.PATTERN, context.getType());
    assertEquals("\\\\}", context.getRegex());
    assertEquals(1, context.getStyles().size());
    var style = context.getStyles().get(0);
    assertEquals("mainStyle", style.getName());
    assertEquals("#000000", style.getColor());
    assertEquals("#000001", style.getBackgrounColor());
    assertEquals(1, context.getContexts().size());
    var innerContext = context.getContexts().get(0);
    assertEquals("main", innerContext.getName());
    assertEquals(Type.PATTERN, innerContext.getType());
    assertEquals("\\{", innerContext.getRegex());
    assertEquals(1, innerContext.getStyles().size());
    assertEquals("mainStyle", innerContext.getStyles().get(0).getName());
    assertEquals(0, innerContext.getContexts().size());
  }

  @Test
  public void testMultipleStyle(){

  }

  @Test
  public void testComplexExample(){

  }

  @Test
  public void testPopHasInnerContextFail(){

  }

  @Test
  public void testInifiniteRecursionFail(){

  }

  @Test
  public void testUnknownIncludeFail(){

  }

  @Test
  public void testUnknownStyleFail(){

  }



}
