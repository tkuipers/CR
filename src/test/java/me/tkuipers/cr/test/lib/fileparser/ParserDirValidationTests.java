package me.tkuipers.cr.test.lib.fileparser;

import me.tkuipers.cr.lib.file.parser.FileParser;
import me.tkuipers.cr.lib.file.parser.exceptions.InvalidFolderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.UUID;

public class ParserDirValidationTests {

  private File folder;
  private File file;

  @Before
  public void setUpFileSystemForTests() throws IOException {
    var uuid = UUID.randomUUID();
    var folderPath = "/tmp/" + uuid;
    folder = new File(folderPath);
    folder.mkdirs();
    file = Files.write(new File(folderPath + "/fakeFile").toPath(), "".getBytes(), StandardOpenOption.CREATE).toFile();
  }

  @After
  public void cleanupFileSystem() throws IOException {
    deleteDirectoryStream(folder.toPath());
  }

  void deleteDirectoryStream(Path path) throws IOException {
    Files.walk(path)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
  }

  @Test(expected = InvalidFolderException.class)
  public void testEnsureInvalidProjectDirIsDetected(){
    new FileParser(folder, file);
  }

  @Test(expected = InvalidFolderException.class)
  public void testEnsureInvalidSyntaxFileFolderIsDetected(){
    new FileParser(file, folder);
  }
}
