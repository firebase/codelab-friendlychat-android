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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.codelab.friendlychat.MainActivity.Companion.ANONYMOUS
import com.google.firebase.codelab.friendlychat.databinding.ItemMessageBinding
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
// See: https://github.com/firebase/FirebaseUI-Android
class MessageItemAdapter(
    options: FirebaseRecyclerOptions<FriendlyMessage>,
    currentUserName: String?
) :
    FirebaseRecyclerAdapter<FriendlyMessage, MessageItemAdapter.MessageItemViewHolder>(options) {

    private val currentUserName = currentUserName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_message,
            parent,
            false
        )
        val binding = ItemMessageBinding.bind(view)
        return MessageItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MessageItemViewHolder,
        position: Int,
        model: FriendlyMessage
    ) {
        holder.bind(model)
    }

    inner class MessageItemViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendlyMessage) {
            val textView = binding.messageTextView
            val imageView = binding.messageImageView

            showMessengerViews(item)

            // Display either the FriendlyMessage text or image
            if (item.text != null) {
                imageView.visibility = View.GONE
                textView.visibility = View.VISIBLE
                textView.text = item.text
                setTextColor(item.name, textView)
                alignNameWithView(textView)
            } else if (item.imageUrl != null) {
                textView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                loadImageIntoView(imageView, item.imageUrl)
                alignNameWithView(imageView)
            }
        }

        // Display user's name and profile image if available
        private fun showMessengerViews(item: FriendlyMessage) {
            binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }

        // Since the FriendlyMessage is either an image or text, we need to set the user's name
        // to align under whichever view (imageView or textView) is showing
        private fun alignNameWithView(view: View) {
            val params =
                binding.messengerTextView.layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = view.id
            params.topToBottom = view.id
            binding.messengerTextView.requestLayout()
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
    }

    companion object {
        const val TAG = "MessageItemAdapter"
    }
}
