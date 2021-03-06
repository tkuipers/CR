package me.tkuipers.cr.test.lib;

import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.exceptions.SyntaxParseException;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.file.FileParser;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SyntaxFileParserTests {

  @Test
  public void testSimpleSituation() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/SimpleSyntaxFile.yml"));
    parser.build();
    var settings = parser.getSettings();

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
    parser.build();
    var settings = parser.getSettings();

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
    parser.build();
    var settings = parser.getSettings();

    var context = settings.getContexts().get(0).getContexts().stream().filter(m -> m.getName().equals("childContext2")).findFirst().orElseThrow();
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
    assertEquals("childContext2", innerContext.getName());
    assertEquals(Type.PATTERN, innerContext.getType());
    assertEquals("\\\\}", innerContext.getRegex());
    assertEquals(1, innerContext.getStyles().size());
    assertEquals("mainStyle", innerContext.getStyles().get(0).getName());
    assertEquals(1, innerContext.getContexts().size());
  }

  @Test
  public void testMultipleStyle() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/MultipleStyleExample.yml"));
    parser.build();
    var settings = parser.getSettings();

    assertEquals(2, settings.getStyles().size());
    var style = settings.getStyles().get(0);
    assertEquals("mainStyle", style.getName());
    assertEquals("#000000", style.getColor());
    assertEquals("#000001", style.getBackgrounColor());

    style = settings.getStyles().get(1);
    assertEquals("childStyle", style.getName());
    assertEquals("#222222", style.getColor());
    assertEquals("#111111", style.getBackgrounColor());
  }

  @Test
  public void testComplexExample() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/ComplexExample.yml"));
    parser.build();
    var settings = parser.getSettings();

    assertEquals("json", settings.getName());
    assertEquals(1, settings.getFileExtensions().size());
    assertEquals(".json", settings.getFileExtensions().get(0));
    assertEquals(2, settings.getContexts().size());

    var origContext = settings.getContexts().get(0);
    assertEquals("main", origContext.getName());
    assertEquals(Type.PATTERN, origContext.getType());
    assertEquals("\\{", origContext.getRegex());
    assertEquals(1, origContext.getStyles().size());

    assertEquals(1, origContext.getContexts().size());
    var childContext = origContext.getContexts().get(0);
    assertEquals("childContext", childContext.getName());
    assertEquals(Type.INLINE_PUSH, childContext.getType());
    assertEquals("\\}", childContext.getRegex());
    assertEquals(1, childContext.getStyles().size());

    assertEquals(1, childContext.getContexts().size());
    var grandChildContext = childContext.getContexts().get(0);
    assertEquals("grandChildContext", grandChildContext.getName());
    assertEquals(Type.PATTERN, grandChildContext.getType());
    assertEquals("\\}", grandChildContext.getRegex());
    assertEquals(1, grandChildContext.getStyles().size());

    assertEquals(2, grandChildContext.getContexts().size());
    var greatGrandChildContext = grandChildContext.getContexts().get(0);
    assertEquals("greatGrandChildContext", greatGrandChildContext.getName());
    assertEquals(Type.POP, greatGrandChildContext.getType());
    assertEquals("\\}", greatGrandChildContext.getRegex());
    assertEquals(1, greatGrandChildContext.getStyles().size());

    var newContext = settings.getContexts().get(1);
    assertEquals("childContext2", newContext.getName());
    assertEquals(Type.PATTERN, newContext.getType());
    assertEquals("\\\\}", newContext.getRegex());
    assertEquals(1, newContext.getStyles().size());

    var style = origContext.getStyles().get(0);
    assertEquals("style1", style.getName());
    assertEquals("#000000", style.getColor());
    assertEquals("#000001", style.getBackgrounColor());
    assertEquals(1, origContext.getContexts().size());
  }

  @Test(expected = SyntaxParseException.class)
  public void testPopHasInnerContextFail() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/FailForPopHavingInnerContext.yml"));
    parser.build();

    var settings =parser.getSettings();
  }

  @Test
  public void testInifiniteRecursionWorks() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/FailForInfiniteRecursion.yml"));
    parser.build();

    var settings = parser.getSettings();
  }

  @Test(expected = SyntaxParseException.class)
  public void testUnknownIncludeFail() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/FailForUnknownInclude.yml"));
    parser.build();

    var settings = parser.getSettings();
  }

  @Test(expected = SyntaxParseException.class)
  public void testUnknownStyleFail() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/FailForUnknownStyle.yml"));
    parser.build();

    var settings = parser.getSettings();
  }

  @Test
  public void testShouldParse() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/FailForUnknownStyle.yml"));
    assertTrue(parser.shouldParse("g.json"));
    assertTrue(parser.shouldParse("f.g.json"));

    assertFalse(parser.shouldParse("g.json1"));
    assertFalse(parser.shouldParse("g.1json"));
    assertFalse(parser.shouldParse("json"));
    assertFalse(parser.shouldParse(".json"));
    assertFalse(parser.shouldParse("g.xml"));
  }

  @Test
  public void testJsonSyntaxParses() throws IOException {

  }



}
