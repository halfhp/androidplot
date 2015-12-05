/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.util;

/**
 * Essentially just a version of the Map interface used to associate
 * a key of a given type with a value of a given type, does impose a 1:1
 * relationship between keys and values and defines no method for insertion or deletion.
 */
public interface Mapping<Key, Value> {

    /**
     * @param value
     * @return The Key associated with the specified value.
     */
    Key get(Value value);
}
