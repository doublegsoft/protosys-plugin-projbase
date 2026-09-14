package org.doublegsoft.protosys.projbase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    File rootdir = new File(appRoot, dir);
    List<String> retVal = new ArrayList<>();
    if (!rootdir.exists()) {
      return retVal;
    }
    List<File> files = listFiles(rootdir);
    for (File f : files) {
      retVal.add(dir + "/" + f.getName().replaceAll(appRoot + "/", ""));
    }
    return retVal;
  }

  public List<ThirdParty> listThirdParties(String dir) {
    File rootdir = new File(appRoot, dir);
    List<ThirdParty> retVal = new ArrayList<>();
    if (!rootdir.exists()) {
      return retVal;
    }
    for (File f : rootdir.listFiles()) {
      if (f.getName().equals(".") || f.getName().equals("..") || !f.isDirectory()) {
        continue;
      }
      ThirdParty tp = new ThirdParty();
      String name = f.getName();
      String projname = name;
      if (name.contains("-")) {
        projname = name.substring(0, name.lastIndexOf("-"));
        tp.setVersion(name.substring(name.lastIndexOf("-") + 1));
      }
      tp.setName(projname);
      retVal.add(tp);
    }
    return retVal;
  }

  private List<File> listFiles(File dir) {
    List<File> retVal = new ArrayList<>();
    if (dir == null || !dir.exists() || dir.listFiles() == null) {
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

  public List<ThirdParty> parseThirdParties(String dir) throws IOException {
    List<ThirdParty> retVal = new ArrayList<>();
    File rootdir = new File(appRoot, dir);
    if (!rootdir.exists()) {
      return retVal;
    }
    for (File f : rootdir.listFiles()) {
      if (!f.isFile()) {
        continue;
      }
      List<String> lines = Files.readAllLines(f.toPath());
      for (String line : lines) {
        if (line.trim().isEmpty()) {
          continue;
        }
        line = line.trim();
        String[] strs = line.split(":");
        ThirdParty tp = new ThirdParty();
        tp.setGroup(strs[0]);
        tp.setName(strs[1]);
        tp.setVersion(strs[2]);
        retVal.add(tp);
      }
    }
    return retVal;
  }
}
