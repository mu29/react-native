/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * <p>This source code is licensed under the MIT license found in the LICENSE file in the root
 * directory of this source tree.
 */
package com.facebook.react.views.text;

import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.Nullable;

public class ReactTypefaceUtils {
  public static final int UNSET = -1;

  public static int parseFontWeight(@Nullable String fontWeightString) {
    int fontWeightNumeric =
        fontWeightString != null ? parseNumericFontWeight(fontWeightString) : UNSET;
    int fontWeight = fontWeightNumeric != UNSET ? fontWeightNumeric : Typeface.NORMAL;

    if (fontWeight == 700 || "bold".equals(fontWeightString)) fontWeight = Typeface.BOLD;
    else if (fontWeight == 400 || "normal".equals(fontWeightString)) fontWeight = Typeface.NORMAL;

    return fontWeight;
  }

  public static int parseFontStyle(@Nullable String fontStyleString) {
    int fontStyle = UNSET;
    if ("italic".equals(fontStyleString)) {
      fontStyle = Typeface.ITALIC;
    } else if ("normal".equals(fontStyleString)) {
      fontStyle = Typeface.NORMAL;
    }

    return fontStyle;
  }

  public static Typeface applyStyles(@Nullable Typeface typeface,
                                         int style, int weight, @Nullable String family, AssetManager assetManager) {
    int oldStyle;
    if (typeface == null) {
      oldStyle = 0;
    } else {
      oldStyle = typeface.getStyle();
    }

    int want = 0;
    if ((weight == Typeface.BOLD)
        || ((oldStyle & Typeface.BOLD) != 0 && weight == ReactTextShadowNode.UNSET)) {
      want |= Typeface.BOLD;
    }

    if ((style == Typeface.ITALIC)
        || ((oldStyle & Typeface.ITALIC) != 0 && style == ReactTextShadowNode.UNSET)) {
      want |= Typeface.ITALIC;
    }

    if (family != null) {
      typeface = ReactFontManager.getInstance().getTypeface(family, want, weight, assetManager);
    } else if (typeface != null) {
      // TODO(t9055065): Fix custom fonts getting applied to text children with different style
      typeface = Typeface.create(typeface, want);
    }

    if (typeface != null) {
      return typeface;
    } else {
      return Typeface.defaultFromStyle(want);
    }
  }

  /**
   * Return -1 if the input string is not a valid numeric fontWeight (100, 200, ..., 900), otherwise
   * return the weight.
   */
  private static int parseNumericFontWeight(String fontWeightString) {
    // This should be much faster than using regex to verify input and Integer.parseInt
    return fontWeightString.length() == 3
        && fontWeightString.endsWith("00")
        && fontWeightString.charAt(0) <= '9'
        && fontWeightString.charAt(0) >= '1'
        ? 100 * (fontWeightString.charAt(0) - '0')
        : UNSET;
  }
}
