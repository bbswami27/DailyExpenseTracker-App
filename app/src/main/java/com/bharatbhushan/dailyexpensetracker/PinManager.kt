package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinManager {

    private const val PREFERENCES_NAME =
        "ghar_budget_pin_settings"

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_SIZE = 16

    fun hasPin(
        context: Context,
        userId: String
    ): Boolean {

        val preferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        return preferences.contains(
            hashKey(userId)
        ) && preferences.contains(
            saltKey(userId)
        )
    }

    fun isAppLockEnabled(
        context: Context,
        userId: String
    ): Boolean {

        val preferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        val key = enabledKey(userId)

        return if (preferences.contains(key)) {
            preferences.getBoolean(key, true)
        } else {
            true
        }
    }

    fun savePin(
        context: Context,
        userId: String,
        pin: String
    ): Boolean {

        if (!pin.matches(Regex("\\d{4,6}"))) {
            return false
        }

        val salt = ByteArray(SALT_SIZE)
        SecureRandom().nextBytes(salt)

        val pinHash = createHash(
            pin = pin,
            salt = salt
        )

        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                saltKey(userId),
                Base64.encodeToString(
                    salt,
                    Base64.NO_WRAP
                )
            )
            .putString(
                hashKey(userId),
                Base64.encodeToString(
                    pinHash,
                    Base64.NO_WRAP
                )
            )
            .putBoolean(
                enabledKey(userId),
                true
            )
            .apply()

        return true
    }

    fun verifyPin(
        context: Context,
        userId: String,
        enteredPin: String
    ): Boolean {

        val preferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        val savedSalt = preferences.getString(
            saltKey(userId),
            null
        ) ?: return false

        val savedHash = preferences.getString(
            hashKey(userId),
            null
        ) ?: return false

        return try {

            val salt = Base64.decode(
                savedSalt,
                Base64.NO_WRAP
            )

            val expectedHash = Base64.decode(
                savedHash,
                Base64.NO_WRAP
            )

            val enteredHash = createHash(
                pin = enteredPin,
                salt = salt
            )

            MessageDigest.isEqual(
                expectedHash,
                enteredHash
            )

        } catch (_: Exception) {
            false
        }
    }

    fun disableAppLock(
        context: Context,
        userId: String
    ) {

        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .remove(saltKey(userId))
            .remove(hashKey(userId))
            .putBoolean(
                enabledKey(userId),
                false
            )
            .apply()
    }

    fun clearPin(
        context: Context,
        userId: String
    ) {

        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .remove(saltKey(userId))
            .remove(hashKey(userId))
            .remove(enabledKey(userId))
            .apply()
    }

    private fun createHash(
        pin: String,
        salt: ByteArray
    ): ByteArray {

        val specification = PBEKeySpec(
            pin.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {

            SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(specification)
                .encoded

        } finally {
            specification.clearPassword()
        }
    }

    private fun saltKey(
        userId: String
    ): String {
        return "pin_salt_$userId"
    }

    private fun hashKey(
        userId: String
    ): String {
        return "pin_hash_$userId"
    }

    private fun enabledKey(
        userId: String
    ): String {
        return "app_lock_enabled_$userId"
    }
}