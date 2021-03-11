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

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
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
class MessageItemAdapter(
    options: FirebaseRecyclerOptions<FriendlyMessage>,
    currentUserName: String
) :
    FirebaseRecyclerAdapter<FriendlyMessage, MessageItemAdapter.MessageItemViewHolder>(options) {

    private val currentUserName = currentUserName

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
        var senderIsUser =
            model.name != ANONYMOUS && currentUserName == model.name && model.name != null

        holder.bind(model, senderIsUser)
    }

    inner class MessageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views for messages from another sender
        private var messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private var messageImageView: ImageView = itemView.findViewById(R.id.messageImageView)
        private var messengerTextView: TextView = itemView.findViewById(R.id.messengerTextView)
        private var messengerImageView: CircleImageView =
            itemView.findViewById(R.id.messengerImageView)

        // Views for messages owned by current user
        private var messageSelfTextView: TextView = itemView.findViewById(R.id.messageSelfTextView)
        private var messageSelfImageView: ImageView =
            itemView.findViewById(R.id.messageSelfImageView)
        private var messengerSelfTextView: TextView =
            itemView.findViewById(R.id.messengerSelfTextView)
        private var messengerSelfImageView: CircleImageView =
            itemView.findViewById(R.id.messengerSelfImageView)

        fun bind(item: FriendlyMessage, senderIsUser: Boolean) {
            val textView = if (senderIsUser) messageSelfTextView else messageTextView
            val imageView = if (senderIsUser) messageSelfImageView else messageImageView

            showMessageView(item, senderIsUser)
            showMessengerViews(item, senderIsUser)

            if (item.text != null) {
                textView.text = item.text
                setMessengerNameConstraintParams(textView, senderIsUser)
            } else if (item.imageUrl != null) {
                loadImageIntoView(imageView, item.imageUrl)
                setMessengerNameConstraintParams(imageView, senderIsUser)
            }
        }

        private fun showMessageView(item: FriendlyMessage, senderIsUser: Boolean) {
            messengerSelfImageView.visibility = if (senderIsUser) View.VISIBLE else View.GONE
            messengerSelfTextView.visibility = if (senderIsUser) View.VISIBLE else View.GONE
            messengerTextView.visibility = if (senderIsUser) View.GONE else View.VISIBLE
            messengerImageView.visibility = if (senderIsUser) View.GONE else View.VISIBLE

            if (item.text != null) {
                messageImageView.visibility = View.GONE
                messageSelfImageView.visibility = View.GONE
                messageTextView.visibility = if (senderIsUser) View.GONE else View.VISIBLE
                messageSelfTextView.visibility = if (senderIsUser) View.VISIBLE else View.GONE
            } else if (item.imageUrl != null) {
                messageTextView.visibility = View.GONE
                messageSelfTextView.visibility = View.GONE
                messageImageView.visibility = if (senderIsUser) View.GONE else View.VISIBLE
                messageSelfImageView.visibility = if (senderIsUser) View.VISIBLE else View.GONE
            }
        }

        private fun showMessengerViews(item: FriendlyMessage, senderIsUser: Boolean) {
            val textView = if (senderIsUser) messengerSelfTextView else messengerTextView
            val imageView = if (senderIsUser) messengerSelfImageView else messengerImageView

            textView.text = if (item.name == null) Companion.ANONYMOUS else item.name

            if (item.photoUrl != null) {
                loadImageIntoView(imageView, item.photoUrl)
            } else {
                imageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
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

        // Since message is either an image or text, we need to set the messenger name view
        // to appear under whichever view (imageView or textView) is showing
        private fun setMessengerNameConstraintParams(view: View, senderIsUser: Boolean) {
            if (senderIsUser) {
                val params = messengerSelfTextView.layoutParams as ConstraintLayout.LayoutParams
                params.endToEnd = view.id
                params.topToBottom = view.id
                messengerSelfTextView.requestLayout()
            } else {
                val params = messengerTextView.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = view.id
                params.topToBottom = view.id
                messengerTextView.requestLayout()
            }
        }
    }

    companion object {
        const val TAG = "MessageItemAdapter"
        const val ANONYMOUS = "anonymous"
    }
}
