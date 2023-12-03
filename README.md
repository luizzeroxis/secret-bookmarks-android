# Secret bookmarks app for Android

Server that stores bookmarks so they are not in the browser, and it works in incognito mode.

# Build

- Open the folder with [Android Studio](https://developer.android.com/studio) and click Build > Make Project. Follow on-screen instructions.

# Help

- The "Serve server" switch starts up a local HTTP server at the port 5000 (`http://localhost:5000`).
- You can enter the password as a URI fragment (`http://localhost:5000/#\<your password here>`).
- You can add bookmarks through the /add endpoint, with 'password' and 'url' URI query parameters (`http://localhost:5000/add?password=\<your password here>&url=\<your url here>`).
