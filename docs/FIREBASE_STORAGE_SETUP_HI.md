# Firebase Storage setup

Automatic cloud backup चालू करने के लिए:

1. Firebase Console में इसी Android app वाला project खोलें।
2. Build > Storage > Get started दबाकर bucket बनाएँ।
3. Rules tab में project root की `firebase-storage.rules` का content लगाकर Publish करें।
4. Authentication > Sign-in method में Email/Password enabled रखें।
5. `app/google-services.json` इसी Firebase Android app और package `com.bharatbhushan.dailyexpensetracker` का होना चाहिए।

Backup path हर user के लिए `user-backups/{firebaseUid}/` है। Rules दूसरे user को इसे पढ़ने या लिखने नहीं देतीं।
