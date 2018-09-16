package me.tkuipers.cr.lib.file.parser.tokentranslator;

import me.tkuipers.cr.lib.file.parser.StyledString;

import java.util.Collection;
import java.util.List;

public interface TokenTranslator {
  //Use list as opposed to collection as it is ordered
  String getStringFromToken(List<StyledString> stringList);

}
