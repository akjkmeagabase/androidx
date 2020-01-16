/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.startup

import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.annotation.RestrictTo

/**
 * The [ContentProvider] which discovers [ComponentInitializer]s in an Application and initializes
 * them before [android.app.Application.onCreate].
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class InitializationProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalStateException("Not allowed.")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw IllegalStateException("Not allowed.")
    }

    override fun onCreate(): Boolean {
        val context = requireNotNull(context)
        val metadata = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        ).metaData

        val startup = context.getString(R.string.androidx_startup)
        if (metadata.size() > 0) {
            val components = mutableListOf<Class<*>>()
            metadata.keySet().forEach { key ->
                val value = metadata.getString(key, null)
                if (startup == value) {
                    try {
                        val clazz = Class.forName(key)
                        StartupLogger.i { "Discovered ($clazz)" }
                        components.add(clazz)
                    } catch (throwable: Throwable) {
                        val message = "Cannot find ComponentInitializer ($key)"
                        StartupLogger.e(message, throwable)
                        throw StartupException(message, throwable)
                    }
                }
            }
            AppInitializer.initialize(context, components)
        }
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw IllegalStateException("Not allowed.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalStateException("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        throw IllegalStateException("Not allowed.")
    }

    companion object {
        private const val TAG = "InitializationProvider"
    }
}
