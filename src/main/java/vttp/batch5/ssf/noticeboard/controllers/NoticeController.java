package vttp.batch5.ssf.noticeboard.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.models.Response;
import vttp.batch5.ssf.noticeboard.services.NoticeService;

@Controller
@RequestMapping
public class NoticeController {

    private final Logger logger = Logger.getLogger(NoticeController.class.getName());

    @Autowired
    private NoticeService noticeSvc;
    
    @GetMapping
    public ModelAndView getNoticePage() {
        logger.info("A visitor has reached the notice page.");

        ModelAndView mav = new ModelAndView();

        Notice notice = new Notice();

        mav.addObject("notice", notice);
        mav.setViewName("notice");

        return mav;
    }

    @PostMapping("/notice")
    public ModelAndView postNotice(
        @Valid Notice notice,
        BindingResult bindings
    ) 
    {
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {

            logger.info("Invalid input for notice form.");

            mav.setViewName("notice");
            return mav;
        }

        Response response = noticeSvc.postToNoticeServer(notice);  

        HttpStatusCode statusCode = response.getStatusCode();

        // If succesfully posted to server
        if (statusCode.is2xxSuccessful()) {

            String id = response.getContent();

            logger.info("Successfully posted to server and saved id: %s to Redis.".formatted(id));

            mav.addObject("id", id);
            mav.setViewName("view2");

            return mav;
        }

        else {
            String message = response.getContent();

            logger.info("Unsuccessful when posting to server, error message: %s".formatted(message));

            mav.addObject("message", message);
            mav.setViewName("view3");

            return mav;
        }
    }

    @GetMapping(path={"/status"}, produces="application/json")
    @ResponseBody
    public ResponseEntity<String> getHealthStatus() {
        try {
            noticeSvc.checkRedisHealth();
            logger.info("System is healthy");
            return ResponseEntity.ok("{}");
        }

        catch (Exception ex) {
            logger.info("System is unhealthy");
            return ResponseEntity.status(503).body("{}");
        }
    }
}