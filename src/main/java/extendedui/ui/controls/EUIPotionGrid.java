package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.PotionInfo;

public class EUIPotionGrid extends EUIItemGrid<PotionInfo> {
    public boolean shouldEnlargeHovered = true;

    public EUIPotionGrid() {
        this(0.5f, true);
    }

    public EUIPotionGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
    }

    public EUIPotionGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public EUIPotionGrid add(AbstractPotion potion) {
        group.add(new PotionInfo(potion));

        return this;
    }

    public EUIPotionGrid remove(AbstractPotion potion) {
        group.group.removeIf(rInfo -> rInfo.potion == potion);

        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hovered != null) {
            EUIGameUtils.renderPotionTip(hovered.potion);
        }
    }

    @Override
    protected float getScrollDistance(PotionInfo potion, int index) {
        float scrollDistance = 1f / getRowCount();
        if (potion.potion.posY > drawTopY) {
            return -scrollDistance;
        }
        else if (potion.potion.posY < 0) {
            return scrollDistance;
        }
        return 0;
    }

    @Override
    public void updateItemPosition(PotionInfo potion, float x, float y) {
        potion.potion.posX = EUIUtils.lerpSnap(potion.potion.posX, x, LERP_SPEED);
        potion.potion.posY = EUIUtils.lerpSnap(potion.potion.posY, y, LERP_SPEED);
    }

    @Override
    public Hitbox getHitbox(PotionInfo item) {
        return item.potion.hb;
    }

    @Override
    public void forceUpdateItemPosition(PotionInfo potion, float x, float y) {
        potion.potion.posX = x;
        potion.potion.posY = y;
        potion.potion.hb.update();
        potion.potion.hb.move(potion.potion.posX, potion.potion.posY);
    }

    @Override
    protected void updateHoverLogic(PotionInfo potion, int i) {
        potion.potion.hb.update();
        potion.potion.hb.move(potion.potion.posX, potion.potion.posY);

        if (potion.potion.hb.hovered) {
            hovered = potion;
            hoveredIndex = i;
            if (!shouldEnlargeHovered) {
                potion.potion.scale = targetScale;
            }
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, PotionInfo potion) {
        potion.potion.render(sb);
    }
}
