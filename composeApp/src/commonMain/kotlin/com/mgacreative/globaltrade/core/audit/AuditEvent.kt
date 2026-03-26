package com.mgacreative.globaltrade.core.audit

import kotlinx.serialization.Serializable

/**
 * Defines all standard actions an entity can perform within the system.
 * 
 * Used for tracking user activities in AuditLog to reconstruct user lifecycles and behaviors.
 */
@Serializable
enum class ActionType {
    LOGIN,
    LOGOUT,
    CREATE,
    UPDATE,
    DELETE,
    APPROVAL,
    REJECTION,
    VIEW,
    DOWNLOAD,
    CERTIFICATE_GENERATED,
    CERTIFICATE_REVOKED,
    CANCELLED,
    APPOINTMENT_NOTIFICATION,
    B2B_MATCH_CALCULATED,
    B2B_MATCH_APPROVED,
    MARKETPLACE_PROVIDER_STATUS_CHANGED,
    MARKETPLACE_PRODUCTS_FETCHED
}

/**
 * Application-wide data model representing an atomic audit log entry.
 * Designed to be safely serializable for Firestore Database integration.
 * 
 * Core requirements implemented:
 * - No UI logic.
 * - String/enum payload ready for remote DBs.
 * - UUID base identification (Firebase auto-ID friendly if id is optionally generated remotely).
 */
@Serializable
data class AuditEvent(
    val id: String = "",
    val userId: String = "",
    val userRole: String = "",
    val actionType: ActionType = ActionType.VIEW,
    val targetModule: String = "",
    val targetId: String? = null,
    val description: String = "",
    val timestamp: Long = 0L,
    val deviceInfo: String? = null,
    val appVersion: String = ""
)
