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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
        private var messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private var messageImageView: ImageView = itemView.findViewById(R.id.messageImageView)
        private var messengerTextView: TextView = itemView.findViewById(R.id.messengerTextView)
        private var messengerImageView: CircleImageView =
            itemView.findViewById(R.id.messengerImageView)

        fun bind(item: FriendlyMessage) {
            setMessengerViews(item)

            if (item.text != null) {
                messageTextView.text = item.text
                messageTextView.visibility = TextView.VISIBLE
                messageImageView.visibility = ImageView.GONE
                setMessengerNameConstraintParams(messageTextView)
            } else if (item.imageUrl != null) {
                loadImageIntoView(messageImageView, item.imageUrl)
                messageImageView.visibility = ImageView.VISIBLE
                messageTextView.visibility = TextView.GONE
                setMessengerNameConstraintParams(messageImageView)
            }
        }

        private fun setMessengerViews(item: FriendlyMessage) {
            messengerTextView.text = if (item.name == null) "Anonymous" else item.name

            if (item.photoUrl != null) {
                loadImageIntoView(messengerImageView, item.photoUrl)
            } else {
                messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
        }

        private fun loadImageIntoView(view: ImageView, url: String?) {
            if (url!!.startsWith("gs://")) {
                val storageReference = Firebase.storage.getReferenceFromUrl(url)
                storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Glide.with(view.context)
                            .load(downloadUrl)
                            .into(view)
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Getting download url was not successful.",
                            e
                        )
                    }
            } else {
                Glide.with(view.context)
                    .load(url)
                    .into(view)
            }
        }

        // Since message is either ImageView or TextView, setting Messenger Name to appear
        // under whichever view is showing
        private fun setMessengerNameConstraintParams(view: View) {
            val params = messengerTextView.layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = view.id
            params.topToBottom = view.id
            messengerTextView.requestLayout()
        }
    }

    companion object {
        const val TAG = "MessageItemAdapter"
    }
}
