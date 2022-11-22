package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIRelicGrid;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class RelicSortHeader extends EUIBase implements SortHeaderButtonListener
{
    public static RelicSortHeader Instance;
    public static final float START_X = screenW(0.5f) - CardLibSortHeader.SPACE_X * 1.45f;

    private SortHeaderButton lastUsedButton;
    protected boolean isAscending;
    protected boolean snapToGroup;
    protected float baseY = Settings.HEIGHT * 0.85f;
    protected SortHeaderButton rarityButton;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton colorButton;
    protected SortHeaderButton seenButton;
    public SortHeaderButton[] buttons;
    public EUIRelicGrid grid;
    public ArrayList<EUIRelicGrid.RelicInfo> originalGroup;

    public RelicSortHeader(EUIRelicGrid grid)
    {
        this.grid = grid;
        Instance = this;
        float xPosition = START_X;
        this.rarityButton = new SortHeaderButton(CardLibSortHeader.TEXT[0], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.colorButton = new SortHeaderButton(EUIRM.Strings.uiColors, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.seenButton = new SortHeaderButton(EUIRM.Strings.uiSeen, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.rarityButton, this.nameButton, this.colorButton, this.seenButton};
    }

    public RelicSortHeader setBaseY(float value)
    {
        this.baseY = value;
        return this;
    }

    public RelicSortHeader snapToGroup(boolean value)
    {
        this.snapToGroup = value;
        return this;
    }

    public RelicSortHeader setGrid(EUIRelicGrid grid) {
        EUI.RelicFilters.clear(false, true);
        this.grid = grid;
        this.originalGroup = new ArrayList<>(grid.relicGroup);

        if (RelicKeywordFilters.CustomModule != null) {
            RelicKeywordFilters.CustomModule.processGroup(EUIUtils.map(grid.relicGroup, r -> r.relic));
        }
        for (SortHeaderButton button : buttons)
        {
            button.reset();
        }

        return this;
    }

    @Override
    public void updateImpl()
    {
        float scrolledY = snapToGroup && this.grid != null && this.grid.relicGroup.size() > 0 ? this.grid.relicGroup.get(0).relic.currentY + 230.0F * Settings.yScale : baseY;
        for (SortHeaderButton button : buttons)
        {
            button.update();
            button.updateScrollPosition(scrolledY);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        for (SortHeaderButton button : buttons)
        {
            button.render(sb);
        }
    }

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending)
    {
        if (grid != null)
        {
            if (button == this.rarityButton)
            {
                this.grid.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.nameButton)
            {
                this.grid.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.relic.name, b.relic.name)) * (isAscending ? 1 : -1));
            }
            else if (button == this.colorButton)
            {
                this.grid.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.seenButton)
            {
                this.grid.relicGroup.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
            }
            else
            {
                this.grid.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
                this.grid.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
                this.grid.relicGroup.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
            }
        }
        for (SortHeaderButton eB : buttons)
        {
            eB.setActive(eB == button);
        }
    }

    public void updateForFilters() {
        if (this.grid != null) {
            if (EUI.RelicFilters.areFiltersEmpty()) {
                this.grid.relicGroup = originalGroup;
            }
            else {
                this.grid.relicGroup = EUI.RelicFilters.applyInfoFilters(originalGroup);
            }
            didChangeOrder(lastUsedButton, isAscending);
            EUI.RelicFilters.refresh(EUIUtils.map(grid.relicGroup, group -> group.relic));
        }
    }

    public ArrayList<AbstractRelic> getRelics()
    {
        return EUIUtils.map(grid.relicGroup, r -> r.relic);
    }

    public ArrayList<AbstractRelic> getOriginalRelics()
    {
        return EUIUtils.map(originalGroup, r -> r.relic);
    }

    protected int sortBySeen(EUIRelicGrid.RelicInfo a, EUIRelicGrid.RelicInfo b)
    {
        int aValue = a == null || a.locked ? 2 : a.relic.isSeen ? 1 : 0;
        int bValue = b == null || b.locked ? 2 : b.relic.isSeen ? 1 : 0;
        return aValue - bValue;
    }
}
