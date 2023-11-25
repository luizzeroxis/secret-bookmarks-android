package io.github.luizeldorado.secretbookmarks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Database {
	static JSONObject readFromFile(Context context, String password) {
		try {
			String filename = getPasswordFilename(password);

			File file = new File(context.getFilesDir(), filename);
			if (!file.exists()) {
				JSONObject json = new JSONObject()
						.put("text", "")
						.put("bookmarks", new JSONArray());
				writeToFile(context, password, json);
			}

			FileInputStream fis = context.openFileInput(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
			StringBuilder stringBuilder = new StringBuilder();

			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = reader.readLine();
			while (line != null) {
				stringBuilder.append(line).append('\n');
				line = reader.readLine();
			}

			String contents = stringBuilder.toString();
			return new JSONObject(contents);
		} catch (IOException | JSONException e) {
			throw new RuntimeException(e);
		}
	}

	static void writeToFile(Context context, String password, JSONObject data) {
		try {
			String filename = getPasswordFilename(password);
			String contents = data.toString();

			try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
				fos.write(contents.getBytes(StandardCharsets.UTF_8));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static String getPasswordFilename(String password) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(password.getBytes());
			byte[] bytes = messageDigest.digest();
			String hash = new BigInteger(1, bytes).toString(16);
			return "data-" + hash + ".json";
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
