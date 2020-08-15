/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.duration.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

abstract class OutputReplacer<S extends OutputStream, T extends Out<S>> implements ParameterResolver, AfterEachCallback {

    @NotNull
    abstract Class<? extends OutputReplacer<S, T>> getNamespaceKey();

    @NotNull
    abstract Class<T> getTargetType();

    @NotNull
    abstract S createNew();

    abstract void setStdIo(@NotNull PrintStream replacing);

    @NotNull
    abstract PrintStream getOriginal();

    @NotNull
    abstract T toParameterType(@NotNull S stream);

    static class Key<T extends Out<?>> {
        @NotNull final Class<?> testClass;
        @NotNull final Class<T> targetType;

        Key(@NotNull Class<?> testClass, @NotNull Class<T> targetType) {
            this.testClass = testClass;
            this.targetType = targetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key<?> key = (Key<?>) o;
            return testClass.equals(key.testClass) &&
                    targetType.equals(key.targetType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(testClass, targetType);
        }
    }

    @NotNull
    private Key<T> createKey(ExtensionContext context) {
        Class<?> requiredTestClass = context.getRequiredTestClass();
        Class<T> targetType = getTargetType();
        return new Key<>(requiredTestClass, targetType);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(getNamespaceKey());
        ExtensionContext.Store store = context.getStore(namespace);
        Key<T> key = createKey(context);
        PrintStream printStream = store.get(key, PrintStream.class);
        if (printStream != null) {
            setStdIo(printStream);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType().equals(getTargetType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(getNamespaceKey());
        ExtensionContext.Store store = extensionContext.getStore(namespace);
        Key<T> key = createKey(extensionContext);
        PrintStream printStream = store.get(key, PrintStream.class);
        if (printStream != null) {
            setStdIo(printStream);
            throw new ParameterResolutionException("cannot create same parameter[" + parameterContext.getParameter() + "] simultaneously.");
        }
        store.put(key, getOriginal());
        S stream = createNew();
        PrintStream newPrintStream = new PrintStream(stream, true, StandardCharsets.UTF_8);
        setStdIo(newPrintStream);
        return toParameterType(stream);
    }
}
