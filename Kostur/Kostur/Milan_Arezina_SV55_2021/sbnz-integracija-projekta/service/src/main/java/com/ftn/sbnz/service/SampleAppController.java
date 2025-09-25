package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.StressScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class SampleAppController {
    private static Logger log = LoggerFactory.getLogger(SampleAppController.class);

    private final SampleAppService sampleService;

    @Autowired
    public SampleAppController(SampleAppService sampleService) {
        this.sampleService = sampleService;
    }

    @RequestMapping(value = "/item", method = RequestMethod.GET)
    public StressScore getQuestions() {

        return sampleService.getClassifiedItem();
    }



}
