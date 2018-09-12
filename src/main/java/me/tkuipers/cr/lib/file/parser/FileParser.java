package me.tkuipers.cr.lib.file.parser;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.data.parsesettings.parsed.Settings;
import me.tkuipers.cr.lib.file.parser.exceptions.InvalidFolderException;

import java.io.File;
import java.util.List;

public class FileParser {

  private List<YamlFileParser> requiredParseSettings;
  private List<YamlFileParser> allParseSettings;

  public FileParser(File syntaxFileLocation, File projectDir){
    verifySyntax(syntaxFileLocation);
    verifyProjectDir(projectDir);

    requiredParseSettings = Lists.newArrayList();
    allParseSettings = Lists.newArrayList();
  }

  private void verifyProjectDir(File projectDir) {
    if(!projectDir.isDirectory()){
      throw new InvalidFolderException(projectDir.getAbsolutePath(), "ProjectDir");
    }
  }

  private void verifySyntax(File syntaxFileLocation) {
    if(!syntaxFileLocation.isDirectory()){
      throw new InvalidFolderException(syntaxFileLocation.getAbsolutePath(), "ProjectDir");
    }
  }
}
