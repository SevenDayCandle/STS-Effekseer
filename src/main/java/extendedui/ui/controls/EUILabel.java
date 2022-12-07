package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUILabel extends EUIHoverable
{
    public String text;
    public boolean smartText;
    public boolean smartTextResize;
    public Color textColor;
    public float verticalRatio;
    public float horizontalRatio;
    public float fontScale;
    protected BitmapFont font;
    private boolean smartPadEnd;

    public EUILabel(BitmapFont font)
    {
        this(font, new EUIHitbox(0, 0));
    }

    public EUILabel(BitmapFont font, EUIHitbox hb)
    {
        super(hb);
        this.smartText = true;
        this.verticalRatio = 0.85f;
        this.horizontalRatio = 0.1f;
        this.textColor = Color.WHITE;
        this.fontScale = 1;
        this.font = font;
        this.text = "";
    }

    public EUILabel makeCopy()
    {
        return (EUILabel) new EUILabel(font, new EUIHitbox(hb))
                .setAlignment(verticalRatio, horizontalRatio, smartText)
                .setColor(textColor)
                .setFont(font, fontScale)
                .setLabel(text)
                .setTooltip(tooltip);
    }

    public EUILabel setLabel(Object content)
    {
        this.text = String.valueOf(content);

        return this;
    }

    public EUILabel setLabel(String text)
    {
        this.text = text;

        return this;
    }

    public EUILabel setLabel(String format, Object... args)
    {
        this.text = EUIUtils.format(format, args);

        return this;
    }

    public EUILabel setFont(BitmapFont font)
    {
        return setFont(font, 1);
    }

    public EUILabel setFont(BitmapFont font, float fontScale)
    {
        this.font = font;
        this.fontScale = fontScale;

        return this;
    }

    public EUILabel setFontScale(float fontScale)
    {
        this.fontScale = fontScale;

        return this;
    }

    public EUILabel setPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio)
    {
        return setAlignment(verticalRatio, horizontalRatio, false);
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio, boolean smartText)
    {
        return setAlignment(verticalRatio, horizontalRatio, smartText, true);
    }

    public EUILabel setAlignment(float verticalRatio, float horizontalRatio, boolean smartText, boolean smartPadEnd)
    {
        this.verticalRatio = verticalRatio;
        this.horizontalRatio = horizontalRatio;
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;

        return this;
    }

    public EUILabel setSmartText(boolean smartText) {
        return setSmartText(smartText, true);
    }

    public EUILabel setSmartText(boolean smartText, boolean smartPadEnd) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        return this;
    }

    public EUILabel setSmartText(boolean smartText, boolean smartPadEnd, boolean smartTextResize) {
        this.smartText = smartText;
        this.smartPadEnd = smartPadEnd;
        this.smartTextResize = smartTextResize;
        return this;
    }

    public EUILabel setColor(Color textColor)
    {
        this.textColor = textColor.cpy();

        return this;
    }

    public EUILabel autosize() {
        return autosize(1f, 1f);
    }

    public EUILabel autosize(Float resizeMultiplier, Float resizeHeight) {
        if (resizeMultiplier != null) {
            this.hb.width = getAutoWidth();
        }
        if (resizeHeight != null) {
            this.hb.height = getAutoHeight();
        }

        return this;
    }

    public float getAutoHeight() {
        return EUISmartText.getSmartHeight(font, text, Settings.WIDTH);
    }

    public float getAutoWidth() {
        return EUISmartText.getSmartWidth(font, text, Settings.WIDTH, 0f);
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        render(sb, hb);

        hb.render(sb);
    }

    public void render(SpriteBatch sb, Hitbox hb)
    {
        font.getData().setScale(fontScale);

        if (smartText)
        {
            final float step = hb.width * horizontalRatio;
            EUISmartText.write(sb, font, text, hb.x + step, hb.y + (hb.height * verticalRatio),
            smartPadEnd ? hb.width - (step * 2) : hb.width, font.getLineHeight(), textColor, smartTextResize);
        }
        else if (horizontalRatio < 0.5f)
        {
            final float step = hb.width * horizontalRatio;
            FontHelper.renderFontLeft(sb, font, text, hb.x + step, hb.y + hb.height * verticalRatio, textColor);
        }
        else if (horizontalRatio > 0.5f)
        {
            final float step = hb.width * (1-horizontalRatio) * 2;
            FontHelper.renderFontRightAligned(sb, font, text, hb.x + hb.width - step, hb.y + hb.height * verticalRatio, textColor);
        }
        else
        {
            FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.y + hb.height * verticalRatio, textColor);
        }

        EUIRenderHelpers.resetFont(font);
    }
}
