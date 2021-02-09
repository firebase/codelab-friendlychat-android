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
> **Note**: If you want to run the finished app, you have to create a project in the Firebase console corresponding to the package name and SHA1. See  [Step #3](https://codelabs.developers.google.com/codelabs/firebase-android/#2) for the command. Also you will have to enable Google as an Auth Provider; do this in the Authentication section of the Firebase console. 


### Import into Android Studio

In Android Studio click **File** > **Open** and  select the `build-android-start` directory ( <img src="img/android_studio_folder.png" alt="android_studio_folder"  width="20.00" />) from the directory where you downloaded the sample code.

You should now have the `build-android-start project` open in Android Studio. If you see a warning about a `google-services.json` file missing, don't worry. It will be added in the next step.


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
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.4.1'
    }
}
```

**app/build.gradle**

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.google.firebase.crashlytics'

android {
    // ...
}

dependencies {
    // ...

    // Google Sign In SDK
    implementation 'com.google.android.gms:play-services-auth:19.0.0'

    // Firebase SDK
    implementation platform('com.google.firebase:firebase-bom:26.4.0')
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-storage'
    implementation 'com.google.firebase:firebase-auth'

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

First add the following instance variables to `MainActivity.java`:

**MainActivity.java**

```
// Firebase instance variables
private FirebaseAuth mFirebaseAuth;
```

Now let's modify `MainActivity.java` to send the user to the sign-in screen whenever they open the app and are unauthenticated.  Add the following to the `onCreate` method **after** `mUsername` has been initialized:

**MainActivity.java**

```
// Initialize Firebase Auth and check if the user is signed in
mFirebaseAuth = FirebaseAuth.getInstance();
if (mFirebaseAuth.getCurrentUser() == null) {
    // Not signed in, launch the Sign In activity
    startActivity(new Intent(this, SignInActivity.class));
    finish();
    return;
}
```

Then implement the `getUserPhotoUrl()` amd `getUserName()` methods to return the appropriate information about the currently authenticated Firebase user:

**MainActivity.java**

```
@Nullable
private String getUserPhotoUrl() {
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    if (user != null && user.getPhotoUrl() != null) {
        return user.getPhotoUrl().toString();
    }

    return null;
}

private String getUserName() {
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    if (user != null) {
        return user.getDisplayName();
    }

    return ANONYMOUS;
}
```

Then implement the `signOut()` method to handle the sign out button:

**MainActivity.java**

```
private void signOut() {
    mFirebaseAuth.signOut();
    mSignInClient.signOut();
    startActivity(new Intent(this, SignInActivity.class));
    finish();
}
```

Now we have all of the logic in place to send the user to the sign-in screen when necessary. Next we need to implement the sign-in screen to properly authenticate users.

#### Implement the Sign-In screen

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

Next, initiate signing in with Google. Update `SignInActivity` `signIn()` method to look like this:

**SignInActivity.java**

```
private void signIn() {
    Intent signInIntent = mSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
}
```

Next, implement the `onActivityResult` method to `SignInActivity` to handle the sign in result. If the result of the Google Sign-In was successful, use the account to authenticate with Firebase:

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

Implement the `firebaseAuthWithGoogle` method to authenticate with the signed in Google account:

**SignInActivity.java**

```
private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mFirebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // If sign in succeeds the auth state listener will be notified and logic to 
                    // handle the signed in user can be handled in the listener.
                    Log.d(TAG, "signInWithCredential:success");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential", e);
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
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

#### MainActivity.java

```
// Firebase instance variables
// ...
private FirebaseDatabase mDatabase;
private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
```

Modify your MainActivity's `onCreate` method by replacing *`mProgressBar.setVisibility(ProgressBar.INVISIBLE);`* with the code defined below. This code initially adds all existing messages and then listens for new child entries under the messages path in your Firebase Realtime Database. It adds a new element to the UI for each message:

**MainActivity.java**

```
// Initialize Realtime Database
mDatabase = FirebaseDatabase.getInstance();
DatabaseReference messagesRef = mDatabase.getReference().child(MESSAGES_CHILD);

// The FirebaseRecyclerAdapter class comes from the FirebaseUI library
// See: https://github.com/firebase/FirebaseUI-Android
FirebaseRecyclerOptions<FriendlyMessage> options =
        new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(messagesRef, FriendlyMessage.class)
                .build();

mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(MessageViewHolder vh, int position, FriendlyMessage message) {
        mBinding.progressBar.setVisibility(ProgressBar.INVISIBLE);
        vh.bindMessage(message);
    }
};

mLinearLayoutManager = new LinearLayoutManager(this);
mLinearLayoutManager.setStackFromEnd(true);
mBinding.messageRecyclerView.setLayoutManager(mLinearLayoutManager);
mBinding.messageRecyclerView.setAdapter(mFirebaseAdapter);

// Scroll down when a new message arrives
// See MyScrollToBottomObserver.java for details
mFirebaseAdapter.registerAdapterDataObserver(
        new MyScrollToBottomObserver(mBinding.messageRecyclerView, mFirebaseAdapter, mLinearLayoutManager));
```

