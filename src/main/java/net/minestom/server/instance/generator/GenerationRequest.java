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

    @NotNull UnitProperty property(@NotNull GenerationUnit unit);

    default @NotNull UnitProperty.Section sectionProperty(@NotNull GenerationUnit.Section section) {
        return (UnitProperty.Section) property(section);
    }

    default @NotNull UnitProperty.Chunk chunkProperty(@NotNull GenerationUnit.Chunk chunk) {
        return (UnitProperty.Chunk) property(chunk);
    }

    default @NotNull UnitProperty.Region regionProperty(@NotNull GenerationUnit.Region region) {
        return (UnitProperty.Region) property(region);
    }
}
