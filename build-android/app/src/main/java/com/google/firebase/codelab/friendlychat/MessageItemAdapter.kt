/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


// The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
// See: https://github.com/firebase/FirebaseUI-Android
class MessageItemAdapter(options: FirebaseRecyclerOptions<FriendlyMessage>) :
    FirebaseRecyclerAdapter<FriendlyMessage, MessageItemAdapter.MessageItemViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessageItemViewHolder,
        position: Int,
        model: FriendlyMessage
    ) {
        holder.bind(model)
    }

    inner class MessageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        var messageImageView: ImageView = itemView.findViewById(R.id.messageImageView)
        var messengerTextView: TextView = itemView.findViewById(R.id.messengerTextView)
        var messengerImageView: CircleImageView = itemView.findViewById(R.id.messengerImageView)

        fun bind(item: FriendlyMessage) {
            if (item.text != null) {
                messageTextView.text = item.text
                messageTextView.visibility = TextView.VISIBLE
                messageImageView.visibility = ImageView.GONE
            } else if (item.imageUrl != null) {
                val imageUrl = item.imageUrl
                if (imageUrl!!.startsWith("gs://")) {
                    val storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl)
                    storageReference.downloadUrl
                        .addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            Glide.with(messageImageView.context)
                                .load(downloadUrl)
                                .into(messageImageView)
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                TAG,
                                "Getting download url was not successful.",
                                e
                            )
                        }
                } else {
                    Glide.with(messageImageView.context)
                        .load(item.imageUrl)
                        .into(messageImageView)
                }
                messageImageView.visibility = ImageView.VISIBLE
                messageTextView.visibility = TextView.GONE
            }
        }
    }

    companion object {
        const val TAG = "MessageItemAdapter"
    }
}
