package io.github.luizeldorado.secretbookmarks;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
	Context context;

	public WebServer(Context context) {
		super("localhost", 5000);
		this.context = context;
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();

		if (method == Method.GET) {
			Map<String, List<String>> query = session.getParameters();

			if (uri.equals("/")) {
				InputStream indexInputStream = context.getResources().openRawResource(R.raw.index);
				return newChunkedResponse(Response.Status.OK, "text/html", indexInputStream);
			} else if (uri.equals("/receive")) {
				String password = query.get("password").get(0);

				JSONObject data = Database.readFromFile(context, password);

				try {
					String jsonStr = new JSONObject()
							.put("data", data)
							.toString();

					return newFixedLengthResponse(jsonStr);
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			} else if (uri.equals("/add")) {
				String password = query.get("password").get(0);
				String url = query.get("url").get(0);

				try {
					JSONObject data = Database.readFromFile(context, password);
					data.getJSONArray("bookmarks")
							.put(new JSONObject()
									.put("url", url));

					Database.writeToFile(context, password, data);

					return newFixedLengthResponse("<!DOCTYPE html><script>window.close();</script>");
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (method == Method.POST) {
			if (uri.equals("/send")) {
				try {
					HashMap<String, String> map = new HashMap<>();
					session.parseBody(map);

					String jsonStr = map.get("postData");

					JSONObject json = new JSONObject(jsonStr);
					String password = json.getString("password");
					JSONObject data = json.getJSONObject("data");

					Database.writeToFile(context, password, data);

					return newFixedLengthResponse("ok");
				} catch (IOException | JSONException | ResponseException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "");
	}
}
