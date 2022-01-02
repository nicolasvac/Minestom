package net.minestom.server.instance.generator;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GenerationRequest {
    @NotNull GenerationUnit unit();

    @NotNull Instance instance();

    @NotNull List<GenerationUnit.Section> sections();

    default GenerationUnit.Section section(int offset) {
        return sections().get(offset);
    }
}
