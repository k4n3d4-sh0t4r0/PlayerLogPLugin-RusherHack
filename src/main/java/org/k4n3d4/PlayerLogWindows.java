package org.k4n3d4;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.ListItemContent;
import org.rusherhack.client.api.ui.window.view.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class PlayerLogWindows extends ResizeableWindow {

    private final List<LogItem> logItems = new ArrayList<>();
    private final LogListView logView;
    private final TabbedView tabView;

    public PlayerLogWindows() {
        super("PlayerLog", 100, 100, 600, 200);

        this.logView = new LogListView("Logs", this, logItems);

        ButtonComponent refreshButton = new ButtonComponent(this, "Refresh", this::loadLogs);
        ComboContent combo = new ComboContent(this);
        combo.addContent(refreshButton, ComboContent.AnchorSide.RIGHT);

        this.tabView = new TabbedView(this, List.of(combo, logView));

        this.setMinWidth(150);
        this.setMinHeight(200);

        loadLogs();
    }

    @Override
    public WindowView getRootView() {
        return this.tabView;
    }

    private void loadLogs() {
        logItems.clear();

        File file = new File(PlayerLogModule.getLogFilePath());
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logItems.add(new LogItem(line, logView));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logView.resort();
    }

    private static class LogItem extends ListItemContent {
        private final String message;

        public LogItem(String message, ListView<LogItem> view) {
            super(view.getWindow(), view);
            this.message = message;
        }

        @Override
        public String getAsString(ListView<?>.Column column) {
            return message;
        }
    }

    private static class LogListView extends ListView<LogItem> {
        public LogListView(String name, ResizeableWindow window, List<LogItem> items) {
            super(name, window, items);
            this.addColumn("Log");
        }
    }
}