package com.dream.myfirestorecharlibrary

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.dream.myfirestorecharlibrary.models.Message
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.io.File

class FireStoreUtil {
    private val fireStore = Firebase.firestore
    val getMessageResponse = MutableLiveData<Resource<ArrayList<Message>>>()
    val getImageURLResponse = MutableLiveData<Resource<String>>()

    fun sendImageToFirebase(filePath: String) {
        getImageURLResponse.postValue(Resource.Loading())
        val ref = FirebaseStorage.getInstance().reference
        val imageRoomID = System.currentTimeMillis().toString()
        ref.child(imageRoomID).putFile(Uri.fromFile(File(filePath)))
            .addOnSuccessListener {
                ref.child(imageRoomID).downloadUrl
                    .addOnSuccessListener {
                        it?.let {
                            getImageURLResponse.postValue(Resource.Success(it.toString()))
                        }
                    }
                    .addOnFailureListener {
                        getImageURLResponse.postValue(Resource.Error(it.message.toString()))
                    }
            }
            .addOnFailureListener {
                getImageURLResponse.postValue(Resource.Error(it.message.toString()))
            }
    }

    fun retrieveMsg(channelName: String) {
        val messages = arrayListOf<Message>()
        fireStore.collection(channelName)
            .addSnapshotListener { value, _ ->
                value?.documents?.size?.let {
                    if (it > 0) {
                        messages.clear()
                        messages.addAll(
                            value.documents.map { map ->
                                Gson().
                                fromJson(
                                    Gson().
                                    toJson(map.data),
                                    Message::class.java
                                )
                            }
                        )
                    }
                    getMessageResponse.postValue(Resource.Success(messages))
                }
            }
    }

    fun sendMsgToFirebase(message: Message, channelName: String) {
        fireStore.collection(channelName)
            .get()
            .addOnSuccessListener { _ ->
                sendMessage(message, channelName)
            }

    }

    private fun sendMessage(message: Message, channelName: String) {
        fireStore.collection(
            channelName
        )
            .document(System.currentTimeMillis().toString())
            .set(message)
    }

    fun deleteChat(channelName: String) {
        fireStore.collection(channelName).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    document.reference.delete()
                }
            }
        }
    }

    fun getChannelName(user1ID: String, user2ID: String): String {
        if (user1ID < user2ID) {
            return "$user1ID-$user2ID"
        }
        return "$user2ID-$user1ID"
    }

    fun deleteImageFromStorage(path: String?) {
        path?.let {
            val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(path)
            photoRef.delete().addOnSuccessListener {
                Log.e("Firebase", "deleteImageFromStorage: ho gai")
            }.addOnFailureListener {
                Log.e("Firebase", "deleteImageFromStorage: nai hoi")
            }
        }
    }
}