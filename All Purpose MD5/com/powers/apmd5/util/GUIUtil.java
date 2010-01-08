package com.powers.apmd5.util;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

public class GUIUtil {

	public static void setImage(final Label l, final String url){
		l.setImage(SWTResourceManager.getImage(GUIUtil.class, url));
	}

}
