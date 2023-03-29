package com.techreturners.GraphAPI.controllers;

import com.microsoft.graph.requests.CalendarCollectionPage;
import com.techreturners.GraphAPI.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/api/v1/calendar")
public class MSGraphController {

    @Autowired
    Graph graph;

    @GetMapping("/")
    public CalendarCollectionPage getCalendarList() throws GeneralSecurityException {
        return Graph.getListOfCalendars();
    }

}
