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
package org.mikeneck.duration;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

interface Either<@NotNull L, @NotNull R> {

    boolean isRight();

    default boolean isLeft() {
        return !isRight();
    }

    @NotNull
    <@NotNull N> Either<L, N> map(@NotNull Function<@NotNull ? super R, @NotNull ? extends N> mapping);

    @NotNull
    R orElse(@NotNull Function<@NotNull ? super L, @NotNull ? extends R> mapping);

    @NotNull
    default R get() {
        throw new IllegalStateException("invalid state to call get");
    }

    @NotNull
    default L error() {
        throw new IllegalStateException("invalid state to call error");
    }

    @NotNull
    static <@NotNull L, @NotNull R> Either<L, R> left(@NotNull L left) {
        return new Left<>(left);
    }

    @NotNull
    static <@NotNull L, @NotNull R> Either<L, R> right(@NotNull R right) {
        return new Right<>(right);
    }
}

class Left<@NotNull L, @NotNull R> implements Either<L, R> {

    @NotNull
    private final L value;

    Left(@NotNull L value) {
        this.value = value;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @NotNull
    @Override
    public L error() {
        return value;
    }

    @Override
    public <N> @NotNull Either<L, N> map(@NotNull Function<? super R, ? extends N> mapping) {
        return new Left<>(value);
    }

    @Override
    public @NotNull R orElse(@NotNull Function<? super L, ? extends R> mapping) {
        return mapping.apply(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Left)) return false;
        Left<?, ?> left = (Left<?, ?>) o;
        return value.equals(left.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Left{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}

class Right<@NotNull L, @NotNull R> implements Either<L, R> {

    @NotNull
    private final R value;

    Right(@NotNull R value) {
        this.value = value;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @NotNull
    @Override
    public R get() {
        return value;
    }

    @Override
    public <N> @NotNull Either<L, N> map(@NotNull Function<? super R, ? extends N> mapping) {
        return new Right<>(mapping.apply(this.value));
    }

    @Override
    public @NotNull R orElse(@NotNull Function<? super L, ? extends R> mapping) {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Right)) return false;
        Right<?, ?> right = (Right<?, ?>) o;
        return value.equals(right.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Right{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
