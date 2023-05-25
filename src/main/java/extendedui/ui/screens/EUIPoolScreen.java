package extendedui.ui.screens;

import basemod.abstracts.CustomScreen;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.patches.game.AbstractDungeonPatches;

public abstract class EUIPoolScreen extends EUIDungeonScreen {

    public void switchScreen() {
        super.switchScreen();
        AbstractDungeon.overlayMenu.hideBlackScreen();
    }

    @Override
    public void close() {
        switchScreen();
    }

    @Override
    public void openingDeck() {
        switchScreen();
    }

    @Override
    public void openingMap() {
        switchScreen();
    }

    @Override
    public void openingSettings() {
        switchScreen();
    }

    @Override
    public void reopen() {
        super.reopen();
        AbstractDungeon.dungeonMapScreen.map.hideInstantly(); // Because the map won't be hidden properly otherwise
        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
    }

    public void open() {
        reopen();
    }

    public boolean allowOpenDeck() {
        return true;
    }

    public boolean allowOpenMap() {
        return true;
    }
}
