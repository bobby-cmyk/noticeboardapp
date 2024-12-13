package vttp.batch5.ssf.noticeboard.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.services.NoticeService;

// Use this class to write your request handlers

@Controller
@RequestMapping
public class NoticeController {

    @Autowired
    private NoticeService noticeSvc;
    
    private final Logger logger = Logger.getLogger(NoticeController.class.getName());
    
    @GetMapping
    public ModelAndView getNoticePage() {
        ModelAndView mav = new ModelAndView();

        Notice notice = new Notice();

        logger.info("A visitor has reached the notice page.");

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

        String statusWithMessage = noticeSvc.postToNoticeServer(notice);


        String[] parts = statusWithMessage.split(",");
        // Get the status

        String status = parts[0];    

        // If succesfully posted to server
        if (status.equals("success")) {

            logger.info("Successfully posted to server and saved response to Redis.");

            String id = parts[1];

            mav.addObject("id", id);
            mav.setViewName("view2");
            return mav;
        }

        else {

            String message = parts[1];

            mav.addObject("message", message);
            mav.setViewName("view3");
            return mav;
        }

       
    }
}
