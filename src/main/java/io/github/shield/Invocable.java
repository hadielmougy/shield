package io.github.shield;

import java.util.function.Supplier;

public interface Invocable {

    Object doInvoke(Supplier supplier);
}
