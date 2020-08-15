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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.jetbrains.annotations.NotNull;

public class StdErrReplacer extends OutputReplacer<ByteArrayOutputStream, StdErr> {

    @Override
    @NotNull Class<? extends OutputReplacer<ByteArrayOutputStream, StdErr>> getNamespaceKey() {
        return StdErrReplacer.class;
    }

    @Override
    @NotNull Class<StdErr> getTargetType() {
        return StdErr.class;
    }

    @NotNull
    @Override
    ByteArrayOutputStream createNew() {
        return new ByteArrayOutputStream();
    }

    @Override
    void setStdIo(@NotNull PrintStream replacing) {
        System.setErr(replacing);
    }

    @Override
    @NotNull PrintStream getOriginal() {
        return System.err;
    }

    @NotNull
    @Override
    StdErr toParameterType(@NotNull ByteArrayOutputStream stream) {
        return () -> stream;
    }
}
