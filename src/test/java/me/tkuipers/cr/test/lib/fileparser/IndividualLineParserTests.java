package me.tkuipers.cr.test.lib.fileparser;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Type;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;
import me.tkuipers.cr.lib.file.parser.file.FileParser;
import me.tkuipers.cr.lib.file.parser.file.IFileParser;
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

  @Before
  public void setUp() throws IOException {
    var crSettings = TokenizerTestUtils.genSimpleSettings();
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
  @Ignore
  public void testParseSimpleInlinePush() throws InterruptedException {
    parser = new FileParser(settings, file);
    var contextList = settings.getContexts();
    var context = contextList.stream().filter(m -> m.getRegex().equals("abc")).findFirst().orElseThrow();
    context.setType(Type.INLINE_PUSH);
    context.setRegex("(abc)|(def)");
    settings.setContexts(contextList);

    var styledLines = parser.parseLine(settings, "abcdefghijklmnopqrstuvwxyzab");


    assertEquals(1, styledLines.size());
    var styleLine1 = styledLines.get(0);
    assertEquals(1, styleLine1.getStyles().size());
    var styleLine1Style = styleLine1.getStyles().get(0);
    assertEquals("otherStyle", styleLine1Style.getName());
    assertEquals("abcdefghijklmnopqrstuvwxyzab", styleLine1.getStringValue());

  }

}
