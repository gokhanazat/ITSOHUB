package com.mgacreative.globaltrade.core.auth

/**
 * Represent a system user in the authentication and authorization context.
 */
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val registryNumber: String? = null,
    val role: Role = Role.GUEST,
    val isApproved: Boolean = false,
    val firmId: String? = null,
    val title: String? = null
) {
    val status: String get() = if (isApproved) "Active" else "Pending"
}
