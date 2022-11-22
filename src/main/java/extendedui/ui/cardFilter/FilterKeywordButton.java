package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;
import static extendedui.text.EUISmartText.CARD_ENERGY_IMG_WIDTH;

public class FilterKeywordButton extends EUIHoverable
{
    public static final float ICON_SIZE = scale(40);
    private static final Color ACTIVE_COLOR = new Color(0.76f, 0.76f, 0.76f, 1f);
    private static final Color NEGATE_COLOR = new Color(0.62f, 0.32f, 0.28f, 1f);
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private ActionT1<FilterKeywordButton> onClick;

    public final GenericFilters<?> filters;
    public final float baseCountOffset = -0.17f;
    public final float baseImageOffsetX = -0.27f;
    public final float baseImageOffsetY = -0.2f;
    public final float baseTextOffsetX = -0.10f;
    public final float baseTextOffsetY = 0f;
    public int cardCount = -1;

    public EUIButton backgroundButton;
    public EUILabel titleText;
    public EUILabel countText;

    protected float offX;
    protected float offY;

    public FilterKeywordButton(GenericFilters<?> filters, EUITooltip tooltip)
    {
        super(filters.hb);

        this.filters = filters;
        this.tooltip = tooltip;

        backgroundButton = new EUIButton(EUIRM.Images.panelRoundedHalfH.texture(), new RelativeHitbox(hb, 1, 1, 0.5f, 0).setIsPopupCompatible(true))
                .setClickDelay(0.01f)
        .setColor(this.filters.currentFilters.contains(this.tooltip) ? ACTIVE_COLOR
                : this.filters.currentNegateFilters.contains(this.tooltip) ? NEGATE_COLOR : PANEL_COLOR)
                .setOnClick(button -> {
                    if (this.filters.currentFilters.contains(this.tooltip))
                    {
                        this.filters.currentFilters.remove(this.tooltip);
                        backgroundButton.setColor(PANEL_COLOR);
                        titleText.setColor(Color.WHITE);
                    }
                    else
                    {
                        this.filters.currentFilters.add(this.tooltip);
                        backgroundButton.setColor(ACTIVE_COLOR);
                        titleText.setColor(Color.DARK_GRAY);
                    }

                    if (this.onClick != null) {
                        this.onClick.invoke(this);
                    }
                })
                .setOnRightClick(button -> {
                    if (this.filters.currentNegateFilters.contains(this.tooltip))
                    {
                        this.filters.currentNegateFilters.remove(this.tooltip);
                        backgroundButton.setColor(PANEL_COLOR);
                    }
                    else
                    {
                        //CardKeywordFilters.CurrentFilters.remove(Tooltip);
                        this.filters.currentNegateFilters.add(this.tooltip);
                        backgroundButton.setColor(NEGATE_COLOR);
                    }
                    titleText.setColor(Color.WHITE);

                    if (this.onClick != null) {
                        this.onClick.invoke(this);
                    }
                });

        titleText = new EUILabel(EUIFontHelper.CardTooltipFont,
        new RelativeHitbox(hb, 0.5f, 1, baseTextOffsetX, baseTextOffsetY))
                .setFont(EUIFontHelper.CardTooltipFont, 0.8f)
                .setColor(this.filters.currentFilters.contains(this.tooltip) ? Color.DARK_GRAY : Color.WHITE)
        .setAlignment(0.5f, 0.49f) // 0.1f
        .setLabel(this.tooltip.title);

        countText = new EUILabel(EUIFontHelper.CardDescriptionFont_Normal,
                new RelativeHitbox(hb, 0.28f, 1, baseCountOffset, 0f))
                .setFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .setAlignment(0.5f, 0.51f) // 0.1f
                .setColor(Settings.GOLD_COLOR)
                .setLabel(this.filters.currentNegateFilters.contains(this.tooltip) ? "X" : cardCount);
    }

    public FilterKeywordButton setIndex(int index)
    {
        offX = (index % CardKeywordFilters.ROW_SIZE) * 1.06f;
        offY = -(Math.floorDiv(index,CardKeywordFilters.ROW_SIZE)) * 0.85f;
        RelativeHitbox.setPercentageOffset(backgroundButton.hb, offX, offY);
        RelativeHitbox.setPercentageOffset(titleText.hb, offX + baseTextOffsetX, offY + baseTextOffsetY);
        RelativeHitbox.setPercentageOffset(countText.hb, offX + baseCountOffset, offY);


        return this;
    }

    public FilterKeywordButton setCardCount(int count)
    {
        this.cardCount = count;
        boolean isNegate = filters.currentNegateFilters.contains(tooltip);
        boolean isContains = filters.currentFilters.contains(tooltip);
        countText
                .setLabel(isNegate ? "X" : count).setColor(count > 0 ? Settings.GOLD_COLOR : Color.DARK_GRAY);
        titleText.setColor((!isContains && count > 0) || isNegate ? Color.WHITE : Color.DARK_GRAY);
        backgroundButton
                .setColor(isContains ? ACTIVE_COLOR
                        : isNegate ? NEGATE_COLOR : PANEL_COLOR)
                .setInteractable(count != 0 || isNegate);

        return this;
    }

    public FilterKeywordButton setOnClick(ActionT1<FilterKeywordButton> onClick)
    {
        this.onClick = onClick;
        return this;
    }

    @Override
    public void updateImpl()
    {
        backgroundButton.updateImpl();
        titleText.updateImpl();
        countText.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        backgroundButton.renderImpl(sb);

        if (tooltip.icon != null) {
            if (cardCount != 0 || filters.currentNegateFilters.contains(tooltip)) {
                renderTooltipImage(sb);
            }
            else {
                EUIRenderHelpers.drawGrayscale(sb, this::renderTooltipImage);
            }
        }


        titleText.renderImpl(sb);
        if (cardCount >= 0) {
            countText.renderImpl(sb);
        }
        if (backgroundButton.hb.hovered) {
            float actualMX;
            float actualMY;
            if (CardCrawlGame.isPopupOpen) {
                actualMX = popupMX;
                actualMY = popupMY;
            }
            else {
                actualMX = InputHelper.mX;
                actualMY = InputHelper.mY;
            }


            EUITooltip.queueTooltip(tooltip, actualMX + 20 * Settings.scale, actualMY + 20 * Settings.scale);
        }
    }

    private void renderTooltipImage(SpriteBatch sb) {
        tooltip.renderTipEnergy(sb, tooltip.icon,
                hb.x + (offX + baseImageOffsetX) * hb.width,
                hb.y + (offY + baseImageOffsetY) * hb.height,
                28 * tooltip.iconmultiW,
                28 * tooltip.iconmultiH,
                CARD_ENERGY_IMG_WIDTH / tooltip.icon.getRegionWidth(),
                CARD_ENERGY_IMG_WIDTH / tooltip.icon.getRegionHeight(),
                cardCount == 0 ? Color.DARK_GRAY : Color.WHITE);
    }
}
