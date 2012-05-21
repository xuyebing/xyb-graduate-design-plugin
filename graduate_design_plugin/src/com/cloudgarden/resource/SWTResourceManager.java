package com.cloudgarden.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * Class to manage SWT resources (Font, Color, Image and Cursor) There are no
 * restrictions on the use of this code.
 * 
 * You may change this code and your changes will not be overwritten, but if you
 * change the version number below then this class will be completely
 * overwritten by Jigloo. #SWTResourceManager:version4.0.0#
 */
public class SWTResourceManager {

	private static DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			SWTResourceManager.users.remove(e.getSource());
			if (SWTResourceManager.users.size() == 0)
				dispose();
		}
	};

	private static SWTResourceManager instance = new SWTResourceManager();

	private static HashMap<Object, Object> resources = new HashMap<Object, Object>();

	private static Vector<Widget> users = new Vector<Widget>();

	/**
	 * 
	 */
	public static void dispose() {
		Iterator<Object> it = SWTResourceManager.resources.keySet().iterator();
		while (it.hasNext()) {
			Object resource = SWTResourceManager.resources.get(it.next());
			if (resource instanceof Font)
				((Font) resource).dispose();
			else if (resource instanceof Color)
				((Color) resource).dispose();
			else if (resource instanceof Image)
				((Image) resource).dispose();
			else if (resource instanceof Cursor)
				((Cursor) resource).dispose();
		}
		SWTResourceManager.resources.clear();
	}

	/**
	 * @param pRed
	 * @param pGreen
	 * @param pBlue
	 * @return The color
	 */
	public static Color getColor(int pRed, int pGreen, int pBlue) {
		String name = "COLOR:" + pRed + "," + pGreen + "," + pBlue;
		if (SWTResourceManager.resources.containsKey(name))
			return (Color) SWTResourceManager.resources.get(name);
		Color color = new Color(Display.getDefault(), pRed, pGreen, pBlue);
		SWTResourceManager.resources.put(name, color);
		return color;
	}

	/**
	 * @param pType
	 * @return the cursor
	 */
	public static Cursor getCursor(int pType) {
		String name = "CURSOR:" + pType;
		if (SWTResourceManager.resources.containsKey(name))
			return (Cursor) SWTResourceManager.resources.get(name);
		Cursor cursor = new Cursor(Display.getDefault(), pType);
		SWTResourceManager.resources.put(name, cursor);
		return cursor;
	}

	/**
	 * @param pName
	 * @param pSize
	 * @param pStyle
	 * @return The font
	 */
	public static Font getFont(String pName, int pSize, int pStyle) {
		return getFont(pName, pSize, pStyle, false, false);
	}

	/**
	 * @param pName
	 * @param pSize
	 * @param pStyle
	 * @param pStrikeout
	 * @param pUnderline
	 * @return The font
	 */
	public static Font getFont(String pName, int pSize, int pStyle,
			boolean pStrikeout, boolean pUnderline) {
		String fontName = pName + "|" + pSize + "|" + pStyle + "|" + pStrikeout
				+ "|" + pUnderline;
		if (SWTResourceManager.resources.containsKey(fontName))
			return (Font) SWTResourceManager.resources.get(fontName);
		FontData fontData = new FontData(pName, pSize, pStyle);
		if (pStrikeout || pUnderline) {
			try {
				Class logFontClass = Class
						.forName("org.eclipse.swt.internal.win32.LOGFONT");
				Object logFont = FontData.class.getField("data").get(fontData);
				if (logFont != null && logFontClass != null) {
					if (pStrikeout)
						logFontClass.getField("lfStrikeOut").set(logFont,
								new Byte((byte) 1));
					if (pUnderline)
						logFontClass.getField("lfUnderline").set(logFont,
								new Byte((byte) 1));
				}
			} catch (Throwable throwable) {
				System.err
						.println("Unable to set underline or strikeout"
								+ " (probably on a non-Windows platform). "
								+ throwable);
			}
		}
		Font font = new Font(Display.getDefault(), fontData);
		SWTResourceManager.resources.put(fontName, font);
		return font;
	}

	/**
	 * @param pUrl
	 * @return The image
	 */
	public static Image getImage(String pUrl) {
		try {
			pUrl = pUrl.replace('\\', '/');
			if (pUrl.startsWith("/"))
				pUrl = pUrl.substring(1);
			if (SWTResourceManager.resources.containsKey(pUrl))
				return (Image) SWTResourceManager.resources.get(pUrl);
			Image image = new Image(Display.getDefault(),
					SWTResourceManager.instance.getClass().getClassLoader()
							.getResourceAsStream(pUrl));
			if (image != null)
				SWTResourceManager.resources.put(pUrl, image);
			return image;
		} catch (Exception exception) {
			System.err
					.println("SWTResourceManager.getImage: Error getting image "
							+ pUrl + ", " + exception);
			return null;
		}
	}

	/**
	 * @param pUrl
	 * @param pWidget
	 * @return The image
	 */
	public static Image getImage(String pUrl, Control pWidget) {
		Image image = getImage(pUrl);
		image.setBackground(pWidget.getBackground());
		return image;
	}

	/**
	 * This method should be called by *all* Widgets which use resources
	 * provided by this SWTResourceManager. When widgets are disposed, they are
	 * removed from the "users" Vector, and when no more registered Widgets are
	 * left, all resources are disposed.
	 * <P>
	 * If this method is not called for all Widgets then it should not be called
	 * at all, and the "dispose" method should be explicitly called after all
	 * resources are no longer being used.
	 * 
	 * @param pWidget
	 */
	public static void registerResourceUser(Widget pWidget) {
		if (SWTResourceManager.users.contains(pWidget))
			return;
		SWTResourceManager.users.add(pWidget);
		pWidget.addDisposeListener(disposeListener);
	}
}
