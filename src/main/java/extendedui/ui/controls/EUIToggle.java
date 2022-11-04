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
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIFontHelper;

public class EUIToggle extends EUIHoverable
{
    public String text = "";
    public boolean toggled = false;
    public boolean interactable = true;

    public CInputAction controllerAction = CInputActionSet.select;
    public EUIImage untickedImage = new EUIImage(ImageMaster.COLOR_TAB_BOX_UNTICKED);
    public EUIImage tickedImage = new EUIImage(ImageMaster.COLOR_TAB_BOX_TICKED);
    public EUIImage backgroundImage = null;
    public Color defaultColor = Settings.CREAM_COLOR.cpy();
    public Color hoveredColor = Settings.GOLD_COLOR.cpy();
    public BitmapFont font = EUIFontHelper.CardTooltipTitleFont_Large;
    public float fontSize = 1;
    public float tickSize = 48;
    public ActionT1<Boolean> onToggle = null;

    public EUIToggle(AdvancedHitbox hb)
    {
        super(hb);
    }

    public EUIToggle SetFontColors(Color defaultColor, Color hoveredColor)
    {
        this.defaultColor = defaultColor.cpy();
        this.hoveredColor = hoveredColor.cpy();

        return this;
    }

    public EUIToggle SetControllerAction(CInputAction action)
    {
        this.controllerAction = action;

        return this;
    }

    public EUIToggle SetTickImage(EUIImage unticked, EUIImage ticked, float size)
    {
        this.untickedImage = unticked;
        this.tickedImage = ticked;
        this.tickSize = size;

        return this;
    }

    public EUIToggle SetInteractable(boolean interactable)
    {
        this.interactable = interactable;

        return this;
    }

    public EUIToggle SetFontSize(float fontSize)
    {
        this.fontSize = fontSize;

        return this;
    }

    public EUIToggle SetFont(BitmapFont font, float fontSize)
    {
        this.font = font;
        this.fontSize = fontSize;

        return this;
    }

    public EUIToggle SetText(String text)
    {
        this.text = text;

        return this;
    }

    public EUIToggle SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public EUIToggle SetBackground(EUIImage image)
    {
        this.backgroundImage = image;

        return this;
    }

    public EUIToggle SetBackground(Texture texture, Color color)
    {
        this.backgroundImage = EUIRenderHelpers.ForTexture(texture).SetHitbox(hb).SetColor(color);

        return this;
    }

    public EUIToggle SetOnToggle(ActionT1<Boolean> onToggle)
    {
        this.onToggle = onToggle;

        return this;
    }

    public EUIToggle SetToggle(boolean value)
    {
        this.toggled = value;

        return this;
    }

    public void Toggle()
    {
        Toggle(!toggled);
    }

    public void Toggle(boolean value)
    {
        if (toggled != value)
        {
            toggled = value;

            if (onToggle != null)
            {
                onToggle.Invoke(value);
            }
        }
    }

    public boolean IsToggled()
    {
        return toggled;
    }

    @Override
    public void Update()
    {
        super.Update();

        if (!interactable)
        {
            return;
        }

        if (EUI.TryHover(hb))
        {
            if (hb.justHovered)
            {
                CardCrawlGame.sound.playA("UI_HOVER", -0.3f);
            }

            if (hb.hovered && InputHelper.justClickedLeft)
            {
                hb.clickStarted = true;
            }
        }

        if (hb.clicked)
        {
            hb.clicked = false;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2f);

            Toggle();
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        if (backgroundImage != null)
        {
            if (backgroundImage.hb != null)
            {
                backgroundImage.Render(sb);
            }
            else
            {
                backgroundImage.RenderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);
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
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.3f * Settings.scale), hb.cY, fontColor);
            EUIRenderHelpers.ResetFont(font);
        }
        else
        {
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.3f * Settings.scale), hb.cY, fontColor);
        }

        EUIImage image = toggled ? tickedImage : untickedImage;
        if (image != null)
        {
            image.RenderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);

//            sb.setColor(fontColor);
//            sb.draw(image, hb.x + (tickSize / 6f) * Settings.scale, hb.cY - tickSize / 2f, tickSize / 2f, tickSize / 2f, tickSize, tickSize,
//                    Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);
        }

        hb.render(sb);
    }
}
