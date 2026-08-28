package org.doublegsoft.protosys.projbase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: ADD DESCRIPTION
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public class ProjbaseHelper {

  private final String appRoot;

  public ProjbaseHelper(String output) {
    this.appRoot = output;
  }

  public List<String> listFiles(String dir) {
    System.out.println(appRoot);
    File rootdir = new File(appRoot, dir);
    List<String> retVal = new ArrayList<>();
    List<File> files = listFiles(rootdir);
    for (File f : files) {
      retVal.add(f.getName().replaceAll(appRoot + "/", ""));
    }
    return retVal;
  }

  private List<File> listFiles(File dir) {
    List<File> retVal = new ArrayList<>();
    if (dir == null) {
      return retVal;
    }
    for (File f : dir.listFiles()) {
      if (f.getName().equals(".") || f.getName().equals("..")) {
        continue;
      }
      if (f.isDirectory()) {
        retVal.addAll(listFiles(f));
      } else {
        retVal.add(f);
      }
    }
    return retVal;
  }
}
