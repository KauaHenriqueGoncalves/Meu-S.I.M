package com.system.application.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class NoEmojiValidator
        implements ConstraintValidator<NoEmoji, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        return value.codePoints().noneMatch(NoEmojiValidator::isEmoji);
    }

    private static boolean isEmoji(int codePoint) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
        return block == Character.UnicodeBlock.EMOTICONS
                || block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS
                || block == Character.UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS
                || block == Character.UnicodeBlock.SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS
                || block == Character.UnicodeBlock.SYMBOLS_AND_PICTOGRAPHS_EXTENDED_A
                || block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS
                || block == Character.UnicodeBlock.DINGBATS
                || block == Character.UnicodeBlock.VARIATION_SELECTORS
                || (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF) // bandeiras (regional indicators)
                || codePoint == 0x200D                            // Zero Width Joiner (emoji combinados)
                || (codePoint >= 0xFE00 && codePoint <= 0xFE0F);  // variation selectors adicionais
    }
}
