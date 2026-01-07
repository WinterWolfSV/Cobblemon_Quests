package winterwolfsv.cobblemon_quests.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.EnumConfig;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.AbstractButtonListScreen;

public class FixedEnumConfig<E> extends EnumConfig<E> {
    public FixedEnumConfig(NameMap<E> nameMap) {
        super(nameMap);
    }

    @Override
    public void onClicked(Widget widget, MouseButton button, ConfigCallback callback) {
        if (this.nameMap.values.size() <= 16 && !BaseScreen.isCtrlKeyDown()) {
            super.onClicked(widget, button, callback);
            return;
        }

        new EnumSelectScreen(widget.getParent(), callback).openGui();
    }

    private class EnumSelectScreen extends AbstractButtonListScreen {
        private final Panel parent;
        private final ConfigCallback callback;

        EnumSelectScreen(Panel parent, ConfigCallback callback) {
            this.parent = parent;
            this.callback = callback;
        }

        @Override
        public void addButtons(Panel panel) {
            for (final E v : nameMap) {
                panel.add(new SimpleTextButton(panel, nameMap.getDisplayName(v), nameMap.getIcon(v)) {
                    @Override
                    public void onClicked(MouseButton button) {
                        playClickSound();
                        boolean changed = setCurrentValue(v);
                        callback.save(changed); // Missing upstream
                        doAccept();
                    }
                });
            }
        }

        @Override
        protected void doAccept() {
            parent.run();
        }

        @Override
        protected void doCancel() {
            parent.run();
        }
    }
}



