---
id: firebase-android
summary: In this codelab, you'll learn how to build an Android app with Firebase platform.
status: [published]
categories: Firebase,Android
tags: firebase-dev-summit-2016,firebase17,gdd17,io2016,io2017,io2018,io2019,kiosk,tag-firebase,web
feedback link: https://github.com/firebase/codelab-friendlychat-android/issues

---

# Firebase Android Codelab - Build Friendly Chat

[Codelab Feedback](https://github.com/firebase/codelab-friendlychat-android/issues)


## Overview
Duration: 05:00


<img src="img/screenshot.png" alt="screenshot"  width="173.00" />

Image: Working Friendly Chat app.

Welcome to the Friendly Chat codelab. In this codelab, you'll learn how to use the Firebase platform to create a chat app on Android.

#### What you'll learn

* Allow users to sign in.
* Sync data using the Firebase Realtime Database.
* Store binary files in Firebase 

#### What you'll need

*  [Android Studio](https://developer.android.com/studio) version 4.0+.
* An Android device or [Emulator](https://developer.android.com/studio/run/emulator#install) with Android 4.4+.


## Get the sample code
Duration: 05:00

### Clone the repository

Clone the GitHub repository from the command line:

```console
$ git clone https://github.com/firebase/codelab-friendlychat-android
```

> aside positive
> 
> The "friendlychat-android" repository contains two directories:
> 
> *  <img src="img/android_studio_folder.png" alt="android_studio_folder"  width="20.00" />**build-android-start**—Starting code that you build upon in this codelab.
> *  <img src="img/android_studio_folder.png" alt="android_studio_folder"  width="20.00" />**build-android**—Completed code for the finished sample app.
> 
> **Note**: If you want to run the finished app, you have to create a project in the Firebase console corresponding to the package name and SHA1. See  [Create Firebase Console Project](https://codelabs.developers.google.com/codelabs/firebase-android/#2) for the command. Also you will have to enable Google as an Auth Provider; do this in the Authentication section of the Firebase console. 


### Import into Android Studio

In Android Studio click **File** > **Open** and  select the `build-android-start` directory ( <img src="img/android_studio_folder.png" alt="android_studio_folder"  width="20.00" />) from the directory where you downloaded the sample code.

You should now have the `build-android-start project` open in Android Studio. If you see a warning about a `google-services.json` file missing, don't worry. It will be added in the next step.


## Create Firebase project
Duration: 03:00

In this step you will create a Firebase project to use during this codelab.

1. In your browser go to the  [Firebase console](https://console.firebase.google.com).
2. Select **Add project**.
3. Select or enter a project name, you can use any name you want.
4. You will not need Google Analytics for this project, so you can disable it when asked.
5. Click **Create Project** and when your project is ready click **Continue**


## Add Firebase to your app
Duration: 03:00

1. From the overview screen of your new project, click the Android icon to launch the setup workflow:

<img src="img/add-android-app.png"

2. On the next screen, enter `com.google.firebase.codelab.friendlychat` as the package name for your app.
3. Enter the SHA1 of your signing keystore. Run the command below in the project directory to determine the SHA1 of your debug key:

```console
./gradlew signingReport
```

You should see some output like this, the important line is the `SHA1` key:

```console
Store: /Users/username/.android/debug.keystore
Alias: AndroidDebugKey
MD5: A5:88:41:04:8F:06:59:6A:AE:33:76:87:AA:AD:19:23
SHA1: A7:89:F5:06:A8:07:A1:22:EC:90:6A:A6:EA:C3:D4:8B:3A:30:AB:18
SHA-256: 05:A2:2A:35:EE:F2:51:23:72:4D:72:67:A5:6A:8A:58:22:2C:00:A6:AB:F6:45:D5:A1:82:D8:90:A4:69:C8:FE
Valid until: Wednesday, August 10, 2044
```

> aside positive
> 
> If you're unable to fins your SHA1 hash see [this page](https://developers.google.com/android/guides/client-auth) for more information.

4. Click **Register App** and then click **Download google-services.json** to download the `google-services` configuration file.
5. Copy the `google-services.json` file into the *`app`* directory in your project. After the file is downloaded you can **Skip** the next steps shown in the console (they've already been done for you in the build-android-start project).

> aside positive
> 
> In the `build.gradle` and `app/build.gradle` files you'll notice that the `com.google.gms:google-services` plugin has already been added to your app.

6. To be sure that all dependencies are available to your app, you should sync your project with gradle files at this point. Select **File** > **Sync Project with Gradle Files** from the Android Studio toolbar.

## Run the starter app
Duration: 03:00

Now that you have imported the project into Android Studio and configured the `google-services` plugin with your JSON file, you are ready to run the app for the first time. 

1. Start your Android device or emulator.
2. In Android Studio click **Run** ( <img src="img/execute.png" alt="execute"  width="20.00" />) in the toolbar.

The app should launch on your device. At this point, you should see an empty message list, and sending and receiving messages will not work. In the next section, you authenticate users so they can use Friendly Chat.

## Enable Authentication
Duration: 05:00

Let's require a user to sign in before reading or posting any Friendly Chat messages.

#### Realtime Database Security Rules

Access to your Firebase Database is configured by a set of rules written in a JSON configuration language.

Go to your project in the Firebase console and select **Database**. Select the **Realtime Database** option (not Cloud Firestore). If prompted for security rules, with choices to start in either **test mode** or **locked mode**, choose **locked mode**. Once the default rules are established, select the **Rules** tab and update the rules configuration with the following:

```
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

Click "Publish" to publish the new rules.

For more information on how this works (including documentation on the "auth" variable) see the Firebase  [security documentation](https://firebase.google.com/docs/database/security/quickstart).

#### **Configure Authentication APIs**

Before your application can access the Firebase Authentication APIs on behalf of your users, you will have to enable it 

1. Navigate to the  [Firebase console](http://console.firebase.google.com) and select your project
2. Select **Authentication**
3. Select the **Sign In Method** tab
4. Toggle the **Google** switch to enabled (blue)
5. Set a support email.
6. Press **Save** on the resulting dialog

If you get errors later in this codelab with the message "CONFIGURATION_NOT_FOUND", come back to this step and double check your work.

#### **Add Firebase Auth dependency**

The firebase-auth SDK allows easy management of authenticated users of your application. Confirm the existence of this dependency in your `app/build.gradle` file.

#### app/build.gradle

```
implementation 'com.google.firebase:firebase-auth'
```

Add the Auth instance variables in the `MainActivity` class under the `// Firebase instance variables` comment:

#### MainActivity.java (instance variable)

```
// Firebase instance variables
private FirebaseAuth mFirebaseAuth;
private FirebaseUser mFirebaseUser;
```

#### **Check for current user**

Now let's modify `MainActivity.java` to send the user to the sign-in screen whenever they open the app and are unauthenticated. 

Add the following to the `onCreate` method **after** `mUsername` has been initialized:

**MainActivity.java**

```
// Initialize Firebase Auth
mFirebaseAuth = FirebaseAuth.getInstance();
mFirebaseUser = mFirebaseAuth.getCurrentUser();
if (mFirebaseUser == null) {
    // Not signed in, launch the Sign In activity
    startActivity(new Intent(this, SignInActivity.class));
    finish();
    return;
} else {
    mUsername = mFirebaseUser.getDisplayName();
    if (mFirebaseUser.getPhotoUrl() != null) {
        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
    }
}
```

Then add a new case to `onOptionsItemSelected()` to handle the sign out button:

**MainActivity.java**

```
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                mSignInClient.signOut();

                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
```

Now we have all of the logic in place to send the user to the sign-in screen when necessary. Next we need to implement the sign-in screen to properly authenticate users.

#### **Implement the Sign-In screen**

Open the file `SignInActivity.java`.  Here a simple Sign-In button is used to initiate authentication. In this step you will implement the logic to Sign-In with Google, and then use that Google account to authenticate with Firebase.

Add an Auth instance variable in the `SignInActivity` class under the `// Firebase instance variables` comment:

**SignInActivity.java**

```
// Firebase instance variables
private FirebaseAuth mFirebaseAuth;
```

Then, edit the `onCreate()` method to initialize Firebase in the same way you did in `MainActivity`:

**SignInActivity.java**

```
// Initialize FirebaseAuth
mFirebaseAuth = FirebaseAuth.getInstance();
```

Next, initiate signing in with Google. Update `SignInActivity`'s `onClick` method to look like this:

**SignInActivity.java**

```
@Override
public void onClick(View v) {
   switch (v.getId()) {
       case R.id.sign_in_button:
           signIn();
           break;
   }
}
```

Add the required signIn method that actually presents the user with the Google Sign-In UI.

**SignInActivity.java**

```
private void signIn() {
    Intent signInIntent = mSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
}
```

Next, add the `onActivityResult` method to `SignInActivity` to handle the sign in result. If the result of the Google Sign-In was successful, use the account to authenticate with Firebase.

**SignInActivity.java**

```
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent in signIn()
    if (requestCode == RC_SIGN_IN) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
        }
    }
}
```

Add the required `firebaseAuthWithGoogle` method to authenticate with the signed in Google account:

**SignInActivity.java**

```
private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
}
```

That's it! You've implemented authentication using Google as an Identity Provider in just a few method calls and without needing to manage any server-side configuration.

#### **Test your work**

Run the app on your device. You should be immediately sent to the sign-in screen. Tap the Google Sign-In button. You should then be sent to the messaging screen if everything worked well.


## Read Messages
Duration: 05:00


#### **Import Messages**

1. In your project in Firebase console, select **Database** on the left navigation menu.
2. Select Realtime Database option.

> aside positive
> 
> **Note**: If you are presented with database creation option, please select Realtime database create option and apply the rules as done in previous step.

3. In the overflow menu of the Data tab, select **Import JSON**. 
4. Browse to the initial_messages.json file in the root of the cloned repository, and select it. 
5. Click **Import**.

> aside positive
> 
> **Note**: This replaces any data currently in your database.

After importing the JSON file, your database should look like this:

```
root
        messages
                -K2ib4H77rj0LYewF7dP
                        text: "hello"
                        name: "anonymous"
                -K2ib5JHRbbL0NrztUfO
                        text: "how are you"
                        name: "anonymous"
                -K2ib62mjHh34CAUbide
                        text: "i am fine"
                        name: "anonymous"
```

#### Add Firebase Realtime Database and Firebase Storage dependencies

In the dependencies block of the app/build.gradle file, the following dependencies should be included. For this codelab, they are already added for convenience; confirm this by looking in the app/build.gradle file:

#### Dependency in app/build.gradle

```
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-storage'
```

#### Synchronize messages

In this section we add code that synchronizes newly added messages to the app UI by:

* Initializing the Firebase Realtime Database and adding a listener to handle changes made to the data.
* Updating the `RecyclerView` adapter so new messages will be shown.
* Adding the Database instance variables with your other Firebase instance variables in the `MainActivity` class:

#### MainActivity.java

```
// Firebase instance variables
...
private DatabaseReference mFirebaseDatabaseReference;
private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
        mFirebaseAdapter;
```

Modify your MainActivity's `onCreate` method by replacing *`mProgressBar.setVisibility(ProgressBar.INVISIBLE);`* with the code defined below. This code initially adds all existing messages and then listens for new child entries under the messages path in your Firebase Realtime Database. It adds a new element to the UI for each message:

#### MainActivity.java

```
// New child entries
mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();
mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }
            
            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
       mProgressBar.setVisibility(ProgressBar.INVISIBLE);
if (friendlyMessage.getText() != null) {
   viewHolder.messageTextView.setText(friendlyMessage.getText());
   viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
   viewHolder.messageImageView.setVisibility(ImageView.GONE);
} else if (friendlyMessage.getImageUrl() != null) {
   String imageUrl = friendlyMessage.getImageUrl();
   if (imageUrl.startsWith("gs://")) {
       StorageReference storageReference = FirebaseStorage.getInstance()
               .getReferenceFromUrl(imageUrl);
       storageReference.getDownloadUrl().addOnCompleteListener(
               new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()) {
                   String downloadUrl = task.getResult().toString();
                   Glide.with(viewHolder.messageImageView.getContext())
                           .load(downloadUrl)
                           .into(viewHolder.messageImageView);
               } else {
                   Log.w(TAG, "Getting download url was not successful.",
                           task.getException());
               }
           }
       });
   } else {
       Glide.with(viewHolder.messageImageView.getContext())
               .load(friendlyMessage.getImageUrl())
               .into(viewHolder.messageImageView);
   }
   viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
   viewHolder.messageTextView.setVisibility(TextView.GONE);
}


viewHolder.messengerTextView.setText(friendlyMessage.getName());
if (friendlyMessage.getPhotoUrl() == null) {
   viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
           R.drawable.ic_account_circle_black_36dp));
} else {
   Glide.with(MainActivity.this)
           .load(friendlyMessage.getPhotoUrl())
           .into(viewHolder.messengerImageView);
}

   }
};

mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
   @Override
   public void onItemRangeInserted(int positionStart, int itemCount) {
       super.onItemRangeInserted(positionStart, itemCount);
       int friendlyMessageCount = mFirebaseAdapter.getItemCount();
       int lastVisiblePosition =
              mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
       // If the recycler view is initially being loaded or the 
       // user is at the bottom of the list, scroll to the bottom 
       // of the list to show the newly added message.
       if (lastVisiblePosition == -1 ||
               (positionStart >= (friendlyMessageCount - 1) &&
                       lastVisiblePosition == (positionStart - 1))) {
           mMessageRecyclerView.scrollToPosition(positionStart);
       }
   }
});

mMessageRecyclerView.setAdapter(mFirebaseAdapter);
```

Appropriately start and stop listening for updates from Firebase Realtime Database. Update the *`onPause`* and *`onResume`* methods in `MainActivity` as shown below.

#### MainActivity.java

```
@Override
public void onPause() {
    mFirebaseAdapter.stopListening();
    super.onPause();
}

@Override
public void onResume() {
    super.onResume();
    mFirebaseAdapter.startListening();
}
```

#### Test message sync

1. Click **Run** ( <img src="img/execute.png" alt="execute"  width="20.00" />).
2. Add new messages directly in the Database section of the Firebase console. Confirm that they show up in the Friendly-Chat UI.

* Navigate to the Database section of the Firebase console. From the Data tab, select the '+' sign on the messages element.
* Give the new element a name of -ABCD (note the '-' sign) and leave the value empty for now.
* Select the '+' sign on the -ABCD element
* Give the new element a name of "name" and value of "Mary"
* Select the '+' sign on the -ABCD element again
* Give the new element a name of "text" and value of "hello"
* Select Add

Congratulations, you just added a realtime database to your app!


## Send Messages
Duration: 05:00


#### **Implement text message sending**

In this section, you will add the ability for app users to send text messages.  The code snippet below listens for click events on the send button, creates a new `FriendlyMessage` object with the contents of the message field, and pushes the message to the database.  The `push()` method adds an automatically generated ID to the pushed object's path.  These IDs are sequential which ensures that the new messages will be added to the end of the list.  

Update the `onClick` method of `mSendButton` in the `onCreate` method in the `MainActivity` class.  This code is at the bottom of the `onCreate` method already. Update the `onClick` body to match the code below:

#### MainActivity.java

```
mSendButton = (Button) findViewById(R.id.sendButton);
mSendButton.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
       FriendlyMessage friendlyMessage = new 
               FriendlyMessage(mMessageEditText.getText().toString(),
                               mUsername,
                               mPhotoUrl,
                               null /* no image */);
       mFirebaseDatabaseReference.child(MESSAGES_CHILD)
               .push().setValue(friendlyMessage);
       mMessageEditText.setText("");
   }
});
```

#### **Implement image message sending**

In this section, you will add the ability for app users to send image messages. Creating an image message is done with these steps:

* Select image
* Handle image selection
* Write temporary image message to the RTDB (Realtime Database)
* Begin to upload selected image
* Update image message URL to that of the uploaded image, once upload is complete

#### **Select Image**

To add images this codelab uses Cloud Storage for Firebase. Cloud Storage is a good place to store the binary data of your app.

In the Firebase console select **Storage** in the left navigation panel. Then click **Get Started** to enable Cloud Storage for your project.  Continue following the steps in the prompt, using the suggested defaults.

With the following code snippet you will allow the user to select an image from the device's local storage. Update the `onClick` method of `mAddMessageImageView` in the `onCreate` method in the `MainActivity` class. This code is at the bottom of the `onCreate` method already. Update the `onClick` body to match the code below:

#### MainActivity.java

```
mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
       Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
       intent.addCategory(Intent.CATEGORY_OPENABLE);
       intent.setType("image/*");
       startActivityForResult(intent, REQUEST_IMAGE);
   }
});
```

##### **Handle image selection and write temp message**

Once the user has selected an image, a call to the `MainActivity`'s `onActivityResult` will be fired. This is where you handle the user's image selection. Using the code snippet below, add the `onActivityResult` method to `MainActivity`. In this function you will write a message with a temporary image url to the database indicating the image is being uploaded.

#### MainActivity.java

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   super.onActivityResult(requestCode, resultCode, data);
   Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

   if (requestCode == REQUEST_IMAGE) {
       if (resultCode == RESULT_OK) {
           if (data != null) {
               final Uri uri = data.getData();
               Log.d(TAG, "Uri: " + uri.toString());

               FriendlyMessage tempMessage = new FriendlyMessage(null, mUsername, mPhotoUrl,
                       LOADING_IMAGE_URL);
               mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                       .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(DatabaseError databaseError,
                                                  DatabaseReference databaseReference) {
                               if (databaseError == null) {
                                   String key = databaseReference.getKey();
                                   StorageReference storageReference =
                                           FirebaseStorage.getInstance()
                                           .getReference(mFirebaseUser.getUid())
                                           .child(key)
                                           .child(uri.getLastPathSegment());

                                   putImageInStorage(storageReference, uri, key);
                               } else {
                                   Log.w(TAG, "Unable to write message to database.",
                                           databaseError.toException());
                               }
                           }
                       });
           }
       }
   }
}
```

##### **Upload image and update message**

Add the method `putImageInStorage` to `MainActivity`. It is called in `onActivityResult` to initiate the upload of the selected image. Once the upload is complete you will update the message to use the appropriate image. 

#### MainActivity.java

```
private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(MainActivity.this, 
                                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        FriendlyMessage friendlyMessage =
                                                new FriendlyMessage(null, mUsername, mPhotoUrl,
                                                        task.getResult().toString());
                                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                                .setValue(friendlyMessage);
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
```

#### **Test Sending Messages**

1. Click the  <img src="img/execute.png" alt="execute"  width="20.00" />**Run** button.
2. Enter a message and hit the send button, the new message should be visible in the app UI and in the Firebase console.
3. Tap the "+" image to select an image from your device. The new message should be visible first with a placeholder image, and then with the selected image once the image upload is complete. The new message should also be visible in the Firebase console, as an object in the Database and as a blob in Storage.


## Congratulations!
Duration: 01:00


You have used Firebase to easily build a real-time chat application.

#### **What we've covered**

* Firebase Authentication
* Firebase Realtime Database
* Cloud Storage for Firebase

#### Next Steps

* Try the  [Grow Friendly Chat](https://codelabs.developers.google.com/codelabs/growfirebase-android/index.html?index=..%2F..index) codelab, to learn how to grow the app you just built.
* Use Firebase in your own Android app.

#### **Learn More**

*  [firebase.google.com](https://firebase.google.com)

