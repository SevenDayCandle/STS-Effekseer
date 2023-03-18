package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.CustomPotionPoolModule;
import extendedui.ui.AbstractDungeonScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.controls.EUIStaticPotionGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;
import java.util.List;

public class PotionPoolScreen extends AbstractDungeonScreen
{
    public static CustomPotionPoolModule customModule;

    public EUIPotionGrid potionGrid;
    protected final EUIContextMenu<PotionPoolScreen.DebugOption> contextMenu;
    protected final EUIButton swapCardScreen;
    protected final EUIButton swapRelicScreen;
    private AbstractPotion selected;

    public PotionPoolScreen()
    {
        potionGrid = (EUIPotionGrid) new EUIStaticPotionGrid()
                .setOnPotionRightClick(this::onRightClick)
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

        swapRelicScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.85f)
                .setFont(EUIFontHelper.buttonFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.relicScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()))
                .setText(EUIRM.strings.uipool_viewRelicPool);

        contextMenu = (EUIContextMenu<PotionPoolScreen.DebugOption>) new EUIContextMenu<PotionPoolScreen.DebugOption>(new EUIHitbox(0, 0, 0, 0), d -> d.name)
                .setOnChange(options -> {
                    for (PotionPoolScreen.DebugOption o : options)
                    {
                        o.onSelect.invoke(this, selected);
                    }
                })
                .setFontForRows(EUIFontHelper.cardTooltipFont, 1f)
                .setItems(getOptions())
                .setCanAutosizeButton(true);
    }

    public void open(AbstractPlayer player, ArrayList<AbstractPotion> potions)
    {
        super.open(false, true);

        potionGrid.clear();
        if (potions.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        potionGrid.setPotions(potions);
        EUI.potionHeader.setGrid(potionGrid).snapToGroup(false);
        EUI.potionFilters.initialize(__ -> {
            EUI.potionHeader.updateForFilters();
            if (customModule != null) {
                customModule.open(EUI.potionHeader.getPotions());
            }
            potionGrid.forceUpdatePotionPositions();
        }, EUI.potionHeader.getOriginalPotions(), player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.potionHeader.updateForFilters();

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        customModule = EUI.getCustomPotionPoolModule(player);
        if (customModule != null) {
            customModule.open(EUIUtils.map(potionGrid.potionGroup, r -> r.potion));
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
        if (!EUI.potionFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            potionGrid.tryUpdate();
            swapCardScreen.updateImpl();
            swapRelicScreen.updateImpl();
            EUI.potionHeader.updateImpl();
            EUI.openPotionFiltersButton.tryUpdate();
            if (customModule != null) {
                customModule.update();
            }
        }
        contextMenu.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        potionGrid.tryRender(sb);
        swapCardScreen.renderImpl(sb);
        swapRelicScreen.renderImpl(sb);
        EUI.potionHeader.renderImpl(sb);
        if (!EUI.potionFilters.isActive) {
            EUI.openPotionFiltersButton.tryRender(sb);
        }
        if (customModule != null) {
            customModule.render(sb);
        }
        contextMenu.tryRender(sb);
    }

    protected void onRightClick(AbstractPotion c)
    {
        if (EUIConfiguration.enableCardPoolDebug.get())
        {
            selected = c;
            contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
            contextMenu.refreshText();
            contextMenu.openOrCloseMenu();
        }
    }

    protected void obtain(AbstractPotion c)
    {
        if (c != null && AbstractDungeon.player != null)
        {
            AbstractDungeon.player.obtainPotion(c);
        }
    }

    public static List<PotionPoolScreen.DebugOption> getOptions()
    {
        return EUIUtils.list(PotionPoolScreen.DebugOption.obtain);
    }

    public static class DebugOption
    {
        public static PotionPoolScreen.DebugOption obtain = new PotionPoolScreen.DebugOption(EUIRM.strings.uipool_obtainPotion, PotionPoolScreen::obtain);

        public final String name;
        public final ActionT2<PotionPoolScreen, AbstractPotion> onSelect;

        DebugOption(String name, ActionT2<PotionPoolScreen, AbstractPotion> onSelect)
        {
            this.name = name;
            this.onSelect = onSelect;
        }
    }
}