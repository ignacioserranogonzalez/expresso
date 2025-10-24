package una.paradigmas.pattern;

import java.util.List;
public record DataPattern(String constructor, List<Pattern> subPatterns)
        implements Pattern {

    public DataPattern(String constructor, List<Pattern> subPatterns) {
        this.constructor = constructor;
        this.subPatterns = subPatterns != null ? subPatterns : List.of();
    }
}
