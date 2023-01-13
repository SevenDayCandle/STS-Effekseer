package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;

public class EUIToggle extends EUIHoverable
{
    public static final float DEFAULT_TICK = 48;
    public String text = "";
    public boolean toggled = false;
    public boolean interactable = true;

    public CInputAction controllerAction = CInputActionSet.select;
    public Color defaultColor = Settings.CREAM_COLOR.cpy();
    public Color hoveredColor = Settings.GOLD_COLOR.cpy();
    public EUIImage untickedImage;
    public EUIImage tickedImage;
    public EUIImage backgroundImage = null;
    public BitmapFont font = EUIFontHelper.cardtooltiptitlefontLarge;
    public float fontSize = 1;
    public float tickSize = DEFAULT_TICK;
    public ActionT1<Boolean> onToggle = null;

    public EUIToggle(EUIHitbox hb)
    {
        this(hb, new EUIImage(ImageMaster.COLOR_TAB_BOX_UNTICKED), new EUIImage(ImageMaster.COLOR_TAB_BOX_TICKED), DEFAULT_TICK);
    }

    public EUIToggle(EUIHitbox hb, EUIImage untickedImage, EUIImage tickedImage)
    {
        this(hb, untickedImage, tickedImage, untickedImage.srcWidth);
    }

    public EUIToggle(EUIHitbox hb, EUIImage untickedImage, EUIImage tickedImage, float tickSize)
    {
        super(hb);
        this.untickedImage = untickedImage;
        this.tickedImage = tickedImage;
        this.tickSize = tickSize;
    }

    public EUIToggle setFontColors(Color defaultColor, Color hoveredColor)
    {
        this.defaultColor = defaultColor.cpy();
        this.hoveredColor = hoveredColor.cpy();

        return this;
    }

    public EUIToggle setControllerAction(CInputAction action)
    {
        this.controllerAction = action;

        return this;
    }

    public EUIToggle setTickImage(EUIImage unticked, EUIImage ticked)
    {
        return setTickImage(unticked, ticked, unticked.srcWidth);
    }

    public EUIToggle setTickImage(EUIImage unticked, EUIImage ticked, float size)
    {
        this.untickedImage = unticked;
        this.tickedImage = ticked;
        this.tickSize = size;

        return this;
    }

    public EUIToggle setInteractable(boolean interactable)
    {
        this.interactable = interactable;

        return this;
    }

    public EUIToggle setFontSize(float fontSize)
    {
        this.fontSize = fontSize;

        return this;
    }

    public EUIToggle setFont(BitmapFont font, float fontSize)
    {
        this.font = font;
        this.fontSize = fontSize;

        return this;
    }

    public EUIToggle setText(String text)
    {
        this.text = text;

        return this;
    }

    public EUIToggle setPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIToggle setBackground(EUIImage image)
    {
        this.backgroundImage = image;

        return this;
    }

    public EUIToggle setBackground(Texture texture, Color color)
    {
        this.backgroundImage = new EUIImage(texture, Color.WHITE).setHitbox(hb).setColor(color);

        return this;
    }

    public EUIToggle setOnToggle(ActionT1<Boolean> onToggle)
    {
        this.onToggle = onToggle;

        return this;
    }

    public EUIToggle setToggle(boolean value)
    {
        this.toggled = value;

        return this;
    }

    public void toggle()
    {
        toggle(!toggled);
    }

    public void toggle(boolean value)
    {
        if (toggled != value)
        {
            toggled = value;

            if (onToggle != null)
            {
                onToggle.invoke(value);
            }
        }
    }

    public EUIToggle autosize() {
        return autosize(1f, 1f);
    }

    public EUIToggle autosize(Float resizeMultiplier, Float resizeHeight) {
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
        return tickSize * Settings.scale + EUISmartText.getSmartWidth(font, text, Settings.WIDTH, 0f);
    }

    public boolean isToggled()
    {
        return toggled;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();

        if (!interactable)
        {
            return;
        }

        if (hb.justHovered)
        {
            CardCrawlGame.sound.playA("UI_HOVER", -0.3f);
        }

        if (hb.hovered && InputHelper.justClickedLeft)
        {
            hb.clickStarted = true;
        }

        if (hb.clicked)
        {
            hb.clicked = false;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2f);

            toggle();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (backgroundImage != null)
        {
            if (backgroundImage.hb != null)
            {
                backgroundImage.renderImpl(sb);
            }
            else
            {
                backgroundImage.renderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);
            }
        }

        Color fontColor;
        if (!interactable)
        {
            fontColor = TEXT_DISABLED_COLOR;
        }
        else if (hb.hovered)
        {
            fontColor = hoveredColor;
        }
        else
        {
            fontColor = defaultColor;
        }

        if (fontSize != 1)
        {
            font.getData().setScale(fontSize);
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.1f * Settings.scale), hb.cY, fontColor);
            EUIRenderHelpers.resetFont(font);
        }
        else
        {
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.1f * Settings.scale), hb.cY, fontColor);
        }

        EUIImage image = toggled ? tickedImage : untickedImage;
        if (image != null)
        {
            image.renderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);
            if (hb.hovered)
            {
                image.renderCentered(sb, EUIRenderHelpers.BlendingMode.Glowing, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);
            }
        }

        hb.render(sb);
    }
}
