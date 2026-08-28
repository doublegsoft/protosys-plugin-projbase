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

  private final String outputRoot;

  public ProjbaseHelper(String root, String appname) {
    this.outputRoot = root + "/" + appname;
  }

  public List<String> listFiles(String dir) {
    File rootdir = new File(outputRoot, dir);
    List<String> retVal = new ArrayList<>();
    List<File> files = listFiles(rootdir);
    for (File f : files) {
      retVal.add(f.getName().replaceAll(outputRoot + "/", ""));
    }
    return retVal;
  }

  private List<File> listFiles(File dir) {
    List<File> retVal = new ArrayList<>();
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
