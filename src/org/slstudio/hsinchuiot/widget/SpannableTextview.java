package org.slstudio.hsinchuiot.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class SpannableTextview extends TextView {

	public SpannableTextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SpannableTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SpannableTextview(Context context) {
		super(context);
	}

	public void setSpannableText(String[] text, int[] color) {
		StringBuilder sbuilder = new StringBuilder();
		for (String s : text) {
			sbuilder.append(s);
		}
		String textString = sbuilder.toString();
		setText(textString);
		SpannableStringBuilder style = new SpannableStringBuilder(getText());
		int index = 0;
		for (int i = 0; i < text.length; i++) {
			int start = index;
			int end = start + text[i].length();
			style.setSpan(new ForegroundColorSpan(color[i]), index, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			index = end;
		}

		setText(style);
	}

}
