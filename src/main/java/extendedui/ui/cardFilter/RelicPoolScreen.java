package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomRelicPoolModule;
import extendedui.ui.AbstractDungeonScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUIStaticRelicGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

public class RelicPoolScreen extends AbstractDungeonScreen
{
    public static CustomRelicPoolModule customModule;

    public EUIRelicGrid relicGrid;
    protected final EUIContextMenu<RelicPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapPotionScreen;
    private AbstractRelic selected;

    public RelicPoolScreen()
    {
        relicGrid = (EUIRelicGrid) new EUIStaticRelicGrid()
                .setOnRelicRightClick(this::onRightClick)
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);

        swapCardScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.9f)
                .setFont(EUIFontHelper.buttonFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.cardsScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllCards()))
                .setText(EUIRM.strings.uipool_viewCardPool);

        swapPotionScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setFont(EUIFontHelper.buttonFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.potionScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllPotions()))
                .setText(EUIRM.strings.uipool_viewPotionPool);

        contextMenu = (EUIContextMenu<RelicPoolScreen.DebugOption>) new EUIContextMenu<RelicPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (RelicPoolScreen.DebugOption o : options)
                    {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setCanAutosizeButton(true);
    }

    public void open(AbstractPlayer player, ArrayList<AbstractRelic> relics)
    {
        super.open(false, true);

        relicGrid.clear();
        if (relics.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        relicGrid.setRelics(relics);
        EUI.relicHeader.setGrid(relicGrid).snapToGroup(false);
        EUI.relicFilters.initialize(__ -> {
            EUI.relicHeader.updateForFilters();
            if (customModule != null) {
                customModule.open(EUI.relicHeader.getRelics());
            }
            relicGrid.forceUpdateRelicPositions();
        }, EUI.relicHeader.getOriginalRelics(), player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.relicHeader.updateForFilters();

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        customModule = EUI.getCustomRelicPoolModule(player);
        if (customModule != null) {
            customModule.open(EUIUtils.map(relicGrid.relicGroup, r -> r.relic));
        }

    }

    @Override
    public void reopen()
    {
        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        if (!EUI.relicFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            relicGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapPotionScreen.updateImpl();
            EUI.relicHeader.updateImpl();
            EUI.openRelicFiltersButton.tryUpdate();
            if (customModule != null) {
                customModule.update();
            }
        }
        contextMenu.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        relicGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapPotionScreen.renderImpl(sb);
        EUI.relicHeader.renderImpl(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openRelicFiltersButton.tryRender(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    protected void onRightClick(AbstractRelic c)
    {
        if (EUIConfiguration.enableCardPoolDebug.get())
        {
            selected = c;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.setItems(getOptions(c));
            contextMenu.openOrCloseMenu();
        }
        else
        {
            openPopup(c);
        }
    }

    protected void openPopup(AbstractRelic c)
    {
        c.hb.unhover();
        CardCrawlGame.relicPopup.open(c);
    }

    protected void obtain(AbstractRelic c)
    {
        if (c != null)
        {
            AbstractRelic copy = c.makeCopy();
            copy.instantObtain();
        }
    }

    public static ArrayList<RelicPoolScreen.DebugOption> getOptions(AbstractRelic r)
    {
        return EUIUtils.arrayList(DebugOption.enlarge, DebugOption.obtain);
    }

    public static class DebugOption
    {
        public static RelicPoolScreen.DebugOption enlarge = new RelicPoolScreen.DebugOption(EUIRM.strings.uipool_enlarge, RelicPoolScreen::openPopup);
        public static RelicPoolScreen.DebugOption obtain = new RelicPoolScreen.DebugOption(EUIRM.strings.uipool_obtainRelic, RelicPoolScreen::obtain);

        public final String name;
        public final ActionT2<RelicPoolScreen, AbstractRelic> onSelect;

        DebugOption(String name, ActionT2<RelicPoolScreen, AbstractRelic> onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}