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


<img src="img/screenshot.png" alt="screenshot"  width="300.00" />

Image: Working Friendly Chat app.

Welcome to the Friendly Chat codelab. In this codelab, you'll learn how to use the Firebase platform to create a chat app on Android.

#### What you'll learn

* How to use Firebase Authentication to allow users to sign in.
* How to sync data using the Firebase Realtime Database.
* How to store binary files in Firebase Storage.

#### What you'll need

*  [Android Studio](https://developer.android.com/studio) version 4.0+.
* An Android device or [Emulator](https://developer.android.com/studio/run/emulator#install) with Android 4.4+.
* Familiarity with the Kotlin programming language.

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
> **Note**: If you want to run the finished app, you have to create a project in the Firebase console corresponding to the package name and SHA1. See  [Step #3](https://codelabs.developers.google.com/codelabs/firebase-android/#2) for the command. Also you will have to enable Google as an Auth Provider; do this in the Authentication section of the Firebase console. 


### Import into Android Studio

In Android Studio click **File** > **Open** and  select the `build-android-start` directory ( <img src="img/android_studio_folder.png" alt="android_studio_folder"  width="20.00" />) from the directory where you downloaded the sample code.

You should now have the `build-android-start` project open in Android Studio. If you see a warning about a `google-services.json` file missing, don't worry. It will be added in the next step.


### Check Dependencies

In this codelab all of the dependencies you will need have already been added for you, but it's important to understand how to add the Firebase SDK to your app:

**build.gradle**

```groovy
buildscript {
    // ...

    dependencies {
        classpath 'com.android.tools.build:gradle:4.1.2'

        // The google-services plugin is required to parse the google-services.json file
        classpath 'com.google.gms:google-services:4.3.5'
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.5.1'
    }
}
```

**app/build.gradle**

```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'com.google.firebase.crashlytics'
}

android {
    // ...
}

dependencies {
    // ...

    // Google Sign In SDK
    implementation 'com.google.android.gms:play-services-auth:19.0.0'

    // Firebase SDK
    implementation platform('com.google.firebase:firebase-bom:26.6.0')
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    implementation 'com.google.firebase:firebase-auth-ktx'

    // Firebase UI Library
    implementation 'com.firebaseui:firebase-ui-database:7.1.1'
}

// Apply the 'google-services' plugin
apply plugin: 'com.google.gms.google-services'
```


## Create Firebase project
Duration: 06:00

In this step you will create a Firebase project to use during this codelab and add the project configuration to your app.

### Create a new project

1. In your browser go to the  [Firebase console](https://console.firebase.google.com).
2. Select **Add project**.
3. Select or enter a project name, you can use any name you want.
4. You will not need Google Analytics for this project, so you can disable it when asked.
5. Click **Create Project** and when your project is ready click **Continue**

### Add Firebase to your app

Before you begin this step, get the SHA1 hash of your app: Run the command below in the project directory to determine the SHA1 of your debug key:

```console
./gradlew signingReport

Store: /Users/<username>/.android/debug.keystore
Alias: AndroidDebugKey
MD5: A5:88:41:04:8F:06:59:6A:AE:33:76:87:AA:AD:19:23
SHA1: A7:89:F5:06:A8:07:A1:22:EC:90:6A:A6:EA:C3:D4:8B:3A:30:AB:18
SHA-256: 05:A2:2A:35:EE:F2:51:23:72:4D:72:67:A5:6A:8A:58:22:2C:00:A6:AB:F6:45:D5:A1:82:D8:90:A4:69:C8:FE
Valid until: Wednesday, August 10, 2044
```

You should see some output like the above, the important line is the `SHA1` key. If you're unable to find your SHA1 hash see [this page](https://developers.google.com/android/guides/client-auth) for more information.

Now in the Firebase console, follow these steps to add an Android app to your project:

  1. From the overview screen of your new project, click the Android icon to launch the setup workflow:
     <img src="img/add-android-app.png" alt="add android app" />
  1. On the next screen, enter `com.google.firebase.codelab.friendlychat` as the package name for your app.
  1. Click **Register App** and then click **Download google-services.json** to download the `google-services` configuration file.
  1. Copy the `google-services.json` file into the *`app`* directory in your project. After the file is downloaded you can **Skip** the next steps shown in the console (they've already been done for you in the build-android-start project).
  1. To be sure that all dependencies are available to your app, you should sync your project with gradle files at this point. Select **File** > **Sync Project with Gradle Files** from the Android Studio toolbar.


## Run the starter app
Duration: 03:00

Now that you have imported the project into Android Studio and configured the `google-services` plugin with your JSON file, you are ready to run the app for the first time. 

1. Start your Android device or emulator.
2. In Android Studio click **Run** ( <img src="img/execute.png" alt="execute"  width="20.00" />) in the toolbar.

The app should launch on your device. At this point, you should see an empty message list, and sending and receiving messages will not work. In the next section, you authenticate users so they can use Friendly Chat.

## Enable Authentication
Duration: 05:00

This app will use Firebase Realtime Database to store all chat messages. Before we add data, we should make sure that the app is secure and that only authenticated users can post messages. In this step we will enable Firebase Authentication and configure Realtime Database Security Rules.

### Configure Firebase Authentication

Before your application can access the Firebase Authentication APIs on behalf of your users, you will have to enable it 

1. Navigate to the  [Firebase console](http://console.firebase.google.com) and select your project
2. Select **Authentication**
3. Select the **Sign In Method** tab
4. Toggle the **Google** switch to enabled (blue)
5. Set a support email.
6. Press **Save** on the resulting dialog

If you get errors later in this codelab with the message "CONFIGURATION_NOT_FOUND", come back to this step and double check your work.

### Configure Realtime Database

As mentioned, this app will store chat messages in Firebase Realtime Database. In this step we will create a database and configure the security via a JSON configuration language called Security Rules.

1. Go to your project in the Firebase console and select **Realtime Database** from the left navigation.
2. Click **Create Database** create a new Realtime Database instance and then select the `us-central1` region and click **Next**.
3. When prompted about security rules, choose **locked mode** and click **Enable**. 

Once the database has been created, select the **Rules** tab and update the rules configuration with the following:

```
{
  "rules": {
    "messages": {
      ".read": "auth.uid != null",
      ".write": "auth.uid != null"
    }
  }
}
```

Click "Publish" to publish the new rules.

For more information on how this works (including documentation on the "auth" variable) see the Firebase  [security documentation](https://firebase.google.com/docs/database/security/quickstart).


### Add basic sign-in functionality

Next we'll add some basic Firebase Authentication code to the app to detect users and implement a sign-in screen.


#### Check for current user

First add the following instance variable to the `MainActivity.kt` class:

**MainActivity.kt**

```
// Firebase instance variables
private lateinit var auth: FirebaseAuth
```

Now let's modify `MainActivity` to send the user to the sign-in screen whenever they open the app and are unauthenticated.  Add the following to the `onCreate()` method **after** the `binding` is attached to the view:

**MainActivity.kt**

```
// Initialize Firebase Auth and check if the user is signed in
auth = Firebase.auth
if (auth.currentUser == null) {
    // Not signed in, launch the Sign In activity
    startActivity(Intent(this, SignInActivity::class.java))
    finish()
    return
}
```

We also want to check if the user is signed in during `onStart()`:

**MainActivity.kt**

```
public override fun onStart() {
    super.onStart()
    // Check if user is signed in.
    if (auth.currentUser == null) {
        // Not signed in, launch the Sign In activity
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
        return
    }
}
```

Then implement the `getUserPhotoUrl()` and `getUserName()` methods to return the appropriate information about the currently authenticated Firebase user:

**MainActivity.kt**

```
private fun getPhotoUrl(): String? {
    val user = auth.currentUser
    return user?.photoUrl?.toString()
}

private fun getUserName(): String? {
    val user = auth.currentUser
    return if (user != null) {
        user.displayName
    } else ANONYMOUS
}
```

Then implement the `signOut()` method to handle the sign out button:

**MainActivity.kt**

```
private fun signOut() {
    auth.signOut()
    signInClient.signOut()
    startActivity(Intent(this, SignInActivity::class.java))
    finish()
}
```

Now we have all of the logic in place to send the user to the sign-in screen when necessary. Next we need to implement the sign-in screen to properly authenticate users.

#### Implement the Sign-In screen

Open the file `SignInActivity.kt`.  Here a simple Sign-In button is used to initiate authentication. In this step you will implement the logic to Sign-In with Google, and then use that Google account to authenticate with Firebase.

Add an Auth instance variable in the `SignInActivity` class under the `// Firebase instance variables` comment:

**SignInActivity.kt**

```
// Firebase instance variables
private lateinit var auth: FirebaseAuth
```

Then, edit the `onCreate()` method to initialize Firebase in the same way you did in `MainActivity`:

**SignInActivity.kt**

```
// Initialize FirebaseAuth
auth = Firebase.auth
```

Next, initiate signing in with Google. Update `signIn()` method to look like this:

**SignInActivity.kt**

```
private fun signIn() {
    val signInIntent = signInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
}
```

Next, implement the `onActivityResult()` method to handle the sign in result. If the result of the Google Sign-In was successful, use the account to authenticate with Firebase:

**SignInActivity.kt**

```
public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    // Result returned from launching the Intent in signIn()
    if (requestCode == RC_SIGN_IN) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }
}
```

Implement the `firebaseAuthWithGoogle()` method to authenticate with the signed in Google account:

**SignInActivity.kt**

```
private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
    val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
    auth.signInWithCredential(credential)
        .addOnSuccessListener(this) {
            // If sign in succeeds the auth state listener will be notified and logic to
            // handle the signed in user can be handled in the listener.
            Log.d(TAG, "signInWithCredential:success")
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }
        .addOnFailureListener(this) { e -> // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithCredential", e)
            Toast.makeText(
                this@SignInActivity, "Authentication failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
}
```

That's it! You've implemented authentication using Google as an Identity Provider in just a few method calls and without needing to manage any server-side configuration.

#### Test your work

Run the app on your device. You should be immediately sent to the sign-in screen. Tap the Google Sign-In button. You should then be sent to the messaging screen if everything worked well.


## Read Messages
Duration: 05:00

In this step we will add functionality to read and display messages stored in Realtime Database.

### Import Sample Messages

1. In the Firebase console, select **Realtime Database** from the left navigation menu.
2. In the overflow menu of the Data tab, select **Import JSON**. 
3. Browse to the initial_messages.json file in the root of the cloned repository, and select it. 
4. Click **Import**.

<img src="img/import-data.gif" />

### Read Data

#### Synchronize messages

In this section we add code that synchronizes newly added messages to the app UI by:

* Initializing the Firebase Realtime Database and adding a listener to handle changes made to the data.
* Updating the `RecyclerView` adapter so new messages will be shown.
* Adding the Database instance variables with your other Firebase instance variables in the `MainActivity` class:

#### MainActivity.kt

```
// Firebase instance variables
// ...
private lateinit var db: FirebaseDatabase
private lateinit var adapter: FriendlyMessageAdapter
```

Modify your MainActivity's `onCreate()` method under the comment `// Initialize Realtime Database and FirebaseRecyclerAdapter` with the code defined below. This code adds all existing messages from Realtime Database and then listens for new child entries under the `messages` path in your Firebase Realtime Database. It adds a new element to the UI for each message:

**MainActivity.kt**

```
// Initialize Realtime Database
db = Firebase.database
val messagesRef = db.reference.child(MESSAGES_CHILD)

// The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
// See: https://github.com/firebase/FirebaseUI-Android
val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
    .setQuery(messagesRef, FriendlyMessage::class.java)
    .build()
adapter = FriendlyMessageAdapter(options, getUserName())
binding.progressBar.visibility = ProgressBar.INVISIBLE
manager = LinearLayoutManager(this)
manager.stackFromEnd = true
binding.messageRecyclerView.layoutManager = manager
binding.messageRecyclerView.adapter = adapter

// Scroll down when a new message arrives
// See MyScrollToBottomObserver for details
adapter.registerAdapterDataObserver(
    MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
)
```

Next in the `FriendlyMessageAdapter.kt` class implement the `bind()` method within the inner class `MessageViewHolder()`:

**FriendlyMessageAdapter.kt**

```
inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
    fun bind(item: FriendlyMessage) {
        binding.messageTextView.text = item.text
        setTextColor(item.name, binding.messageTextView)

        binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }
    }
    ...
}
```

We also need to display messages that are images, so also implement the `bind()` method within the inner class `ImageMessageViewHolder()`:

**FriendlyMessageAdapter.kt**

```
inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
    ViewHolder(binding.root) {
    fun bind(item: FriendlyMessage) {
        loadImageIntoView(binding.messageImageView, item.imageUrl!!)

        binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }
    }
}
```

Finally, back in `MainActivity`, start and stop listening for updates from Firebase Realtime Database. 
Update the *`onPause()`* and *`onResume()`* methods in `MainActivity` as shown below:

**MainActivity.kt**

```
public override fun onPause() {
    adapter.stopListening()
    super.onPause()
}

public override fun onResume() {
    super.onResume()
    adapter.startListening()
}
```

### Test message sync

1. Click **Run** ( <img src="img/execute.png" alt="execute"  width="20.00" />).
2. In the Firebase console return to the **Realtime Database** section and manually add a new message with the ID `-ABCD`.
Confirm that the message shows up in your Android app:

<img src="img/add-message.gif" />

Congratulations, you just added a realtime database to your app!


## Send Messages
Duration: 05:00

### Implement text message sending

In this section, you will add the ability for app users to send text messages. The code snippet below listens for click events on the send button, creates a new `FriendlyMessage` object with the contents of the message field, and pushes the message to the database.  The `push()` method adds an automatically generated ID to the pushed object's path.  These IDs are sequential which ensures that the new messages will be added to the end of the list.  

Update the click listener of the send button in the `onCreate()` method in the `MainActivity` class. 
This code is at the bottom of the `onCreate()` method already. Update the `onClick()` body to match the code below:

**MainActivity.kt**

```
// Disable the send button when there's no text in the input field
// See MyButtonObserver for details
binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

// When the send button is clicked, send a text message
binding.sendButton.setOnClickListener {
    val friendlyMessage = FriendlyMessage(
        binding.messageEditText.text.toString(),
        getUserName(),
        getPhotoUrl(),
        null /* no image */
    )
    db.reference.child(MESSAGES_CHILD).push().setValue(friendlyMessage)
    binding.messageEditText.setText("")
}
```

### Implement image message sending

In this section, you will add the ability for app users to send image messages. Creating an image message is done with these steps:

* Select image
* Handle image selection
* Write temporary image message to the Realtime Database
* Begin to upload selected image
* Update image message URL to that of the uploaded image, once upload is complete

#### Select Image

To add images this codelab uses Cloud Storage for Firebase. Cloud Storage is a good place to store the binary data of your app.

In the Firebase console select **Storage** in the left navigation panel. Then click **Get Started** to enable Cloud Storage for your project.  Continue following the steps in the prompt, using the suggested defaults.

##### Handle image selection and write temp message

Once the user has selected an image, `startActivityForResult()` is called. This is already implemented in the code at the end of the `onCreate()` method. It launches the `MainActivity`'s `onActivityResult()` method. Using the code snippet below, you will write a message with a temporary image url to the database indicating the image is being uploaded.

**MainActivity.kt**

```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
    if (requestCode == REQUEST_IMAGE) {
        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data
            Log.d(TAG, "Uri: " + uri.toString())
            val user = auth.currentUser
            val tempMessage =
                FriendlyMessage(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
            db.reference.child(MESSAGES_CHILD).push()
                .setValue(
                    tempMessage,
                    DatabaseReference.CompletionListener { databaseError, databaseReference ->
                        if (databaseError != null) {
                            Log.w(
                                TAG, "Unable to write message to database.",
                                        databaseError.toException()
                                    )
                                    return@CompletionListener
                                }

                                // Build a StorageReference and then upload the file
                                val key = databaseReference.key
                                val storageReference = Firebase.storage
                                    .getReference(user!!.uid)
                                    .child(key!!)
                                    .child(uri!!.lastPathSegment!!)
                                putImageInStorage(storageReference, uri, key)
                            })
        }
    }
}
```

#### Upload image and update message

Add the method `putImageInStorage()` to `MainActivity`. It is called in `onActivityResult()` to initiate the upload of the selected image. Once the upload is complete you will update the message to use the appropriate image. 

#### MainActivity.kt

```
private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
    // First upload the image to Cloud Storage
    storageReference.putFile(uri)
        .addOnSuccessListener(
            this
        ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
            // and add it to the message.
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    val friendlyMessage =
                        FriendlyMessage(null, getUserName(), getPhotoUrl(), uri.toString())
                    db.reference
                        .child(MESSAGES_CHILD)
                        .child(key!!)
                        .setValue(friendlyMessage)
                }
        }
        .addOnFailureListener(this) { e ->
            Log.w(
                TAG,
                "Image upload task was unsuccessful.",
                e
            )
        }
}
```

#### Test Sending Messages

1. Click the  <img src="img/execute.png" alt="execute"  width="20.00" />**Run** button.
2. Enter a message and hit the send button, the new message should be visible in the app UI and in the Firebase console.
3. Tap the "+" image to select an image from your device. The new message should be visible first with a placeholder image, and then with the selected image once the image upload is complete. The new message should also be visible in the Firebase console, as an object in the Database and as a blob in Storage.


## Congratulations!
Duration: 01:00

You just built a real-time chat application using Firebase!

#### What you learned

* Firebase Authentication
* Firebase Realtime Database
* Cloud Storage for Firebase

Next try using what you learned to add Firebase to your own Android app! To learn more about Firebase visit [firebase.google.com](https://firebase.google.com)

