package me.tkuipers.cr.lib.file.parser.project;

import com.google.common.collect.Lists;
import me.tkuipers.cr.lib.data.parsesettings.YamlFileParser;
import me.tkuipers.cr.lib.file.parser.exceptions.InvalidFolderException;

import java.io.File;
import java.util.List;

public class ProjectParser {

  private List<YamlFileParser> requiredParseSettings;
  private List<YamlFileParser> allParseSettings;

  public ProjectParser(File syntaxFileLocation, File projectDir){
    verify(syntaxFileLocation, projectDir);
    requiredParseSettings = Lists.newArrayList();
    allParseSettings = Lists.newArrayList();
  }

  private void verify(File syntaxFileLocation, File projectDir){
    if(syntaxFileLocation.getAbsolutePath().equals(projectDir.getAbsolutePath())){
      throw new InvalidFolderException(projectDir.getAbsolutePath(), "ProjectDir or SyntaxDir", "Both folders use the same path");
    }
    verifySyntax(syntaxFileLocation);
    verifyProjectDir(projectDir);
  }

  private void verifyProjectDir(File projectDir) {
    if(!projectDir.isDirectory()){
      throw new InvalidFolderException(projectDir.getAbsolutePath(), "ProjectDir", "File used instead of folder");
    }
  }

  private void verifySyntax(File syntaxFileLocation) {
    if(!syntaxFileLocation.isDirectory()){
      throw new InvalidFolderException(syntaxFileLocation.getAbsolutePath(), "SyntaxDir", "File used instead of folder");
    }
  }
}
