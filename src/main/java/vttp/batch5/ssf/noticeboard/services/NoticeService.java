package vttp.batch5.ssf.noticeboard.services;

import java.io.StringReader;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.models.Response;
import vttp.batch5.ssf.noticeboard.repositories.NoticeRepository;

@Service
public class NoticeService {

	private final Logger logger = Logger.getLogger(NoticeService.class.getName());

	@Autowired
	private NoticeRepository noticeRepo;

	@Value("${notice.server.url}")
  	private String noticeServerUrl;

	public Response postToNoticeServer(Notice notice) {

		// Build URL
		String url = noticeServerUrl + "/notice";

		JsonArrayBuilder builderArr = Json.createArrayBuilder();

		for (String category : notice.getCategories()) {
			builderArr.add(category);
		}
		
		// Build Payload
		JsonObject reqBody = Json.createObjectBuilder()
			.add("title", notice.getTitle())
			.add("poster", notice.getPoster())
			.add("postDate", notice.getPostDateEpoch())
			.add("categories", builderArr)
			.add("text", notice.getText())
			.build();

		// Build Request Entity
		RequestEntity<String> req = RequestEntity
			.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(reqBody.toString(), String.class);

		// RestTemplate to exchange for response
		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = null;

		// Initialise a response to be returned
		Response response = new Response();

		try {
			resp = template.exchange(req, String.class);

			String payload = resp.getBody();

			JsonReader reader = Json.createReader(new StringReader(payload));

			JsonObject respObj = reader.readObject();

			HttpStatusCode statusCode = resp.getStatusCode();
			
			if (statusCode.is2xxSuccessful()) {
				String id = respObj.getString("id");
				
				noticeRepo.insertNotices(id, respObj.toString());

				response.setStatusCode(statusCode);
				response.setContent(id);

				return response;
			}

			// If submission is not succesful
			else{
				String message = respObj.getString("message");

				response.setStatusCode(statusCode);
				response.setContent(message);
				
				return response;
			}
		}

		catch (Exception e) {
			// Set to 500 to avoid null
			response.setStatusCode(HttpStatusCode.valueOf(500));
			response.setContent(e.getMessage());

			return response;
		}	
	}

	public void checkRedisHealth() throws Exception {
		noticeRepo.getRandomKey();
	}
}
