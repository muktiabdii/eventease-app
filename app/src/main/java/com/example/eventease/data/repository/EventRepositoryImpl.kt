package com.example.eventease.data.repository

import android.content.Context
import android.net.Uri
import com.example.eventease.data.domain.model.Event
import com.example.eventease.data.remote.cloudinary.CloudinaryService
import com.example.eventease.data.remote.firebase.FirebaseProvider
import com.example.eventease.data.remote.firebase.FirebaseProvider.auth
import com.example.eventease.domain.repository.EventRepository
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl(context: Context) : EventRepository {

    private val firestore = FirebaseProvider.firestore
    private val auth = FirebaseProvider.auth
    private val eventsCollection = firestore.collection("events")
    private val cloudinaryService = CloudinaryService(context)

    override fun getAllEvents(): Flow<Result<List<Event>>> = callbackFlow {
        val snapshotListener = eventsCollection.addSnapshotListener { snapshot, error ->

            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val events = snapshot.toObjects(Event::class.java)
                trySend(Result.success(events))
            }
        }

        awaitClose { snapshotListener.remove() }
    }
    override suspend fun createEvent(event: Event, imageUri: Uri): Result<Unit> {
        return try {
            val creatorId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val imageUrl = cloudinaryService.uploadImage(imageUri)

            val finalEvent = event.copy(
                creatorId = creatorId,
                imageUrl = imageUrl
            )

            eventsCollection.add(finalEvent).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun getEventDetails(eventId: String): Flow<Result<Event>> = callbackFlow {
        val docRef = eventsCollection.document(eventId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            val event = snapshot?.toObject(Event::class.java)
            if (event != null) {
                trySend(Result.success(event))
            } else {
                trySend(Result.failure(Exception("Event not found")))
            }
        }
        awaitClose { listener.remove() }
    }

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun attendEvent(eventId: String): Result<Unit> {
        val userId = getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            eventsCollection.document(eventId).update(
                "participants", FieldValue.arrayUnion(userId)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAttendance(eventId: String): Result<Unit> {
        val userId = getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            eventsCollection.document(eventId).update(
                "participants", FieldValue.arrayRemove(userId)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun getCreatedEvents(): Flow<Result<List<Event>>> {
        val userId = getCurrentUserId()
        if (userId == null) {
            return flowOf(Result.failure(Exception("User not logged in")))
        }

        return callbackFlow {
            val snapshotListener = eventsCollection
                .whereEqualTo("creatorId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val events = snapshot.toObjects(Event::class.java)
                        trySend(Result.success(events))
                    }
                }
            awaitClose { snapshotListener.remove() }
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        val userId = getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))

        return try {
            val eventDoc = eventsCollection.document(eventId).get().await()
            val creatorId = eventDoc.getString("creatorId")

            if (creatorId != userId) {
                return Result.failure(Exception("You don't have permission to delete this event"))
            }

            eventsCollection.document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getJoinedEvents(): Flow<Result<List<Event>>> {
        val userId = getCurrentUserId()
        if (userId == null) {
            return flowOf(Result.failure(Exception("User not logged in")))
        }

        return callbackFlow {
            val snapshotListener = eventsCollection
                .whereArrayContains("participants", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val events = snapshot.toObjects(Event::class.java)
                        trySend(Result.success(events))
                    }
                }
            awaitClose { snapshotListener.remove() }
        }
    }
}