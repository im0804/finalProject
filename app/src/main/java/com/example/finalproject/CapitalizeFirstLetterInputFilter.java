package com.example.finalproject;

import android.text.InputFilter;
import android.text.Spanned;

public class CapitalizeFirstLetterInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char currentChar = source.charAt(i);
            char prevChar = (dstart > 0) ? dest.charAt(dstart - 1) : ' ';

            if (prevChar == ' ' && Character.isLowerCase(currentChar)) {
                builder.append(Character.toUpperCase(currentChar));
            } else {
                builder.append(currentChar);
            }
        }
        return builder.toString();
    }
}
