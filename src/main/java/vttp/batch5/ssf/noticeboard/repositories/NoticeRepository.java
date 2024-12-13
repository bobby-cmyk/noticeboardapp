package vttp.batch5.ssf.noticeboard.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepository {

	@Autowired @Qualifier("notice")
	private RedisTemplate<String, Object> template;

	// set id respString
	public void insertNotices(String id, String respString) {
		template.opsForValue().set(id, respString);
	}

	// randomkey
	public void getRandomKey() throws Exception{
		template.randomKey();
	}
}
