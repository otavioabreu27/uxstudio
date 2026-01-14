package com.uxstudio.contacts.infrastructure.persistence

/**
 * Base contract for persistence entities.
 * * This interface ensures that all infrastructure models can be converted
 * back to the pure [D] domain model, maintaining a strict separation of concerns.
 * * @param D The target Domain Model type.
 */
fun interface PersistenceEntity<D> {
    /**
     * Maps the persistence entity (Infrastructure) to a pure Domain model.
     * @return An instance of the domain model [D].
     */
    fun toDomain(): D
}