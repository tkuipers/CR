package me.tkuipers.cr.test.lib.fileparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRContext;
import me.tkuipers.cr.lib.data.parsesettings.filebacked.CRSettings;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;
import me.tkuipers.cr.lib.file.parser.file.FileParser;
import me.tkuipers.cr.lib.file.parser.file.IFileParser;
import me.tkuipers.cr.lib.file.parser.tokentranslator.HTMLTokenTranslator;
import me.tkuipers.cr.test.utils.TokenizerTestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;

public class IndividualLineParserTests {

  private IFileParser parser;
  private Settings settings;
  private File file;
  private File folder;
  private CRSettings crSettings;

  @Before
  public void setUp() throws IOException {
    crSettings = TokenizerTestUtils.genSimpleSettings();
    var origCRStyle = crSettings.getStyles().get(0);
    var newCRStyle = origCRStyle.clone();
    newCRStyle.setName("otherStyle");
    newCRStyle.setBackgrounColor("#111111");
    newCRStyle.setColor("#111111");
    crSettings.setStyles(Lists.newArrayList(origCRStyle, newCRStyle));

    var origCRContext =  TokenizerTestUtils.genCRContext();
    var newCRContext  = origCRContext.clone();
    newCRContext.setStyles(Lists.newArrayList("otherStyle"));
    newCRContext.setRegex("abc");
    crSettings.setContexts(Lists.newArrayList(newCRContext, origCRContext));
    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();


    folder = TokenizerTestUtils.buildRandomTempFolder();
    file = Files.write(new File(folder.getAbsoluteFile() + "/fakeFile").toPath(), "".getBytes(), StandardOpenOption.CREATE).toFile();
  }

  @Test
  public void testEnsureClassIsInstantiated(){
    parser = new FileParser(settings, file);
  }

  @Test(expected = FileParseException.class)
  public void testEnsureFoldersAreNotScanned(){
    parser = new FileParser(settings, folder);
  }

  @Test
  public void testParseSimplePattern() throws InterruptedException {
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyzab");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());


    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("mainStyle", styleLine2Style.getName());
  }

