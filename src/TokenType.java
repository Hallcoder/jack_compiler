public enum TokenType {
    SYMBOL("symbol"),
    KEYWORD("keyword"),
    IDENTIFIER("identifier"),
    STRING_CONSTANT("stringConstant"),
    NUMBER_CONSTANT("numberConstant"),;

    private String displayName;

    TokenType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
