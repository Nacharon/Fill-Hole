package me.nacharon.fillhole.api.fawe.mask;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.registry.SimpleInputParser;

import java.util.List;

/**
 * A parser for the translucent mask used in FAWE.
 */
public class TranslucentMaskParser extends SimpleInputParser<Mask> {

    private final List<String> aliases = ImmutableList.of("#translucent");

    /**
     * Constructs a TranslucentMaskParser.
     *
     * @param worldEdit The WorldEdit instance.
     */
    public TranslucentMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    /**
     * Gets the list of matched aliases for the parser.
     *
     * @return A list of aliases.
     */
    @Override
    public List<String> getMatchedAliases() {
        return aliases;
    }

    /**
     * Parses input to create a TranslucentMask.
     *
     * @param input   The input string.
     * @param context The parser context.
     * @return A new TranslucentMask instance.
     */
    @Override
    public Mask parseFromSimpleInput(String input, ParserContext context) {
        return new TranslucentMask(context.getExtent());
    }
}