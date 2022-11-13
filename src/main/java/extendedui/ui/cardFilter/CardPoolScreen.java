package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUIStaticCardGrid;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

public class CardPoolScreen extends AbstractScreen
{
    public static CustomCardPoolModule CustomModule;

    private final EUIToggle upgradeToggle;
    private final EUIToggle colorlessToggle;
    private final EUIButton swapScreen;
    public EUICardGrid cardGrid;

    public CardPoolScreen()
    {
        cardGrid = new EUIStaticCardGrid()
                .ShowScrollbar(true)
                .CanRenderUpgrades(true)
                .SetOnCardRightClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.cards);
                })
                .SetVerticalStart(Settings.HEIGHT * 0.66f);

        upgradeToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetBackground(EUIRM.Images.Panel.Texture(), Color.DARK_GRAY)
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.8f)
                .SetFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .SetText(SingleCardViewPopup.TEXT[6])
                .SetOnToggle(EUI::ToggleViewUpgrades);

        colorlessToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetBackground(EUIRM.Images.Panel.Texture(), Color.DARK_GRAY)
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.75f)
                .SetFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .SetText(EUIRM.Strings.UICardPool_ShowColorless)
                .SetOnToggle(val -> {
                    EUI.CardFilters.ColorsDropdown.ToggleSelection(AbstractCard.CardColor.COLORLESS, val, true);
                    EUI.CardFilters.ColorsDropdown.ToggleSelection(AbstractCard.CardColor.CURSE, val, true);
                });

        this.swapScreen = new EUIButton(EUIRM.Images.HexagonalButton.Texture(),
                new AdvancedHitbox(Scale(210), Scale(43)))
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.88f)
                .SetFont(FontHelper.buttonLabelFont, 0.8f)
                .SetColor(Color.GRAY)
                .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.GRAY)
                .SetOnClick(() -> EUI.RelicScreen.Open(AbstractDungeon.player, CardPoolPanelItem.GetAllRelics()))
                .SetText(EUIRM.Strings.UIPool_ViewRelicPool);
    }

    public void Open(AbstractPlayer player, CardGroup cards)
    {
        super.Open(false, true);

        cardGrid.Clear();
        colorlessToggle.SetToggle(false);
        if (cards.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        cardGrid.SetCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CustomHeader.SetupButtons();
        EUI.CardFilters.Initialize(__ -> {
            EUI.CustomHeader.UpdateForFilters();
            if (CustomModule != null) {
                CustomModule.Open(EUI.CustomHeader.group.group);
            }
            cardGrid.ForceUpdateCardPositions();
        }, EUI.CustomHeader.originalGroup, player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.CustomHeader.UpdateForFilters();

        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        CustomModule = EUI.GetCustomCardPoolModule(player);
        if (CustomModule != null) {
            CustomModule.SetActive(true);
            CustomModule.Open(cardGrid.cards.group);
        }

    }

    @Override
    public void Reopen()
    {
        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }
    }

    @Override
    public void Update()
    {
        if (!EUI.CardFilters.TryUpdate() && !CardCrawlGame.isPopupOpen) {
            cardGrid.TryUpdate();
            upgradeToggle.SetToggle(SingleCardViewPopup.isViewingUpgrade).Update();
            colorlessToggle.Update();
            swapScreen.Update();
            EUI.CustomHeader.update();
            EUI.OpenCardFiltersButton.TryUpdate();
            if (CustomModule != null) {
                CustomModule.TryUpdate();
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        cardGrid.TryRender(sb);
        upgradeToggle.Render(sb);
        colorlessToggle.Render(sb);
        swapScreen.Render(sb);
        EUI.CustomHeader.render(sb);
        if (!EUI.CardFilters.isActive) {
            EUI.OpenCardFiltersButton.TryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.TryRender(sb);
        }
    }
}