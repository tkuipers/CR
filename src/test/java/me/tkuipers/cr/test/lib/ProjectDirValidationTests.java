package me.tkuipers.cr.test.lib;

import me.tkuipers.cr.lib.file.parser.project.ProjectParser;
import me.tkuipers.cr.lib.file.parser.exceptions.InvalidFolderException;
import me.tkuipers.cr.test.utils.TokenizerTestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class ProjectDirValidationTests {
  private File folder;
  private File folder1;
  private File file;

  @Before
  public void setUpFileSystemForTests() throws IOException {
    folder = TokenizerTestUtils.buildRandomTempFolder();

    folder1 = new File(folder.getAbsoluteFile() + "/fakeyfake/");
    folder1.mkdirs();
    file = Files.write(new File(folder.getAbsoluteFile() + "/fakeFile").toPath(), "".getBytes(), StandardOpenOption.CREATE).toFile();
  }

  @After
  public void cleanupFileSystem() throws IOException {
    FileUtils.deleteDirectory(folder);
  }

  @Test(expected = InvalidFolderException.class)
  public void testEnsureInvalidProjectDirIsDetected() {
    new ProjectParser(folder, file);
  }

  @Test(expected = InvalidFolderException.class)
  public void testEnsureInvalidSyntaxFileFolderIsDetected() {
    new ProjectParser(file, folder);
  }

  @Test(expected = InvalidFolderException.class)
  public void testEnsureFoldersAreNotTheSame() {
    new ProjectParser(folder, folder);
  }

  @Test
  public void ensureObjectIsInstantiated() {
    new ProjectParser(folder, folder1);
  }
}