Next in the `MessageViewHolder` class implement the `bindMessage()` method:

**MessageViewHolder.java**

```
public void bindMessage(FriendlyMessage friendlyMessage) {
    if (friendlyMessage.getText() != null) {
        messageTextView.setText(friendlyMessage.getText());
        messageTextView.setVisibility(TextView.VISIBLE);
        messageImageView.setVisibility(ImageView.GONE);
    } else if (friendlyMessage.getImageUrl() != null) {
        String imageUrl = friendlyMessage.getImageUrl();
        if (imageUrl.startsWith("gs://")) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(imageUrl);

            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            Glide.with(messageImageView.getContext())
                                    .load(downloadUrl)
                                    .into(messageImageView);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Getting download url was not successful.", e);
                        }
                    });
        } else {
            Glide.with(messageImageView.getContext())
                    .load(friendlyMessage.getImageUrl())
                    .into(messageImageView);
        }

        messageImageView.setVisibility(ImageView.VISIBLE);
        messageTextView.setVisibility(TextView.GONE);
    }
}
```

Finally, back in `MainActivity`,start and stop listening for updates from Firebase Realtime Database. 
Update the *`onPause`* and *`onResume`* methods in `MainActivity` as shown below:

**MainActivity.java**

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

Update the click listener of the send button in the `onCreate` method in the `MainActivity` class. 
This code is at the bottom of the `onCreate` method already. Update the `onClick` body to match the code below:

**MainActivity.java**

```
// Disable the send button when there's no text in the input field
// See MyButtonObserver.java for details
mBinding.messageEditText.addTextChangedListener(new MyButtonObserver(mBinding.sendButton));

// When the send button is clicked, send a text message
mBinding.sendButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FriendlyMessage friendlyMessage = new
                FriendlyMessage(mBinding.messageEditText.getText().toString(),
                getUserName(),
                getUserPhotoUrl(),
                null /* no image */);

        mDatabase.getReference().child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mBinding.messageEditText.setText("");
    }
});
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

With the following code snippet you will allow the user to select an image from the device's local storage. Update the click listener of `addMessageImageView` in the `onCreate` method in the `MainActivity` class. This code is at the bottom of the `onCreate` method already. Update the `onClick` body to match the code below:

#### MainActivity.java

```
// When the image button is clicked, launch the image picker
mBinding.addMessageImageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }
});
```

##### Handle image selection and write temp message

Once the user has selected an image, a call to the `MainActivity`'s `onActivityResult` will be fired. This is where you handle the user's image selection. Using the code snippet below, add the `onActivityResult` method to `MainActivity`. In this function you will write a message with a temporary image url to the database indicating the image is being uploaded.

**MainActivity.java**

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

    if (requestCode == REQUEST_IMAGE) {
        if (resultCode == RESULT_OK && data != null) {
            final Uri uri = data.getData();
            Log.d(TAG, "Uri: " + uri.toString());

            final FirebaseUser user = mFirebaseAuth.getCurrentUser();
            FriendlyMessage tempMessage = new FriendlyMessage(
                    null, getUserName(), getUserPhotoUrl(), LOADING_IMAGE_URL);

            mDatabase.getReference().child(MESSAGES_CHILD).push()
                    .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.w(TAG, "Unable to write message to database.",
                                        databaseError.toException());
                                return;
                            }

                            // Build a StorageReference and then upload the file
                            String key = databaseReference.getKey();
                            StorageReference storageReference =
                                    FirebaseStorage.getInstance()
                                            .getReference(user.getUid())
                                            .child(key)
                                            .child(uri.getLastPathSegment());

                            putImageInStorage(storageReference, uri, key);
                        }
                    });
        }
    }
}
```

#### Upload image and update message

Add the method `putImageInStorage` to `MainActivity`. It is called in `onActivityResult` to initiate the upload of the selected image. Once the upload is complete you will update the message to use the appropriate image. 

#### MainActivity.java

```
private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
    // First upload the image to Cloud Storage
    storageReference.putFile(uri)
            .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // After the image loads, get a public downloadUrl for the image
                    // and add it to the message.
                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FriendlyMessage friendlyMessage = new FriendlyMessage(
                                            null, getUserName(), getUserPhotoUrl(), uri.toString());
                                    mDatabase.getReference()
                                            .child(MESSAGES_CHILD)
                                            .child(key)
                                            .setValue(friendlyMessage);
                                }
                            });
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Image upload task was not successful.", e);
                }
            });
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

Next try using what you learned to add Firebase to your own Android app! To learn more about Firebae visit [firebase.google.com](https://firebase.google.com)

