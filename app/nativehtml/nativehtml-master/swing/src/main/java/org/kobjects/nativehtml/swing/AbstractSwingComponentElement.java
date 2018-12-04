package org.kobjects.nativehtml.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.util.HashMap;

import javax.swing.JComponent;

import org.kobjects.nativehtml.css.CssEnum;
import org.kobjects.nativehtml.css.CssProperty;
import org.kobjects.nativehtml.css.CssStyleDeclaration;
import org.kobjects.nativehtml.dom.Document;
import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.util.ElementImpl;

public abstract class AbstractSwingComponentElement extends JComponent implements org.kobjects.nativehtml.layout.ComponentElement {
	private final Document document;
	protected final String elementName;
	private HashMap<String, String> attributes;
	private CssStyleDeclaration style;
	protected CssStyleDeclaration computedStyle;
	// Set in setBounds, use for padding in paint
	private float containingBoxWidth;
	
	protected AbstractSwingComponentElement(Document document, String name) {
		this.document = document;
		this.elementName = name;
	}
	
	@Override
	public String getLocalName() {
		return elementName;
	}

	@Override
	public void setAttribute(String name, String value) {
		if (attributes == null) {
			this.attributes = new HashMap<>();
		}
		attributes.put(name, value);
        if (name.equals("style")) {
        	style = CssStyleDeclaration.fromString(value);
        }
	}

	@Override
	public String getAttribute(String name) {
		return attributes == null ? null : attributes.get(name);
	}

	@Override
	public Element getParentElement() {
		return getParent() instanceof Element ? ((Element) getParent()) : null;
	}

	@Override
	public void setParentElement(Element parent) {
	}


	@Override
	public CssStyleDeclaration getStyle() {
		if (style == null) {
			style = new CssStyleDeclaration();
		}
		return style;
	}

	@Override
	public CssStyleDeclaration getComputedStyle() {
		return computedStyle;
	}

	@Override
	public void setComputedStyle(CssStyleDeclaration computedStyle) {
		this.computedStyle = computedStyle;
	}
	
	@Override
	public String getTextContent() {
		return ElementImpl.getTextContent(this);
	}

	@Override
	public void setTextContent(String textContent) {
		System.err.println("setTextContent ignored for " + getElementType() + ": " + elementName + " text: " + textContent);
	}

	
	@Override
	public void setBorderBoxBounds(float x, float y, float width, float height, float containingBoxWidth) {
	    float scale = document.getSettings().getScale();
		setBounds(Math.round(x * scale), Math.round(y * scale), Math.round(width * scale), Math.round(height * scale));	
		this.containingBoxWidth = containingBoxWidth;
	}
	
	@Override
	public void moveRelative(float dx, float dy) {
      float scale = document.getSettings().getScale();
      setLocation(getX() + Math.round(dx * scale), getY() + Math.round(dy * scale));
	}
	
	static private Color createColor(int argb) {
		return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255);
	}
	
	private void drawBackground(Graphics2D g2d, int x, int y, int w, int h) {
	  CssStyleDeclaration style = getComputedStyle();
	  if (style.isSet(CssProperty.BACKGROUND_COLOR)) {
	    g2d.setColor(createColor(style.getColor(CssProperty.BACKGROUND_COLOR)));
	    g2d.fillRect(x, y, w, h);
	  }

	  if (!style.isSet(CssProperty.BACKGROUND_IMAGE)) {
	    return;
	  }

	  String bgImage = style.getString(CssProperty.BACKGROUND_IMAGE);
	  Image image = ((SwingPlatform) getOwnerDocument().getPlatform()).getImage(this, getOwnerDocument().getUrl().resolve(bgImage));
	  
	  if (image == null) {
	    return;
	  }

	  Shape savedClip = g2d.getClip(); 
	  g2d.clipRect(x, y, w, h);
	  CssEnum repeat = style.getEnum(CssProperty.BACKGROUND_REPEAT);
      int bgY = 0;
      int bgX = 0;
      if (repeat == CssEnum.REPEAT_Y || repeat == CssEnum.REPEAT) {
        do {
          if (repeat == CssEnum.REPEAT) {
            int currentBgX = bgX;
            do {
              g2d.drawImage(image, x + currentBgX, y + bgY, null);
              currentBgX += image.getWidth(null);
            } while (currentBgX < w);
          } else {
            g2d.drawImage(image, x + bgX, y + bgY, null);
          }
          bgY += image.getHeight(null);
        } while (bgY < h);
      } else if (repeat == CssEnum.REPEAT_X) {
        do {
          g2d.drawImage(image, x + bgX, y + bgY, null);
          bgX += image.getWidth(null);
        } while (bgX < w);
      } else {
        g2d.drawImage(image, x + bgX, y + bgY, null);
      }
      
      g2d.setClip(savedClip);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		float scale = getOwnerDocument().getSettings().getScale();
		int borderLeft = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_LEFT_WIDTH, containingBoxWidth));
		int borderRight = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_RIGHT_WIDTH, containingBoxWidth));
		int borderTop = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_TOP_WIDTH, containingBoxWidth));
		int borderBottom = Math.round(scale * computedStyle.getPx(CssProperty.BORDER_BOTTOM_WIDTH, containingBoxWidth));

		int w = getWidth() ;
		int h = getHeight();

		// Background paint area is specified using 'background-clip' property, and default value of it
	    // is 'border-box'
		drawBackground(g2d, borderLeft, borderTop, getWidth() - borderRight, getHeight() - borderBottom);
		
		if (borderTop > 0 && computedStyle.getEnum(CssProperty.BORDER_TOP_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_TOP_COLOR)));
			int dLeft = (borderLeft << 8) / borderTop;
			int dRight = (borderRight << 8) / borderTop;
			for (int i = 0; i < borderTop; i++) {
				g2d.drawLine(
						((i * dLeft) >> 8), i,
						w - 1 - ((i * dRight) >> 8), i);
		      	}
		}
		if (borderRight > 0 && computedStyle.getEnum(CssProperty.BORDER_RIGHT_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_RIGHT_COLOR)));
			int dTop = (borderTop << 8) / borderRight;
			int dBottom = (borderBottom << 8) / borderRight;
			for (int i = 0; i < borderRight; i++) {
				g2d.drawLine(
						w - 1 - i, ((i * dTop) >> 8),
						w - 1 - i, h - 1 - ((i * dBottom) >> 8));
		    }
		}
		if (borderBottom > 0 && computedStyle.getEnum(CssProperty.BORDER_BOTTOM_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_BOTTOM_COLOR)));
			int dLeft = (borderLeft << 8) / borderBottom;
		    int dRight = (borderRight << 8) / borderBottom;
		    for (int i = 0; i < borderBottom; i++) {
		       g2d.drawLine(
		            ((i * dLeft) >> 8), h - 1 - i,
		            w - 1 - ((i * dRight) >> 8) - 1, h - 1 - i);
		    }
		}
		if (borderLeft > 0 && computedStyle.getEnum(CssProperty.BORDER_LEFT_STYLE) != CssEnum.NONE) {
			g2d.setColor(createColor(computedStyle.getColor(CssProperty.BORDER_LEFT_COLOR)));
		    int dTop = (borderTop << 8) / borderLeft;
		    int dBottom = (borderBottom << 8) / borderLeft;
		    for (int i = 0; i < borderLeft; i++) {
		        g2d.drawLine(
		            i, ((i * dTop) >> 8),
		            i, h - 1 - ((i * dBottom) >> 8));
	      }
	    }
	}
	
	
	@Override
	public Document getOwnerDocument() {
	  return document;
	}

}
