package net.minestom.server.instance.generator;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface GenerationRequest {
    @NotNull Instance instance();

    @NotNull List<Integer> sections();

    interface Chunk extends GenerationRequest {
        @Override
        default @NotNull List<Integer> sections() {
            var instance = instance();
            List<Integer> sections = new ArrayList<>();
            for (int y = instance.getSectionMinY(); y <= instance.getSectionMaxY(); y++) {
                sections.add(y);
            }
            return List.copyOf(sections);
        }
    }
}
