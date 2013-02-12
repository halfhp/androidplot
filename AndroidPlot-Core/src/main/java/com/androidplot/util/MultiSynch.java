/*
 * Copyright 2013 AndroidPlot.com
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
import java.util.List;
import java.util.Set;

/**
 * Utility class for obtaining synch lock across multiple objects.
 */
public abstract class MultiSynch {

    /**
     * Callback class for doing work from within a MultiSynch.
     */
    public interface Action {

        /**
         * Invoked by MultiSynch.run(...)
         * @param params
         */
        public void run(Object[] params);
    }


    /**
     *
     * @param params
     * @param synchSet Set of objects to be synchronized upon
     * @param action Action to be invoked once  full synchronization has been obtained.
     */
    public static void run(Object[] params, Set synchSet,  Action action) {
        run(params, synchSet.toArray(), action, 0);
    }

    /**
     * @param params
     * @param synchList List of objects to be synchronized upon
     * @param action   Action to be invoked once  full synchronization has been obtained.
     */
    public static void run(Object[] params, List synchList, Action action) {
        run(params, synchList.toArray(), action, 0);
    }

    /**
     * @param params
     * @param synchArr Array of objects to be synchronized upon
     * @param action   Action to be invoked once  full synchronization has been obtained.
     */
    public static void run(Object[] params, Object[] synchArr, Action action) {
        run(params, synchArr, action, 0);
    }

    /**
     * Recursively synchs on each item in SynchList
     * @param params
     * @param synchArr
     * @param action
     * @param depth
     */
    private static void run(Object[] params, Object[] synchArr, Action action, int depth) {
        if (synchArr != null) {
            synchronized (synchArr[depth]) {
                if (depth < synchArr.length - 1) {
                    run(params, synchArr, action, ++depth);
                } else {
                    action.run(params);
                }
            }
        }
        action.run(params);
    }
}
