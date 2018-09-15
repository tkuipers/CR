package me.tkuipers.cr.lib.file.parser.file;

import me.tkuipers.cr.lib.data.parsesettings.parsed.IContextContainer;
import me.tkuipers.cr.lib.file.parser.StyledString;

import java.io.File;
import java.util.List;

public interface IFileParser {

  List<StyledString> parseLine(IContextContainer parserContext, String lineToParse);
  List<StyledString> parseFile(IContextContainer parserContext, File fileToParse);
}
