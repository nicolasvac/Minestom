package net.minestom.server.instance.generator;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public interface GenerationRequest {
    @NotNull Instance instance();
}