  @Test
  public void testParsePatternWithMultipleMatches() throws InterruptedException {
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyzabc");

    assertEquals(3, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("mainStyle", styleLine2Style.getName());

    var styleLine3 = styledLines.get(2);
    assertEquals(1, styleLine3.getStyles().size());
    var styleLine3Style = styleLine3.getStyles().get(0);
    assertEquals("otherStyle", styleLine3Style.getName());
    assertEquals("abc", styleLine3.getStringValue());
  }

  @Test
  public void testParsePatternWithMultipleMatchers() throws InterruptedException {
    parser = new FileParser(settings, file);
    var contextList = settings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setRegex("(abc)|(def)");
    settings.setContexts(contextList);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyzab");


    assertEquals(3, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("def", styleLine2.getStringValue());

    var styleLine3 = styledLines.get(2);
    assertEquals(1, styleLine3.getStyles().size());
    var styleLine3Style = styleLine3.getStyles().get(0);
    assertEquals("mainStyle", styleLine3Style.getName());

  }

  @Test
  public void testParseSimpleInlinePush() throws InterruptedException {
    parser = new FileParser(settings, file);
    var contextList = settings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");
    settings.setContexts(contextList);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyzab");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijklmnopqrstuvwxyzab", styleLine2.getStringValue());

  }

  @Test
  public void testParseInlinePushWithNewlineInIt() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");
    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyz\nab");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijklmnopqrstuvwxyz\nab", styleLine2.getStringValue());

  }

  @Test
  public void testParseInlinePushWithNewlineAtEnd() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");
    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyz\n");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijklmnopqrstuvwxyz\n", styleLine2.getStringValue());

  }

  @Test
  public void testParseInlinePushWithMultiplePushes() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");
    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyz\nababcss\n");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijklmnopqrstuvwxyz\nababcss\n", styleLine2.getStringValue());
  }

  @Test
  public void testParseInlinePushWithMultipleNestedPushes() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");

    var style = TokenizerTestUtils.genCRStyle();
    style.setName("thirdStyle");
    crSettings.getStyles().add(style);

    var context2 = new CRContext();
    context2.setType(Type.INLINE_PUSH);
    context2.setRegex("(xyz)");
    context2.getStyles().add("thirdStyle");

    context.getContexts().add(context2);

    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefxyzgh\nijklmnopqrstuvwxz\nab");

    assertEquals(4, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("def", styleLine2.getStringValue());

    var styleLine3 = styledLines.get(2);
    assertEquals(1, styleLine3.getStyles().size());
    var styleLine3Style = styleLine3.getStyles().get(0);
    assertEquals("thirdStyle", styleLine3Style.getName());
    assertEquals("xyz", styleLine3.getStringValue());

    var styleLine4 = styledLines.get(3);
    assertEquals(1, styleLine4.getStyles().size());
    var styleLine4Style = styleLine4.getStyles().get(0);
    assertEquals("otherStyle", styleLine4Style.getName());
    assertEquals("gh\nijklmnopqrstuvwxz\nab", styleLine4.getStringValue());
  }

  @Test
  public void testParseMultilinePushWithNewlineInItAndNoPop() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.MULTILINE_PUSH);
    context.setRegex("(abc)|(def)");
    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyz\nab");

    assertEquals(2, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijklmnopqrstuvwxyz\nab", styleLine2.getStringValue());
  }


  @Test
  public void testParseMultilinePushWithNewlineInItAndAPop() throws InterruptedException, IOException {
    var contextList = crSettings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.MULTILINE_PUSH);
    context.setRegex("(abc)|(def)");

    var style = TokenizerTestUtils.genCRStyle();
    style.setName("thirdStyle");
    crSettings.getStyles().add(style);

    var context2 = new CRContext();
    context2.setType(Type.POP);
    context2.setRegex("(xyz)");

    context.getContexts().add(context2);

    var yamlParser = new YamlFileParser(crSettings);
    yamlParser.build();
    settings = yamlParser.getSettings();
    parser = new FileParser(settings, file);

    var styledLines = parser.parseLine(settings, "abcdefghijkl\nmnopqrstuvwxyz\nab");


    assertEquals(4, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abc", styleLine1.getStringValue());

    var styleLine2 = styledLines.get(1);
    assertEquals(1, styleLine2.getStyles().size());
    var styleLine2Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine2Style.getName());
    assertEquals("defghijkl\nmnopqrstuvw", styleLine2.getStringValue());

    var styleLine3 = styledLines.get(2);
    assertEquals(1, styleLine3.getStyles().size());
    var styleLine3Style = styleLine3.getStyles().get(0);
    assertEquals("otherStyle", styleLine3Style.getName());
    assertEquals("xyz", styleLine3.getStringValue());

    var styleLine4 = styledLines.get(3);
    assertEquals(1, styleLine4.getStyles().size());
    var styleLine4Style = styleLine2.getStyles().get(0);
    assertEquals("otherStyle", styleLine4Style.getName());
    assertEquals("\nab", styleLine4.getStringValue());
  }

  @Test
  //@Ignore
  public void testExampleJSONFile() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/CustomLanguageSyntaxFile.yml"));
    var mapper = new ObjectMapper(new YAMLFactory());
    var crSettings = mapper.readValue(cl.getResource("ExampleYamlFiles/CustomLanguageSyntaxFile.yml"), CRSettings.class);
    System.out.println(crSettings.getContexts());
    parser.build();
    var json = "execute \"song\";\n" +
          "\n" +
          "group {\n" +
          "   execute 1;\n" +
          "   execute 2;\n" +
          "   // This is a comment\n" +
          "   print \"hello\";\n" +
          "   \n" +
          "   /* This \n" +
          "   is \n" +
          "   a \n" +
          "   multiline\n" +
          "   comment */\n" +
          "   a (1,2,3);\n" +
          "   a;\n" +
          "   group {\n" +
          "   \n" +
          "   }\n" +
          "}}";
    var fParser = new FileParser(parser.getSettings(), file);
    var styledLines = fParser.parseLine(parser.getSettings(), json);
    System.out.println(styledLines);

    System.out.println(new HTMLTokenTranslator().getStringFromToken(styledLines));
  }

  @Test
  @Ignore
  public void testExampleSmallCase() throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    var parser = new YamlFileParser(cl.getResource("ExampleYamlFiles/CustomLanguageSyntaxFile.yml"));
    parser.build();
    var json =
          "group {\n" +
          "  f\n" +
          "}\n";
    var fParser = new FileParser(parser.getSettings(), file);
    var styledLines = fParser.parseLine(parser.getSettings(), json);
    System.out.println(styledLines);

    System.out.println(new HTMLTokenTranslator().getStringFromToken(styledLines));
  }

}
