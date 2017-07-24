package com.autominifier.service;

import static com.autominifier.model.settings.InternalParameters.SENTINEL_FILE_EXTENSION;
import static com.autominifier.model.settings.InternalParameters.SETTINGS_HISTORY_DIR;
import static com.autominifier.util.AutoMinifierUtils.getSettingsDirPath;
import static com.autominifier.util.AutoMinifierUtils.hideFile;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.autominifier.model.settings.SettingsManager;
import com.autominifier.model.settings.beans.Settings;
import com.autominifier.model.settings.command.AppendPartialSettingsToJson;
import com.autominifier.model.settings.command.GetPartialSettingsFromJson;
import com.google.gson.GsonBuilder;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class JsonUpdaterService extends Service<Set<Settings>> {

   private GsonBuilder    builder;
   private final Settings settings;
   private final String   dir;

   public JsonUpdaterService(GsonBuilder builder, Settings settings, String dir) {
      this.builder = builder;
      this.settings = settings;
      this.dir = dir;
   }

   @Override
   protected Task<Set<Settings>> createTask() {
      return new Task<Set<Settings>>() {
         @Override
         protected Set<Settings> call() throws Exception {
            final String regEx = String.format(
                  "^\\.settings[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\.%s$",
                  SENTINEL_FILE_EXTENSION);

            List<File> listOfSentinels = FileUtils
                  .listFiles(new File(dir), new RegexFileFilter(regEx), FalseFileFilter.FALSE).stream()
                  .collect(Collectors.toList());

            String historyDirPathFmt = String.format("%1$s%2$s%3$s%2$s", getSettingsDirPath(), File.separator,
                  SETTINGS_HISTORY_DIR);

            if (listOfSentinels.size() > 1) {
               listOfSentinels.stream().forEach(s -> {
                  String name = FilenameUtils.removeExtension(s.getName());
                  FileUtils.deleteQuietly(new File(String.format("%s%s.json", historyDirPathFmt, name)));
                  FileUtils.deleteQuietly(s);
               });
               listOfSentinels.clear();
            }

            if (listOfSentinels.size() == 0) {
               String name = ".settings" + UUID.randomUUID().toString().toLowerCase();
               File f = new File(String.format("%s%s%s.%s", dir, File.separator, name, SENTINEL_FILE_EXTENSION));
               FileUtils.touch(f);
               hideFile(f);
               FileUtils.touch(new File(String.format("%s%s.json", historyDirPathFmt, name)));
               listOfSentinels.add(f);
            }

            File f = new File(String.format("%s%s.json", historyDirPathFmt,
                  FilenameUtils.removeExtension(listOfSentinels.get(0).getName())));

            // Defensive approach
            if (!f.exists()) {
               FileUtils.touch(f);
            }

            SettingsManager manager = SettingsManager.getInstance();

            // Output param. It will be filled with partial settings from json
            Set<Settings> partialSettings = new HashSet<>();

            // Get json partial settings
            GetPartialSettingsFromJson getOp = new GetPartialSettingsFromJson(builder, f, partialSettings);
            manager.executeOperation(getOp);

            Settings settingsCopy = new Settings();
            settingsCopy.getCssSettings().init(settings.getCssSettings().getMinSeparator(),
                  settings.getCssSettings().getMinExtension(), -1, null);
            settingsCopy.getJsSettings().init(settings.getJsSettings().getMinSeparator(),
                  settings.getJsSettings().getMinExtension(), -1, null);
            partialSettings.add(settingsCopy);

            // Append current settings partially to json file
            AppendPartialSettingsToJson appendOp = new AppendPartialSettingsToJson(builder, f, partialSettings);
            manager.executeOperation(appendOp);

            return partialSettings;
         }
      };
   }
}
