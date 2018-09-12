package me.tkuipers.cr.test.lib.fileparser;

import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.file.parser.exceptions.FileParseException;
import me.tkuipers.cr.lib.file.parser.file.FileParser;
import me.tkuipers.cr.lib.file.parser.file.IFileParser;
import me.tkuipers.cr.test.utils.TokenizerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class IndividualFileParserTests {

  private IFileParser parser;
  private Settings settings;
  private File file;
  private File folder;

  @Before
  public void setUp() throws IOException {
    var crSettings = TokenizerTestUtils.genSimpleSettings();
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

}
