package org.kobjects.nativehtml.util;

import org.kobjects.nativehtml.dom.Element;
import org.kobjects.nativehtml.dom.HtmlCollection;

public class DebugDump {
	public static final void dump(Element element, String indent) {
		
		System.out.println(indent + "<" + element.getLocalName() + ">");
		
		String indent2 = indent + "  ";
		System.out.println(indent2 + "   // elementType: " + element.getElementType());
		System.out.println(indent2 + "   // class: " + element.getClass());
		System.out.println(indent2 + "   // computedStyle " + element.getComputedStyle());
		
		HtmlCollection children = element.getChildren();
		for (int i = 0; i < children.getLength(); i++) {
			dump(children.item(i), indent2);
		}
		System.out.println(indent + "</" + element.getLocalName() + ">");
		
	}
	

}
