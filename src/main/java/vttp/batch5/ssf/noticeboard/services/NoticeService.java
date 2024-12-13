package vttp.batch5.ssf.noticeboard.services;

import java.io.StringReader;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import vttp.batch5.ssf.noticeboard.repositories.NoticeRepository;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepo;

	private final Logger logger = Logger.getLogger(NoticeService.class.getName());

	@Value("${notice.server.url}")
  	private String noticeServerUrl;

	public String postToNoticeServer(Notice notice) {

		// 1. URL
		String url = noticeServerUrl + "/notice";

		JsonArrayBuilder builderArr = Json.createArrayBuilder();

		for (String category : notice.getCategories()) {
			builderArr.add(category);
		}
		
		// 2. Payload
		JsonObject reqBody = Json.createObjectBuilder()
			.add("title", notice.getTitle())
			.add("poster", notice.getPoster())
			.add("postDate", notice.getPostDateEpoch())
			.add("categories", builderArr)
			.add("text", notice.getText())
			.build();

		// 3. Request Entity
		RequestEntity<String> req = RequestEntity
			.post(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(reqBody.toString(), String.class);
		
		logger.info("Request:\n %s \n".formatted(req.toString()));

		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = null;

		try {
			resp = template.exchange(req, String.class);

			if (resp.getStatusCode().is2xxSuccessful()) {

				String payload = resp.getBody();

				JsonReader reader = Json.createReader(new StringReader(payload));

				JsonObject respObj = reader.readObject();

				String id = respObj.getString("id");
				
				noticeRepo.insertNotices(id, respObj.toString());

				String statusWithMessage = "success" + "," + id;

				return statusWithMessage;
			}

			else{
				// If submission is not succesful
				String payload = resp.getBody();

				JsonReader reader = Json.createReader(new StringReader(payload));

				JsonObject respObj = reader.readObject();

				String message = respObj.getString("message");

				String statusWithMessage = "notsuccess" + "," + message;
				
				return statusWithMessage;
			}
		}

		catch (Exception e) {

			logger.info("Error occured: %s.".formatted(e.getMessage()));

			String statusWithMessage = "notsuccess" + "," + e.getMessage();

			return statusWithMessage;
		}	
	}

	public void checkRedisHealth() throws Exception {
		noticeRepo.getRandomKey();
	}
}
