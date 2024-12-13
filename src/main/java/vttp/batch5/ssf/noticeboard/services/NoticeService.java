package vttp.batch5.ssf.noticeboard.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import vttp.batch5.ssf.noticeboard.models.Notice;

@Service
public class NoticeService {

	private final Logger logger = Logger.getLogger(NoticeService.class.getName());

	@Value("${notice.server.url}")
  	private String noticeServerUrl;

	public ResponseEntity<String> postToNoticeServer(Notice notice) {

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

			return resp;
		}

		catch (Exception e) {

			logger.info("Error occured: %s.".formatted(e.getMessage()));

			return resp;
		}	
	}	
}
